package com.coinprism.address

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration.DurationInt
import com.coinprism.config.ActorSystemConfig
import akka.actor.Props

class AddressRequestTest extends FlatSpec with MustMatchers with ScalaFutures with ActorSystemConfig {
  val testBitcoinAddress = BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  val testAssetAddress = AssetAddress("akMVNmYwN8bqqQknswpgPCoVCUAyiXyDxvG")

  "AddressRequest" must "get the correct bitcoin address from coinprism" in {

    val addressBalanceFromCoinprism =
      AddressRequest.getAddress(BitcoinAddress("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt"))

    whenReady(addressBalanceFromCoinprism, timeout(5 seconds), interval(5 millis)) { a =>
      a.bitcoin_address must be(testBitcoinAddress)
    }
  }

  it must "get the correct asset address from coinprism" in {
    val addressBalanceFromCoinprism = AddressRequest.getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.asset_address must be(testAssetAddress)
    }
  }

  it must "tell if the current address is an issuable asset" in {
    val addressBalanceFromCoinprism = AddressRequest.getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.issuable_asset must be(Some("AcuREPQJemxHwqVZzbsutcVVqPr4HwjXBa"))
    }
  }

  it must "tell if there is a balance available at this address" in {
    val addressBalanceFromCoinprism = AddressRequest.getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }

  it must "tell if there is an unconfirmed balance at this address" in {
    val addressBalanceFromCoinprism = AddressRequest.getAddress(testAssetAddress)
    whenReady(addressBalanceFromCoinprism, timeout(2 seconds), interval(5 millis)) { a =>
      a.balance must be(0)
    }
  }
}