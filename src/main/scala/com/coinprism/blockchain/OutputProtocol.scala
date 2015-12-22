package com.coinprism.blockchain

import org.scalacoin.protocol.BitcoinAddress
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject
import spray.json.JsArray
import spray.json.JsNumber
import spray.json.JsNull
import scala.collection.breakOut

case class Output(transaction_hash: Option[String], index: Long, value: Long, addresses: List[BitcoinAddress],
  script: String, asset_id: Option[String], asset_quantity: Option[Long])
object OutputProtocol extends DefaultJsonProtocol {
  import BitcoinAddressProtocol._

  implicit object OutputFormat extends RootJsonFormat[Output] {

    override def read(jsValue: JsValue): Output = {
      val obj = jsValue.asJsObject
      val Seq(index, value, addresses,
        script, asset_id, asset_quantity) =
        obj.getFields("index", "value", "addresses", "script", "asset_id", "asset_quantity")

      val convertedAssetQuantity = asset_quantity match {
        case JsString(s) => Some(s.toLong)
        case _ => None
      }
      val transactionHash: Option[String] = obj.fields.contains("transaction_hash") match {
        case true => obj.fields("transaction_hash") match {
          case JsString(s) => Some(s)
          case _ => None
        }
        case false => None
      }
      // convert JsArray to List[ BitcoinAdress ]
      val addressList = addresses match {
        case ja: JsArray => {
          ja.elements.toList.map(e => BitcoinAddress(e.convertTo[String]))
        }
        case _ => throw new RuntimeException("This Json type is not valid for parsing a list of bitcoin addresses")
      }
      Output(transactionHash, index.convertTo[Long],
        value.convertTo[Long], addressList, script.convertTo[String],
        asset_id.convertTo[Option[String]], convertedAssetQuantity)
    }
    override def write(output: Output) = {
      val addresses = output.addresses.map(p =>
        bitcoinAddressFormat.write(p))(breakOut): Vector[JsValue]
      val m: Map[String, JsValue] = Map("transaction_hash" -> (output.transaction_hash match {
        case Some(t) => JsString(t)
        case None => JsNull
      }),
        "index" -> JsNumber(output.index),
        "value" -> JsNumber(output.value),
        "addresses" -> JsArray(addresses),
        "script" -> JsString(output.script),
        "asset_id" -> (output.asset_id match {
          case Some(s) => JsString(s)
          case None => JsNull
        }),
        "asset_quantity" -> (output.asset_quantity match {
          case Some(n) => JsNumber(n)
          case None => JsNull
        }))
      JsObject(m)
    }
  }
}