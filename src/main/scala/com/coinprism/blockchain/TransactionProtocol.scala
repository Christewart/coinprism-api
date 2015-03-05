package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol
import org.joda.time.DateTime
import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString

case class Transaction(hash: String, block_hash: String, block_height: Long, block_time: DateTime,
  inputs: List[Input], outputs: List[Output], amount: Long, fees: Long, confirmations: Long)

object DateTimeProtocol extends DefaultJsonProtocol {

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {

    override def read(value: JsValue) = {
      value match {
        case JsString(s) => DateTime.parse(s)
        case _ => throw new RuntimeException("DateTime format should be of type JsString")
      }
    }

    override def write(dateTime: DateTime) = {
      JsString(dateTime.toString)
    }
  }
}
object TransactionProtocol extends DefaultJsonProtocol {
  import InputProtocol._
  import OutputProtocol._
  import DateTimeProtocol._

  implicit val transactionProtocol = jsonFormat9(Transaction.apply)
}

