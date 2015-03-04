package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol
object BitcoinAddressProtocol extends DefaultJsonProtocol {

  implicit val bitcoinAddressFormat = jsonFormat(BitcoinAddress, "value")
}