

import org.scalatest.FlatSpec
import org.scalatest.MustMatchers
import com.coinprism.transaction.TransactionBuilder
import com.coinprism.config.Test
import com.coinprism.blockchain.AssetAddress
import com.coinprism.blockchain.BitcoinAddress
import com.coinprism.transaction.ColorCoinIssuance
import scala.concurrent.duration.DurationInt
import org.scalatest.concurrent.ScalaFutures

//case class ColorCoinIssuance(fees: Long, from: BitcoinAddress, address: BitcoinAddress,
// amount: Long, metadata: String)
class TransactionBuilderTest extends FlatSpec with MustMatchers with ScalaFutures
  with TransactionBuilder with Test {

  "Transaction Builder" must "issue colored coins" in {
    val coloredCoinIssuance = ColorCoinIssuance(1000, BitcoinAddress("1zLkEoZF7Zdoso57h9si5fKxrKopnGSDn"),
      AssetAddress("akSjSW57xhGp86K6JFXXroACfRCw7SPv637"), 500, "u=https://site.com/assetdef")
    val unsignedTransaction = issueColoredCoins(coloredCoinIssuance)
    whenReady(unsignedTransaction, timeout(2 seconds), interval(5 millis)) { txs =>
      txs.inputs.size must be(1)
      txs.outputs.size must be(3)
    }
  }
}