package com.coinprism.transaction


import com.coinprism.blockchain.{BitcoinAddressProtocol}
import org.scalacoin.protocol.{AssetAddress, BitcoinAddress}
import spray.json.{JsValue, JsObject, DefaultJsonProtocol, RootJsonFormat, JsNumber, JsString,
JsArray}

import scala.collection.breakOut
import scala.collection.immutable.Map

case class NewAssetTransaction(fees: Long, from: BitcoinAddress, to: List[AssetDestination])
object NewAssetTransactionProtocol extends DefaultJsonProtocol {
  import AssetDestinationProtocol._
  import BitcoinAddressProtocol._

  implicit val newAssetTransactionFormat = jsonFormat3(NewAssetTransaction)

  implicit object NewAssetTransactionProtocol extends RootJsonFormat[NewAssetTransaction] {


    override def read(jsValue : JsValue) = {
      val obj = jsValue.asJsObject
      val Seq(fees, from, to) = obj.getFields("fees", "from", "to")


      // convert JsArray to List[ AssetDestination ]
      val toList : List[AssetDestination] = to match {
        case ja: JsArray => {
          ja.elements.toList.map(e => AssetDestination(
            AssetAddress(e.asJsObject.fields("address").convertTo[String]),
            e.asJsObject.fields("amount").convertTo[Long],
            e.asJsObject.fields("asset_id").convertTo[String]))
        }
        case _ => throw new RuntimeException("NewAssetTransactionFormat only accepts JsArrays for to")

      }
      NewAssetTransaction(fees.convertTo[Long], BitcoinAddress(from.convertTo[String]), toList)
    }

    override def write(assetTx : NewAssetTransaction) : JsValue = {

      import AssetDestinationProtocol._

      val assetDestination = assetTx.to.map(p =>
        AssetAddressFormat.write(p))(breakOut): Vector[JsValue]

      val m : Map[String, JsValue] = Map("fees" -> JsNumber(assetTx.fees),
          "from" -> JsString(assetTx.from.value),
          "to" -> JsArray(assetDestination)
        )
      JsObject(m)
    }
  }
}
