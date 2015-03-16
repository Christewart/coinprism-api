package com.coinprism.transaction

import spray.json.DefaultJsonProtocol
import com.coinprism.blockchain.OutputProtocol
import com.coinprism.blockchain.InputProtocol
import com.coinprism.blockchain.Output
import com.coinprism.blockchain.Input
case class UnsignedTransaction(inputs: List[Input], outputs: List[Output], amount : Long, fees : Long)
object UnsignedTransactionProtocol extends DefaultJsonProtocol {
  import OutputProtocol._
  import InputProtocol._

  implicit val unsignedTransactionFormat = jsonFormat4(UnsignedTransaction.apply)

}