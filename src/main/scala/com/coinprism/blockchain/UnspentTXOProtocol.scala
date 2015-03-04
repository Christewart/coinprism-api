package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol
import spray.json.JsObject
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import collection.breakOut
import spray.json.JsArray
import spray.json.JsNumber
import spray.json.JsBoolean
import spray.json.JsNull
import com.coinprism.blockchain.BitcoinAddressProtocol.bitcoinAddressFormat

case class UnspentTXO(transaction_hash: String, output_index: Int, value: Long,
  asset_id: Option[String], asset_quantity: Option[Long], addresses: List[BitcoinAddress],
  script_hex: String, spent: Boolean)
object UnspentTXOProtocol extends DefaultJsonProtocol {
  import BitcoinAddressProtocol._

  implicit object UnspentTXOProtocolFormat extends RootJsonFormat[UnspentTXO] {
    override def read(value: JsValue): UnspentTXO = {
      val jsObject = value.asJsObject

      // get only non-optional values here 
      val Seq(transaction_hash, output_index, locked_satoshies, addresses, script_hex, spent) =
        jsObject.getFields("transaction_hash", "output_index", "value", "addresses",
          "script_hex", "spent")

      val assetId = jsObject.fields.get("asset_id") match {
        case Some(JsString(s)) => Some(s)
        case None => None
      }

      val assetQuantity = jsObject.fields.get("asset_quantity") match {
        case Some(JsNumber(n)) => Some(n.toLong)
        case None => None
      }

      // convert JsArray to List[ BitcoinAdress ]
      val addressList = addresses match {
        case ja: JsArray => {
          ja.elements.toList.map(e => BitcoinAddress(e.convertTo[String]))
        }
      }

      UnspentTXO(transaction_hash.convertTo[String], output_index.convertTo[Int],
        locked_satoshies.convertTo[Long], assetId, assetQuantity,
        addressList, script_hex.convertTo[String], spent.convertTo[Boolean])

    }

    override def write(unspentTXO: UnspentTXO): spray.json.JsValue = {
      val addresses = unspentTXO.addresses.map(p =>
        bitcoinAddressFormat.write(p))(breakOut): Vector[JsValue]

      val m: Map[String, JsValue] = Map("transaction_hash" -> JsString(unspentTXO.transaction_hash),
        "output_index" -> JsString(unspentTXO.output_index.toString),
        "value" -> JsString(unspentTXO.value.toString),
        "asset_id" -> (unspentTXO.asset_id match {
          case Some(s) => JsString(s)
          case None => JsNull
        }),
        "asset_quantity" -> (unspentTXO.asset_quantity match {
          case Some(n) => JsNumber(n)
          case None => JsNull
        }),
        "addresses" -> JsArray(addresses),
        "script_hex" -> JsString(unspentTXO.script_hex),
        "spent" -> JsBoolean(unspentTXO.spent))
      JsObject(m)
    }
  }

}
