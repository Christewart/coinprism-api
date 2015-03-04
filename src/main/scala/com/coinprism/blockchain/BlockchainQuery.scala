package com.coinprism.blockchain

import scala.concurrent.Future
import spray.http.HttpRequest
import akka.actor.ActorSystem
import com.coinprism.config.Constants
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import com.coinprism.config.Constants
import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http._
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport._
import akka.util.Timeout

abstract class Address(val value: String)
case class BitcoinAddress(override val value: String) extends Address(value)
case class AssetAddress(override val value: String) extends Address(value)

trait BlockchainQuery {
  import Constants._
  implicit val system = ActorSystem()
  import system.dispatcher
  private def path = v1 + addresses

  def getAddress(address: Address): Future[AddressBalance] = {
    import AssetBalanceProtocol._
    import AddressBalanceProtocol._
    val pipeline: HttpRequest => Future[AddressBalance] = sendReceive ~> unmarshal[AddressBalance]
    pipeline(Get(host + path + address.value))
  }

  def getUnspentTXOs(address: Address): Future[List[UnspentTXO]] = {
    import UnspentTXOProtocol._
    val pipeline: HttpRequest => Future[List[UnspentTXO]] =
      sendReceive ~> unmarshal[List[UnspentTXO]]
    pipeline(Get(host + path + address.value + "/unspents"))
  }

}