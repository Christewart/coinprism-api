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
import spray.json.JsArray
import collection.breakOut
abstract class Address(val value: String)
case class BitcoinAddress(override val value: String) extends Address(value)
case class AssetAddress(override val value: String) extends Address(value)

object  AddressRequest {

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
