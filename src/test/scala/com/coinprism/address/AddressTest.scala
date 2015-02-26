package com.coinprism.address

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration.DurationInt
class AddressTest extends FlatSpec with MustMatchers with ScalaFutures {
  val testBitcoinAddress = Address("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt")
  "Address" must
    "get the correct address from coinprism" in {

      val addressBalanceFromCoinPrism =
        Address.getAddress(Address("1BXVXP82f7x9YWdWuCaCYwad8ZoYayyRYt"))

      whenReady(addressBalanceFromCoinPrism, timeout(3 seconds), interval(5 millis)) { a =>
        a.bitcoin_address must be(testBitcoinAddress.value)
      }
    }
}