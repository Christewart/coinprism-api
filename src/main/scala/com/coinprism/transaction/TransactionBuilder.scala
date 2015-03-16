package com.coinprism.transaction

import com.coinprism.config.Environment
import com.coinprism.blockchain.BitcoinAddress
import spray.json._
import DefaultJsonProtocol._
import spray.http.HttpRequest
import spray.client.pipelining._
import spray.http._
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport._
import scala.concurrent.Future
import com.coinprism.blockchain.AddressProtocol
import spray.json._
import DefaultJsonProtocol._
trait TransactionBuilder { this: Environment =>
  import system._

  /**
   * Creates an unsigned transaction for issuing colored coins from a bitcoin address.
   * The destination ('address' parameter) must be an asset enabled address.
   * @param unsigned transaction - the unsigned transaction
   */
  def createUnsignedTxForColoredCoins(issuance: ColorCoinIssuance): Future[UnsignedTransaction] = {
    import ColorCoinIssuanceProtocol._
    import UnsignedTransactionProtocol._
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + issueAsset + "?format=json", issuance))

  }

  /**
   * Creates an unsigned transaction for sending an asset from a bitcoin address.
   * The destination ('address' parameter) must be an asset enabled address.
   * @param asset transaction - the asset transaction to be sent to coinprism
   */
  def createUnsignedTxForAsset(assetTransaction: NewAssetTransaction): Future[UnsignedTransaction] = {
    import UnsignedTransactionProtocol._
    import NewAssetTransactionProtocol._
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + sendAsset + "?format=json", assetTransaction))
  }

  /**
   * Creates an unsigned transaction for sending Bitcoin from an address.
   * @param bitcoinTransaction - the bitcoin transaction to be sent to coinprism
   * @return unsignedTransaction - the unsignedTransaction coinprism created
   */
  def createUnsignedTxForBitcoin(bitcoinTransaction: NewBitcoinTransaction): Future[UnsignedTransaction] = {
    import UnsignedTransactionProtocol._
    import NewBitcoinTransactionProtocol._
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + sendBitcoin + "?format=json", bitcoinTransaction))
  }

  /**
   * Creates an unsigned transaction that atomically swaps
   * the given amount of Bitcoins coming from one address with
   * the given amount of an asset coming from another address.
   * @return UnsignedTransaction reprsenting the swap
   */
  def atomicallySwapBitcoinsAndAsset( swp : SwapBitcoinsAndAsset) : Future[UnsignedTransaction] = {
    import UnsignedTransactionProtocol._
    import SwapBitcoinsAndAssetProtocol._
    println(swp.toJson)
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + bitcoinAssetSwap + "?format=json", swp))
  }
}
