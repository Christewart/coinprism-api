package com.coinprism.transaction

import org.scalacoin.protocol.AssetAddress
import spray.json.DefaultJsonProtocol
import com.coinprism.blockchain.AssetAddressProtocol
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.JsString
import spray.json.JsObject

case class AssetDestination(address: AssetAddress, amount: Long, asset_id: String)
object AssetDestinationProtocol extends DefaultJsonProtocol {

  import AssetAddressProtocol._
  private val assetDestinationFormat = jsonFormat3(AssetDestination)

  implicit object AssetAddressFormat extends RootJsonFormat[AssetDestination] {

    override def read(jsValue: JsValue) = {
      assetDestinationFormat.read(jsValue)
    }

    override def write(destination: AssetDestination) = {
      val m: Map[String, JsValue] = Map("address" -> JsString(destination.address.value),
        "amount" -> JsString(destination.amount.toString), "asset_id" -> JsString(destination.asset_id))

      JsObject(m)
    }
  }
}