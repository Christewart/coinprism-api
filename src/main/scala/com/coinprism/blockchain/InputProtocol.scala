package com.coinprism.blockchain

import org.scalacoin.protocol.BitcoinAddress
import spray.json.DefaultJsonProtocol
import spray.json.JsValue
import spray.json.JsObject
import spray.json.RootJsonFormat
import spray.json.JsString
import spray.json.JsNumber
import spray.json.JsArray
import scala.collection.breakOut
import spray.json.JsNull

case class Input(transaction_hash: Option[String], output_hash: String, output_index: Long,
  value: Long, addresses: List[BitcoinAddress], script_signature: String, asset_id: Option[String],
  asset_quantity: Option[Long])
object InputProtocol extends DefaultJsonProtocol {
  import BitcoinAddressProtocol._
  implicit object InputFormat extends RootJsonFormat[Input] {
    override def read(jsValue: JsValue): Input = {
      val obj = jsValue.asJsObject
      val Seq(output_hash, output_index, value, addresses,
        script_signature) =
        obj.getFields("output_hash", "output_index",
          "value", "addresses", "script_signature")

      val transactionHash: Option[String] = obj.fields.contains("transaction_hash") match {
        case true => obj.fields("transaction_hash") match {
          case JsString(s) => Some(s)
          case _ => None
        }
        case false => None
      }
      val convertedAssetQuantity : Option[Long] = if (obj.fields.contains("asset_quantity"))
        {
          obj.fields("asset_quantity") match {
            case JsString(s) => Some(s.toLong)
            case _ => None
        }
      } else {
        None
      }
      // convert JsArray to List[ BitcoinAdress ]
      val addressList = addresses match {
        case ja: JsArray => {
          ja.elements.toList.map(e => BitcoinAddress(e.convertTo[String]))
        }
        case _ => throw new RuntimeException("This Json type is not valid for parsing a list of bitcoin addresses")
      }
      val asset_id : Option[String] = if (obj.fields.contains("asset_id")) {
        obj.fields("asset_quantity") match {
          case JsString(s) => Some(obj.fields("asset_id").convertTo[String])
          case _ => None
        }
      } else None

      Input(transactionHash, output_hash.convertTo[String], output_index.convertTo[Long],
        value.convertTo[Long], addressList, script_signature.convertTo[String],
        asset_id, convertedAssetQuantity)
    }

    override def write(input: Input): spray.json.JsValue = {
      val addresses = input.addresses.map(p =>
        bitcoinAddressFormat.write(p))(breakOut): Vector[JsValue]

      val m: Map[String, JsValue] = Map("transaction_hash" -> (input.transaction_hash match {
        case Some(t) => JsString(t)
        case None => JsNull
      }),

        "output_hash" -> JsString(input.output_hash),
        "output_index" -> JsNumber(input.output_index),
        "value" -> JsNumber(input.value),
        "addresses" -> JsArray(addresses),
        "script_signature" -> JsString(input.script_signature),
        "asset_id" -> (input.asset_id match {
          case Some(s) => JsString(s)
          case None => JsNull
        }),
        "asset_quantity" -> (input.asset_quantity match {
          case Some(n) => JsNumber(n)
          case None => JsNull
        }))
      JsObject(m)
    }
  }
}