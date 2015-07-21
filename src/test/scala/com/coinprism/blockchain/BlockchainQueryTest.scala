package com.coinprism.blockchain

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.FlatSpec
import org.scalatest.MustMatchers
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import spray.httpx.UnsuccessfulResponseException
import scala.concurrent.Future
import com.coinprism.config.{Formats, CoinprismProduction}
import com.coinprism.transaction. { RawTransaction, Transaction, Tx }
import scala.concurrent.ExecutionContext.Implicits.global

class BlockchainQueryTest extends FlatSpec with MustMatchers
with ScalaFutures with CoinprismBlockchainQuery with CoinprismProduction {

  val bitcoinAddress = BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  val unusedAddress = BitcoinAddress("1suredbitsx9YWdWuCaCYwad8ZoYayyRYt")
  val testBitcoinAddress = BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  val testAssetAddress = AssetAddress("akMVNmYwN8bqqQknswpgPCoVCUAyiXyDxvG")
  val assetId = "AS6tDJJ3oWrcE1Kk3T14mD8q6ycHYVzyYQ"
  "BlockchainQuery" must "get the correct bitcoin address from coinprism" in {
    val addressBalanceFromCoinprism =
      addressBalance(BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt"))
    whenReady(addressBalanceFromCoinprism, timeout(5 seconds), interval(5 millis)) { a =>
      a.bitcoin_address must be(testBitcoinAddress)
    }
  }

  it must "get the correct asset address from coinprism" in {
    val addressBalanceFromCoinprism = addressBalance(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.asset_address must be(testAssetAddress)
    }
  }

  it must "tell if the current address is an issuable asset" in {
    val addressBalanceFromCoinprism = addressBalance(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.issuable_asset must be(Some("AcuREPQJemxHwqVZzbsutcVVqPr4HwjXBa"))
    }
  }

  it must "tell if there is a balance available at this address" in {
    val addressBalanceFromCoinprism = addressBalance(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }

  it must "tell if there is an unconfirmed balance at this address" in {
    val addressBalanceFromCoinprism = addressBalance(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }

  it must "return the recent transactions for a given address" in {
    val transactions: Future[List[Transaction]] = recentTransactions(bitcoinAddress)
    whenReady(transactions, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.size must be(2)
    }
  }

  it must "return the specified raw transaction for a given hash" in {
    val tx: Future[Tx] = transaction("fbb36255453bf8ff465d9ca5c427bd0e36cc799fda090cbcd62113f1f3e97cb4")(Formats.Raw)

    whenReady(tx, timeout(2 seconds), interval(5 millis)) { t =>

      val serializedTx = t match {

        case RawTransaction(tx) => tx
        case _ => throw new RuntimeException("This should not happen")
      }


      serializedTx must be("010000000251fe802030b47de4446515845a301a30f766ae6ec35eb36f20a926d494c0b1b3000000008" +
        "a4730440220097e576cde74ea5846270de7cbeab7092b81677f8f09ebd688503bf7bc2f2ccc02200e7b94c28b2900e9319a1f5a" +
        "866389d6b60133893c3f3076ea3873e4bfa676ae014104b571b629acf60e5cc2172312300ca4430f41eed8abd6f63f7f56faf89" +
        "c0cf973fe442ee0e8a4bce43b2f4c579360aeb0815029c38386ebd74ebef41abb8e7a64ffffffffe8c910f11f69e68360bb7f9f" +
        "e6e6d905d8097ba3b9c8a68819a2d9340657d112010000008c493046022100daf02064b803c403a9ef2d981667dbd53df427e79" +
        "9f78f59f2627db9689b1272022100d4ee9808cae4eefc6e65c0bc6b88a371a9e88f730f0f2691c5ac945b9f3706a0014104a0df" +
        "3cdda29a8062483e6fd472958e05c0ea8d439591be97d1d230095ae264f293be19f35500e934faae5c0ad2a780d1c98018898a" +
        "56f34bdcf729e7332aa824ffffffff0280841e00000000001976a914795efb808598d6a24d1734b929fce1d4b713215188ac72c" +
        "a3400000000001976a914a05c4efe25b472dbb07d04e6c8a6b3197d8bdcdb88ac00000000")
    }
  }
  it must "return the correct amount of inputs for a transaction given an address" in {
    val transactions: Future[List[Transaction]] = recentTransactions(bitcoinAddress)
    whenReady(transactions, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.head.inputs.size must be(2)

    }
  }

  it must "return the correct amount of outputs for a transaction given an address" in {
    val transactions: Future[List[Transaction]] = recentTransactions(bitcoinAddress)
    whenReady(transactions, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.head.outputs.size must be(4)
    }
  }
  it must "return the correct amount of unspent txos" in {
    val response = unspentTXOs(bitcoinAddress)
    whenReady(response, timeout(10 seconds), interval(5 millis)) { txos =>
      txos.size must be(2)
    }
  }

  it must "return no utxos for an address whose utxos have been spent" in {
    val response : Future[List[UnspentTXO]] = unspentTXOs(BitcoinAddress("1HuTJL3F8vdSpTmSjVEQLoB65bCDbXJC6y"))
    whenReady(response, timeout(5 seconds), interval(5 millis)) { utxos =>
      utxos.size must be (0)
    }
  }

  it must "return the address that own an asset" in {

    val ownership = assetOwners(assetId)
    whenReady(ownership, timeout(2 seconds), interval(5 millis)) { o =>
      o.asset_id must be(assetId)
      o.owners.size must be(4)
      o.owners.head.address must be(BitcoinAddress("1D24qr4gDZ1h5D5hLXF3AbddFWiA4Vnm3d"))
      o.owners.head.asset_quantity must be(800005)
      o.owners.head.script must be("76a91483d521f559808be29bcc14dbb9b8763e8bd0230f88ac")
    }
  }

  it must "the same asset id from coinprism given an asset id" in {
    val assetMetaInfo = assetDefinition(assetId)
    whenReady(assetMetaInfo, timeout(2 seconds), interval(5 millis)) { a =>
      a.asset_id must be(assetId)
    }
  }

  it must "return an error for an invalid address" in {
    val response = unspentTXOs(unusedAddress)
    evaluating {
      Await.result(response, 2 seconds)
    } must produce[UnsuccessfulResponseException]

  }

  it must "return utxos for bitcoin addresses that have no associated asset information correctly" in  {
    val bitcoinAddress = BitcoinAddress("1C4kYhyLftmkn48YarSoLupxHfYFo8kp64")
    val utxos : Future[List[UnspentTXO]] = unspentTXOs(bitcoinAddress)
    utxos onFailure { case err =>
        err.printStackTrace

    }
    whenReady(utxos, timeout(5 seconds), interval( 5 millis)) { utxo =>
      utxo.size must be (1)


    }
  }
}