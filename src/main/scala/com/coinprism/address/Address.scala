package com.coinprism.address

import scala.concurrent.Future
import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.client.pipelining._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.json.JsValue
import spray.json.JsObject
import spray.json.JsNumber
import spray.json.JsString
import spray.json.RootJsonFormat

case class Address(value: String)
case class AssetBalance(id: String, balance: Long, unconfirmed_balance: Long)
case class AddressBalance(asset_address: String, bitcoin_address: String,
  issuable_asset: Option[String],
  balance: Long, unconfirmed_balance: Long, assets: List[AssetBalance])

object AssetBalanceProtocol extends DefaultJsonProtocol {
  implicit val assetBalanceFormat = jsonFormat3(AssetBalance)
  implicit object AddressBalanceFormat extends RootJsonFormat[AssetBalance] {
    override def read(value: JsValue): AssetBalance = {

      val Seq(id, balance, unconfirmed_balance) =
        value.asJsObject.getFields("id", "balance", "unconfirmed_balance")
      AssetBalance(id.toString, balance.convertTo[String].toLong, 
          unconfirmed_balance.convertTo[String].toLong)
    }

    override def write(assetBalance: AssetBalance): spray.json.JsValue = {
      val m: Map[String, JsValue] = Map("id" -> JsString(assetBalance.id),
        "balance" -> JsString(assetBalance.balance.toString),
        "unconfirmed_balance" -> JsString(assetBalance.unconfirmed_balance.toString))
      JsObject(m)
    }
  }

}
object AddressBalanceProtocol extends DefaultJsonProtocol {
  import AssetBalanceProtocol._
  implicit val addressBalanceFormat = jsonFormat6(AddressBalance)
  //TODO: parse bitcoin address into the Address case class
}

object Address {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  import AddressBalanceProtocol._
  import AssetBalanceProtocol._
  def getAddress(address: Address): Future[AddressBalance] = {
    val pipeline: HttpRequest => Future[AddressBalance] = sendReceive ~> unmarshal[AddressBalance]
    val host = "https://api.coinprism.com"
    val path = "/v1/addresses/"
    pipeline(Get(host + path + address.value))

  }

}
