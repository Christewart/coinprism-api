package com.coinprism.transaction

import spray.json.DefaultJsonProtocol
import com.coinprism.blockchain.BitcoinAddress
import com.coinprism.blockchain.Address
import com.coinprism.blockchain.AddressProtocol
import com.coinprism.blockchain.BitcoinAddressProtocol
case class NewTransaction(fees : Long, from : BitcoinAddress, to : List[Address])
object NewTransactionProtocol extends DefaultJsonProtocol {
  import AddressProtocol._
  import BitcoinAddressProtocol._
  
  implicit val newTransactionFormat = jsonFormat3(NewTransaction)
}