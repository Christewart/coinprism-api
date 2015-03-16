package com.coinprism.transaction

import com.coinprism.blockchain.BitcoinAddress
import spray.json._
import com.coinprism.blockchain.BitcoinAddressProtocol

import scala.collection.breakOut

case class NewBitcoinTransaction(fees: Long, from: BitcoinAddress,
                                 to: List[BitcoinDestination])
object NewBitcoinTransactionProtocol extends DefaultJsonProtocol {

  import BitcoinDestinationProtocol._
  import BitcoinAddressProtocol._
  implicit val newBitcoinTransactionFormat = jsonFormat3(NewBitcoinTransaction)

  implicit object NewBitcoinTransactionFormat extends RootJsonFormat[NewBitcoinTransaction] {

    override def read(jsValue : JsValue) = {
      val obj = jsValue.asJsObject
      val Seq(fees,fromAddress, to) = obj.getFields("fees","from", "to")

      throw new RuntimeException("Reading new bitcoin transactions is not implemented" +
        "currently in this coinrpism api")
    }

    override def write(newTx : NewBitcoinTransaction) = {
      val bitcoinDestination = newTx.to.map(p =>
        BitcoinDestinationFormat.write(p))(breakOut): Vector[JsValue]

      val m : Map[String,JsValue]  = Map("fees"-> JsNumber(newTx.fees),
        "from" -> JsString(newTx.from.value),
        "to" -> JsArray(bitcoinDestination))
      JsObject(m)
    }
  }
}