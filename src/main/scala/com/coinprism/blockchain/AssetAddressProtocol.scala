package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol

object AssetAddressProtocol extends DefaultJsonProtocol {

  implicit val assetAddressFormat = jsonFormat(AssetAddress, "value")

}