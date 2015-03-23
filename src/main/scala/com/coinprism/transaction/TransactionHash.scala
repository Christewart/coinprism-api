package com.coinprism.transaction

import spray.json.DefaultJsonProtocol

/**
 * Created by chris on 3/23/15.
 */
case class TransactionHash(hash : String)

object TransactionHashFormat extends DefaultJsonProtocol {

  implicit val transactionHashFormat = jsonFormat(TransactionHash, "hash")

}
