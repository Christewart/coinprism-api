package com.coinprism.transaction

import com.coinprism.blockchain.{Output, Input}
import org.joda.time.DateTime
import spray.json.DefaultJsonProtocol

/**
 * Created by chris on 3/15/15.
 */

sealed abstract class Tx

case class Transaction(hash: String, block_hash: String, block_height: Long, block_time: DateTime,
  inputs: List[Input], outputs: List[Output], amount: Long, fees: Long, confirmations: Long) extends Tx

case class RawTransaction(raw : String) extends Tx

object RawTransactionProtocol extends DefaultJsonProtocol {
  implicit val rawTransactionFormat = jsonFormat(RawTransaction, "raw")
}
