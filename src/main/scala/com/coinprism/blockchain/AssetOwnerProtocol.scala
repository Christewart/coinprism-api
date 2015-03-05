package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.JsNumber
import spray.json.JsString
import spray.json.JsObject

case class AssetOwnership(block_height: Long, asset_id: String, owners: List[AssetOwner])
case class AssetOwner(script: String, address: BitcoinAddress, asset_quantity: Long)
object AssetOwnerProtocol extends DefaultJsonProtocol {

  implicit object AssetOwnerFormat extends RootJsonFormat[AssetOwner] {
    override def read(jsValue: JsValue): AssetOwner = {
      val obj = jsValue.asJsObject
      val Seq(script, address, asset_quantity) = obj.getFields("script", "address", "asset_quantity")
      val bitcoinAddress = BitcoinAddress(address.convertTo[String])
      val assetQuantity = asset_quantity match {
        case JsString(s) => s.toLong
        case _ => throw new RuntimeException("asset_quantity inside of AssetOwnerProtocol must be of type JsString")
      }
      AssetOwner(script.convertTo[String], bitcoinAddress, assetQuantity)
    }
    override def write(assetOwner: AssetOwner): JsValue = {
      val m: Map[String, JsValue] = Map("script" -> JsString(assetOwner.script),
        "address" -> JsString(assetOwner.address.value),
        "asset_quantity" -> JsNumber(assetOwner.asset_quantity))
      JsObject(m)
    }

  }
}

object AssetOwnershipProtocol extends DefaultJsonProtocol {
  import AssetOwnerProtocol._
  implicit val assetOwnershipFormat = jsonFormat3(AssetOwnership.apply)
}