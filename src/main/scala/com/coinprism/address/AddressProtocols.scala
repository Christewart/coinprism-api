package com.coinprism.address

import spray.json.DefaultJsonProtocol
import spray.json.JsObject
import spray.json.JsArray
import spray.json.RootJsonFormat
import spray.json.JsString
import spray.json.JsNumber
import spray.json.JsValue
import collection.breakOut

case class AssetBalance(id: String, balance: Long, unconfirmed_balance: Long)

case class AddressBalance(asset_address: AssetAddress, bitcoin_address: BitcoinAddress,
  issuable_asset: Option[String], balance: Long, unconfirmed_balance: Long,
  assets: List[AssetBalance])

object AssetBalanceProtocol extends DefaultJsonProtocol {
  implicit val assetBalanceFormat = jsonFormat3(AssetBalance)
  implicit object AssetBalanceFormat extends RootJsonFormat[AssetBalance] {
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
  implicit object AddressBalanceFormat extends RootJsonFormat[AddressBalance] {
    override def read(value: JsValue): AddressBalance = {
      val Seq(asset_address, bitcoin_address, issuable_asset, balance, unconfirmed_balance, assets) =
        value.asJsObject.getFields("asset_address", "bitcoin_address", "issuable_asset",
          "balance", "unconfirmed_balance", "assets")
      if (issuable_asset.toString == "") {
        AddressBalance(AssetAddress(asset_address.convertTo[String]),
          BitcoinAddress(bitcoin_address.convertTo[String]),
          None, balance.convertTo[Long], unconfirmed_balance.convertTo[Long],
          assets.convertTo[List[AssetBalance]])
      } else {
        AddressBalance(AssetAddress(asset_address.convertTo[String]),
          BitcoinAddress(bitcoin_address.convertTo[String]),
          Some(issuable_asset.convertTo[String]), balance.convertTo[Long], unconfirmed_balance.convertTo[Long],
          assets.convertTo[List[AssetBalance]])
      }

    }

    override def write(addressBalance: AddressBalance): JsValue = {

      val assets = addressBalance.assets.map(p =>
        AssetBalanceProtocol.AssetBalanceFormat.write(p))(breakOut): Vector[JsValue]

      val m: Map[String, JsValue] = Map("asset_address" ->
        JsString(addressBalance.asset_address.value),
        "bitcoin_address" -> JsString(addressBalance.bitcoin_address.value),
        "issuable_asset" -> JsString(addressBalance.issuable_asset.getOrElse("")),
        "balance" -> JsNumber(addressBalance.balance),
        "unconfirmed_balance" -> JsNumber(addressBalance.unconfirmed_balance),
        "assets" -> JsArray(assets))
      JsObject(m)
    }
  }
  //implicit val addressBalanceFormat = jsonFormat6(AddressBalance)
  //TODO: parse bitcoin address into the Address case class
}