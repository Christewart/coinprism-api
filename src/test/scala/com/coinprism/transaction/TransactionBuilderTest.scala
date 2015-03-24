package com.coinprism.transaction

import com.coinprism.blockchain.{AssetAddress, BitcoinAddress}
import com.coinprism.config.CoinprismProduction
import com.coinprism.config.Formats.{ Json , Raw}
import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

//case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: BitcoinAddress,
// amount: Long, metadata: String)
class TransactionBuilderTest extends FlatSpec with MustMatchers with ScalaFutures
  with CoinprismTransactionBuilder with CoinprismProduction {

  import coinprismSystem._
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

  it must "broadcast a transaction to the network" in {

    val rawTx = "0100000001d238c42ec059b8c7747cd51debb4310108f" +
      "6279d14957472822cf061a660828b000000006b483045022100d" +
      "326257244e8cb86889509cf5b4717edf273d9e6e643f571c4347" +
      "53059eb01a902204aa761f44e89b55af0e2fa0caef580401a4ba" +
      "61eebf8bc29020ce23f6fab1ee2012102661ac805eef8015c7c8" +
      "d617c65ef327c4f2272fd5d9e97456a0d32d3bcf6f563fffffff" +
      "f0288130000000000001976a91430a5d35558ade668b8829a2a0" +
      "f60a3f10358327e88ac306f" +
      "0100000000001976a914760fdb3483204406ddb73a45b20b7c9be61d0a7e88ac00000000"

    val response = broadcastTransaction(rawTx)
    response onComplete {
      case Success(h) => println(h)
      case Failure(err) => throw err
    }
    whenReady(response, timeout(2 seconds), interval(5 millis)) { hash =>

    }
  }

}

