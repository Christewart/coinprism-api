package com.coinprism.transaction

import spray.json.DefaultJsonProtocol

/**
 * Created by chris on 3/15/15.
 */

case class RawTransaction(raw : String)
object RawTransactionProtocol extends DefaultJsonProtocol {
implicit val rawTransactionFormat = jsonFormat(RawTransaction, "raw")
}
