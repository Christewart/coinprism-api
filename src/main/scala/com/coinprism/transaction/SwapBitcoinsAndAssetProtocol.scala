package com.coinprism.transaction

import com.coinprism.blockchain.AssetAddress
import spray.json._

/**
 * Created by chris on 3/15/15.
 */

case class SwapBitcoinsAndAsset(from_btc : AssetAddress, amount_btc : Long,
  from_asset : AssetAddress, asset_id : String,
  amount_asset : Long, fees : Long)
object SwapBitcoinsAndAssetProtocol extends DefaultJsonProtocol {

  implicit object SwapBitcoinsAndAssetProtocol extends RootJsonFormat[SwapBitcoinsAndAsset] {
    override def read(jsValue : JsValue) = {
      val obj = jsValue.asJsObject
      val Seq(from_btc, amount_btc, from_asset, asset_id, amount_asset,
        fees) = obj.getFields("from_btc", "amount_btc", "from_asset", "asset_id",
        "amount_asset", "fees")
      val fromBTC= AssetAddress(from_btc.convertTo[String])
      val fromAsset = AssetAddress(from_asset.convertTo[String])
      SwapBitcoinsAndAsset(fromBTC, amount_btc.convertTo[Long], fromAsset,
        asset_id.convertTo[String], amount_asset.convertTo[Long],
        fees.convertTo[Long])
    }

    override def write(swp : SwapBitcoinsAndAsset) = {
      val m : Map[String,JsValue] = Map("from_btc" -> JsString(swp.from_btc.value),
        "amount_btc" -> JsString(swp.amount_btc.toString), "from_asset" ->
        JsString(swp.from_asset.value), "asset_id" -> JsString(swp.asset_id),
        "amount_asset" -> JsString(swp.amount_asset.toString),
        "fees" -> JsString(swp.fees.toString))
      JsObject(m)
    }

  }
}
