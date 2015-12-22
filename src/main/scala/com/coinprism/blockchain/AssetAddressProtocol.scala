package com.coinprism.blockchain

import org.scalacoin.protocol.AssetAddress
import spray.json.DefaultJsonProtocol

object AssetAddressProtocol extends DefaultJsonProtocol {

  implicit val assetAddressFormat = jsonFormat1(AssetAddress.apply _)

}