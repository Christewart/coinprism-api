package com.coinprism.blockchain

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.FlatSpec
import org.scalatest.MustMatchers
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import spray.httpx.UnsuccessfulResponseException

class BlockchainQueryTest extends FlatSpec with MustMatchers with ScalaFutures with BlockchainQuery {

  val bitcoinAddress = BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  val unusedAddress = BitcoinAddress("1suredbitsx9YWdWuCaCYwad8ZoYayyRYt")
  val testBitcoinAddress = BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  val testAssetAddress = AssetAddress("akMVNmYwN8bqqQknswpgPCoVCUAyiXyDxvG")
  "BlockchainQuery" must "get the correct bitcoin address from coinprism" in {
    val addressBalanceFromCoinprism =
      getAddress(BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt"))
    whenReady(addressBalanceFromCoinprism, timeout(5 seconds), interval(5 millis)) { a =>
      a.bitcoin_address must be(testBitcoinAddress)
    }
  }

  it must "get the correct asset address from coinprism" in {
    val addressBalanceFromCoinprism = getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.asset_address must be(testAssetAddress)
    }
  }

  it must "tell if the current address is an issuable asset" in {
    val addressBalanceFromCoinprism = getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.issuable_asset must be(Some("AcuREPQJemxHwqVZzbsutcVVqPr4HwjXBa"))
    }
  }

  it must "tell if there is a balance available at this address" in {
    val addressBalanceFromCoinprism = getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }

  it must "tell if there is an unconfirmed balance at this address" in {
    val addressBalanceFromCoinprism = getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }
  it must "return the correct amount of unspent txos" in {
    val response = getUnspentTXOs(bitcoinAddress)
    whenReady(response, timeout(10 seconds), interval(5 millis)) { txos =>
      txos.size must be(2)
    }
  }

  it must "return an error for an invalid address" in {
    val response = getUnspentTXOs(unusedAddress)
    evaluating {
      Await.result(response, 2 seconds)
    } must produce[UnsuccessfulResponseException]

  }
}