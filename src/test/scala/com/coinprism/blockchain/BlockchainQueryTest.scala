package com.coinprism.blockchain

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.FlatSpec
import org.scalatest.MustMatchers
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import spray.httpx.UnsuccessfulResponseException
import scala.concurrent.Future
import com.coinprism.config.Test
import com.coinprism.config.Production

class BlockchainQueryTest extends FlatSpec with MustMatchers with ScalaFutures with BlockchainQuery with Production {

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
}