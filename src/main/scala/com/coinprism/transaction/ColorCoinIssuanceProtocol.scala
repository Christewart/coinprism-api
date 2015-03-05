package com.coinprism.transaction

import spray.json.DefaultJsonProtocol
import com.coinprism.blockchain.BitcoinAddress
import com.coinprism.blockchain.BitcoinAddressProtocol
import com.coinprism.blockchain.AssetAddress
import com.coinprism.blockchain.AssetAddressProtocol
case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: AssetAddress,
  amount: Long, metadata: String)
object ColorCoinIssuanceProtocol extends DefaultJsonProtocol {

  import BitcoinAddressProtocol._
  import AssetAddressProtocol._
  implicit val colorCoinIssuanceFormat = jsonFormat5(ColorCoinIssuance.apply)
}