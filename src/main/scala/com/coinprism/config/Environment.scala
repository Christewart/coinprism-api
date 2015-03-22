package com.coinprism.config

import akka.actor.ActorSystem

trait Environment {
  def host: String
  def v1 = "v1/"
  def addresses = "addresses/"
  def transactions = "transactions/"
  def assets = "assets/"
  def issueAsset = "issueasset"
  def sendAsset : String = "sendasset"
  def sendBitcoin = "sendbitcoin"
  def bitcoinAssetSwap = "bitcoinassetswap"
  def signTransaction = "signtransaction"
  def version = v1

  implicit val coinprismSystem = ActorSystem("Coinprism-Api-Actor-System")
}

trait CoinprismProduction extends Environment {
  override def host = "https://api.coinprism.com/"
}

trait CoinprismTest extends Environment {
  override def host = "https://private-anon-0d71c0e2f-coinprism.apiary-mock.com/"
}