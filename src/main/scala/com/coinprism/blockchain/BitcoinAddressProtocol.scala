package com.coinprism.blockchain

import org.scalacoin.protocol.{AssetAddress, Address, BitcoinAddress}
import spray.json.DefaultJsonProtocol
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.JsObject

object BitcoinAddressProtocol extends DefaultJsonProtocol {

  implicit val bitcoinAddressFormat = jsonFormat1(BitcoinAddress.apply _)

}

object AddressProtocol extends DefaultJsonProtocol {

  implicit object AddressFormat extends RootJsonFormat[Address] {
    override def read(jsValue: JsValue) = {
      jsValue match {
        case JsString(string) => string match {
          case s if s(0) == 'a' => AssetAddress(s)
          case s if s(0) == '1' || s(0) == '3' => BitcoinAddress(s)
          case _ => throw new RuntimeException("Addresses should always start with 'a' '1' or '3'")
        }
        case _ => throw new RuntimeException("Addresses should always be reprsented by a JsString")
      }
    }

    override def write(address: Address) = {
      val m: Map[String, JsValue] = Map("address" -> JsString(address.value))
      JsObject(m)
    }
  }
}

