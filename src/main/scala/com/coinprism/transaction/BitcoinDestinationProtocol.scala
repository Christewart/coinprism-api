package com.coinprism.transaction

import org.scalacoin.protocol.BitcoinAddress
import spray.json._
import com.coinprism.blockchain.BitcoinAddressProtocol

case class BitcoinDestination(address : BitcoinAddress, amount : Long)
object BitcoinDestinationProtocol extends DefaultJsonProtocol {
  import BitcoinAddressProtocol._

  implicit object BitcoinDestinationFormat extends RootJsonFormat[BitcoinDestination] {

    override def read(jsValue : JsValue)  = {
      val obj = jsValue.asJsObject
      val Seq(address,amount) = obj.getFields("address","amount")
      BitcoinDestination(BitcoinAddress(address.convertTo[String]),
        amount.convertTo[Long])
    }

    override def write(dest : BitcoinDestination) = {
      val m : Map[String,JsValue] = Map("address" -> JsString(dest.address.value),
        "amount" -> JsNumber(dest.amount)
      )
      JsObject(m)
    }
  }
}