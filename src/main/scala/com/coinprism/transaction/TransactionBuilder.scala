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
import com.coinprism.blockchain.BitcoinAddressProtocol
import com.coinprism.blockchain.Address
import com.coinprism.blockchain.AddressProtocol


trait TransactionBuilder { this: Environment =>
  import system._

  /**
   * Creates an unsigned transaction for issuing colored coins from a bitcoin address.
   * The destination ('address' parameter) must be an asset enabled address.
   * @param unsigned transaction - the unsigned transaction
   */
  def issueColoredCoins(issuance: ColorCoinIssuance): Future[UnsignedTransaction] = {
    import ColorCoinIssuanceProtocol._
    import UnsignedTransactionProtocol._
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + issueAsset + "?format=json", issuance))

  }

  /**
   * Creates an unsigned transaction for sending an asset from a bitcoin address. 
   * The destination ('address' parameter) must be an asset enabled address.
   * @param 
   */
  def sendAsset(assetTransaction: NewTransaction) = {
    import UnsignedTransactionProtocol._
    import AddressProtocol._
    import NewTransactionProtocol._
    val pipeline: HttpRequest => Future[UnsignedTransaction] = sendReceive ~> unmarshal[UnsignedTransaction]
    pipeline(Post(host + version + issueAsset + "?format=json", assetTransaction))
  }
}


/*"""{  'fees': 1000,  'from': '1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn',  
'to': [    {      'address': 'akSjSW57xhGp86K6JFXXroACfRCw7SPv637',      
  'amount': '10',      
'asset_id': 'AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36'    }  ]}"""*/