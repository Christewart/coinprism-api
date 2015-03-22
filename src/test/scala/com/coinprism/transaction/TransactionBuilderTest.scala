package com.coinprism.transaction

import com.coinprism.blockchain.{AssetAddress, BitcoinAddress}
import com.coinprism.config.CoinprismProduction
import com.coinprism.config.Formats.{ Json , Raw}
import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

//case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: BitcoinAddress,
// amount: Long, metadata: String)
class TransactionBuilderTest extends FlatSpec with MustMatchers with ScalaFutures
  with CoinprismTransactionBuilder with CoinprismProduction {

  "Transaction Builder" must "issue colored coins" in {
    val coloredCoinIssuance = ColorCoinIssuance(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"), 500, "u=https://site.com/assetdef")
    val unsignedTransaction = createUnsignedTxForColoredCoins(coloredCoinIssuance)(Json)

    whenReady(unsignedTransaction, timeout(3 seconds), interval(5 millis)) { txs =>
      txs.inputs.get.size must be(1)
      txs.outputs.get.size must be(3)
      txs.amount.get must be (140756317)
      txs.fees.get must be (1000)

    }
  }

  it must "issue colored coins and return a raw unsigned tx" in {
    val coloredCoinIssuance = ColorCoinIssuance(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"), 500, "u=https://site.com/assetdef")
    val unsignedTransaction = createUnsignedTxForColoredCoins(coloredCoinIssuance)(Raw)

    whenReady(unsignedTransaction, timeout(2 seconds), interval(5 millis)) { uTx =>
      println(uTx.raw.get)
      uTx.raw.isDefined must be (true)

    }
  }

  it must "create an unsigned asset transaction" in {

    val newTx = NewAssetTransaction(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      List(AssetDestination(AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"),
        10, "AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36")))
    val unsignedTx = createUnsignedTxForAsset(newTx)(Json)

    whenReady(unsignedTx, timeout(3 seconds), interval(5 millis)) { tx =>

      tx.inputs.get.size must be(2)
      tx.outputs.get.size must be(4)
    }

  }

  it must "create an unsigned bitcoin transaction" in {

    val newTx = NewBitcoinTransaction(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      List(BitcoinDestination(BitcoinAddress("199bUwVViHiT3FCHS9TtYgDbcaMDZdY55q"), 200000)))

    val unsignedTx = createUnsignedTxForBitcoin(newTx)(Json)

    whenReady(unsignedTx, timeout(2 seconds), interval(5 millis)) { tx =>

      tx.inputs.get.size must be(1)
      tx.outputs.get.size must be(2)

    }
  }

  it must "atomically swap bitcoins for an asset" in {

    val swp = SwapBitcoinsAndAsset(AssetAddress("anaypepNg9qwGbfekFdqEVohqupLhPkKA5Y"),
      1500000, AssetAddress("akBxDzQctibTKg7xSAKG4MwZuJTVys7dK7E"),
      "AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36", 5, 15000)
    val unsignedTx = atomicallySwapBitcoinsAndAsset(swp)(Json)
    whenReady(unsignedTx, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.inputs.get.size must be (2)
      txs.outputs.get.size must be (5)

      txs.amount.get must be ("4989985600".toLong)
      txs.fees.get must be (15000)
    }
  }

}

