package com.coinprism.blockchain

import scala.concurrent.Future
import spray.http.HttpRequest
import akka.actor.ActorSystem
import com.coinprism.config.Constants
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import com.coinprism.config.Constants
import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http._
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport._
import akka.util.Timeout

abstract class Address(val value: String)
case class BitcoinAddress(override val value: String) extends Address(value)
case class AssetAddress(override val value: String) extends Address(value)

trait BlockchainQuery {
  import Constants._
  implicit val system = ActorSystem()
  import system.dispatcher

  /**
   * returns the balance of an address
   * @param address - the address to fetch from coinprism
   * @return balance - the balance of this address
   */
  def addressBalance(address: Address): Future[AddressBalance] = {
    import AssetBalanceProtocol._
    import AddressBalanceProtocol._
    val pipeline: HttpRequest => Future[AddressBalance] = sendReceive ~> unmarshal[AddressBalance]
    pipeline(Get(host + version + addresses + address.value))
  }

  /**
   * Returns the last 20 transactions for a given address
   * @param address - the address to find transactions for
   * @return transactions - Returns the recent transactions that involved this address.
   */
  def recentTransactions(address: Address): Future[List[Transaction]] = {
    import TransactionProtocol._

    val pipeline: HttpRequest => Future[List[Transaction]] =
      sendReceive ~> unmarshal[List[Transaction]]

    pipeline(Get(host + Constants.version + addresses + address.value + "/transactions?format=json"))
  }
  /**
   * Finds all the unspent transaction outputs of an address
   * @param address
   * @return utxos - the unspent transaction outputs of a given address
   */
  def unspentTXOs(address: Address): Future[List[UnspentTXO]] = {
    import UnspentTXOProtocol._
    val pipeline: HttpRequest => Future[List[UnspentTXO]] =
      sendReceive ~> unmarshal[List[UnspentTXO]]
    pipeline(Get(host + version + addresses + address.value + "/unspents"))
  }

  /**
   * Returns details about a bitcoin or colored coin transaction given its hash.
   * @param transaction_hash - the transaction hash for a given transaction
   * @return
   */
  def transaction(transaction_hash: String): Future[Transaction] = {
    import TransactionProtocol._
    val pipeline: HttpRequest => Future[Transaction] = sendReceive ~> unmarshal[Transaction]

    pipeline(Get(host + version + "transactions/" + transaction_hash))
  }

  /**
   * Returns the metadata about an asset.
   * @param assetid - the asset id to fetch
   * @return assetMetaInfo - the meta information about the given asset
   */
  def assetDefinition(assetId: String): Future[AssetMetaInfo] = {
    import AssetMetaInfoProtocol._
    val pipeline: HttpRequest => Future[AssetMetaInfo] = sendReceive ~> unmarshal[AssetMetaInfo]
    pipeline(Get(host + version + assets + assetId))
  }

}