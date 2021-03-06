package com.coinprism.blockchain


import com.coinprism.config.Formats.{Raw, Json, ApiFormats}
import com.coinprism.transaction.{ Transaction , RawTransaction, Tx }
import org.scalacoin.protocol.Address
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport._
import spray.json.JsValue
import com.coinprism.config.{Formats, CoinprismEnvironment}


trait CoinprismBlockchainQuery { this: CoinprismEnvironment =>
  import coinprismSystem._

  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  /**
   * returns the balance of an address
   * @param address - the address to fetch from coinprism
   * @return balance - the balance of this address
   */
  def addressBalance(address: Address): Future[AddressBalance] = {
    import AssetBalanceProtocol._
    import AddressBalanceProtocol._
    val pipeline: HttpRequest => Future[AddressBalance] =
      addHeader("Authorization",
        "2179cc484e71fcaf682bbe3a95364210c02495b57940949dcb83df26306d0ebb") ~>
      sendReceive ~> unmarshal[AddressBalance]
    pipeline(Get(coinprismUrl +  addresses + address.value))
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
    pipeline(Get(coinprismUrl + addresses + address.value + "/transactions?format=json"))
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
    pipeline(Get(coinprismUrl + addresses + address.value + "/unspents"))
  }

  /**
   * Returns details about a bitcoin or colored coin transaction given its hash.
   * @param transaction_hash - the transaction hash for a given transaction
   * @return
   */
  def transaction(transaction_hash: String)(format : ApiFormats): Future[Tx] = {
    import TransactionProtocol._
    import com.coinprism.transaction.RawTransactionProtocol._
    val uri = coinprismUrl + "transactions/" + transaction_hash
    val formattedUri = Formats.correctFormat(uri,format)
    logger.debug("Full request: " + formattedUri)
    val pipeline: HttpRequest => Future[Tx] = format match {
      case Json => sendReceive ~> unmarshal[Transaction]
      case Raw => sendReceive ~> unmarshal[RawTransaction]
    }
    pipeline(Get(formattedUri))
  }

  /**
   * Returns the metadata about an asset.
   * @param assetid - the asset id to fetch
   * @return assetMetaInfo - the meta information about the given asset
   */
  def assetDefinition(assetId: String): Future[AssetMetaInfo] = {
    import AssetMetaInfoProtocol._
    val pipeline: HttpRequest => Future[AssetMetaInfo] = sendReceive ~> unmarshal[AssetMetaInfo]
    pipeline(Get(coinprismUrl+ assets + assetId))
  }

  /**
   * Returns all the addresses holding an asset, and the number of units held
   * @param assetId - the asset to find up in the blockchain
   * @return owners - the ownsers of the asset and the number of units held
   */
  def assetOwners(assetId: String, blockHeight: Option[Long] = None) = {
    import AssetOwnershipProtocol._
    val pipeline: HttpRequest => Future[AssetOwnership] = sendReceive ~> unmarshal[AssetOwnership]
    blockHeight match {
      case Some(n) => pipeline(Get(coinprismUrl + assets + assetId + "/owners?block=" + blockHeight.toString))
      case None => pipeline(Get(coinprismUrl + assets + assetId + "/owners"))
    }
  }

  /**
   * Query asset information for transactions that are not in the Blockchain yet. The transactions may reference each other,
   * or reference other transactions already in the Blockchain.
   * @param json - the raw transaction in json to analyze
   * @return
   */
  def analyzeRawTransactions(json: JsValue) = ???
}