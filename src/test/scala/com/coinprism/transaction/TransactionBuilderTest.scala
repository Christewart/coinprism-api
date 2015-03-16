package com.coinprism.transaction

import com.coinprism.blockchain.{AssetAddress, BitcoinAddress}
import com.coinprism.config.Production
import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

//case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: BitcoinAddress,
// amount: Long, metadata: String)
class TransactionBuilderTest extends FlatSpec with MustMatchers with ScalaFutures
  with TransactionBuilder with Production {

  "Transaction Builder" must "issue colored coins" in {
    val coloredCoinIssuance = ColorCoinIssuance(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"), 500, "u=https://site.com/assetdef")
    val unsignedTransaction = createUnsignedTxForColoredCoins(coloredCoinIssuance)

    whenReady(unsignedTransaction, timeout(3 seconds), interval(5 millis)) { txs =>
      txs.inputs.size must be(1)
      txs.outputs.size must be(3)
      txs.amount must be (140756317)
      txs.fees must be (1000)

    }
  }

  it must "create an unsigned asset transaction" in {

    val newTx = NewAssetTransaction(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      List(AssetDestination(AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"),
        10, "AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36")))
    val unsignedTx = createUnsignedTxForAsset(newTx)

    whenReady(unsignedTx, timeout(3 seconds), interval(5 millis)) { tx =>

      tx.inputs.size must be(2)
      tx.outputs.size must be(4)
    }

  }

  it must "create an unsigned bitcoin transaction" in {

    val newTx = NewBitcoinTransaction(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      List(BitcoinDestination(BitcoinAddress("199bUwVViHiT3FCHS9TtYgDbcaMDZdY55q"), 200000)))

    val unsignedTx = createUnsignedTxForBitcoin(newTx)

    whenReady(unsignedTx, timeout(2 seconds), interval(5 millis)) { tx =>

      tx.inputs.size must be(1)
      tx.outputs.size must be(2)

    }
  }

  it must "atomically swap and bitcoins for an asset" in {

    val swp = SwapBitcoinsAndAsset(AssetAddress("anaypepNg9qwGbfekFdqEVohqupLhPkKA5Y"),
      1500000, AssetAddress("akBxDzQctibTKg7xSAKG4MwZuJTVys7dK7E"),
      "AHthB6AQHaSS9VffkfMqTKTxVV43Dgst36", 5, 15000)
    val unsignedTx = atomicallySwapBitcoinsAndAsset(swp)
    whenReady(unsignedTx, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.inputs.size must be (2)
      txs.outputs.size must be (5)

      txs.amount must be ("4989985600".toLong)
      txs.fees must be (15000)
    }
  }
}

