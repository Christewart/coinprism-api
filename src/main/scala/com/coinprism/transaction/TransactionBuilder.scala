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
    println(bitcoinTransaction.toJson)
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + sendBitcoin + "?format=json", bitcoinTransaction))
  }
}


/*"""{  'fees': 1000,  'from': '1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn',  
'to': [    {      'address': 'akSjSW57xhGp86K6JFXXroACfRCw7SPv637',      
  'amount': '10',      
'asset_id': 'AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36'    }  ]}"""*/