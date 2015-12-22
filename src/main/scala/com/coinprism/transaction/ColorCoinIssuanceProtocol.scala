package com.coinprism.transaction

import org.scalacoin.protocol.{AssetAddress, BitcoinAddress}
import spray.json._

import com.coinprism.blockchain.BitcoinAddressProtocol
import com.coinprism.blockchain.AssetAddressProtocol
case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: AssetAddress,
  amount: Long, metadata: String)
object ColorCoinIssuanceProtocol extends DefaultJsonProtocol {

  import BitcoinAddressProtocol._
  import AssetAddressProtocol._
  implicit object ColorCoinIssuanceFormat extends RootJsonFormat[ColorCoinIssuance] {

    override def read(jsValue : JsValue) = {
      val obj = jsValue.asJsObject
      val Seq(fees,from,address,amount,metadata) = obj.getFields("fees","from",
        "address", "amount", "metadata")
      ColorCoinIssuance(fees.convertTo[Long],
        BitcoinAddress(from.convertTo[String]), AssetAddress(address.convertTo[String]),
        amount.convertTo[Long], metadata.convertTo[String])
    }

    override def write(issuance : ColorCoinIssuance) = {

      val fees = JsNumber(issuance.fees)
      val from = JsString(issuance.from.value)
      val address = JsString(issuance.address.value)
      val amount = JsNumber(issuance.amount)
      val metadata = JsString(issuance.metadata)

      val m : Map[String,JsValue] = Map ("fees" -> fees, "from"-> from,
        "address" -> address, "amount" -> amount, "metadata" -> metadata)
      JsObject(m)

    }
  }
}