package com.coinprism.config

import akka.actor.ActorSystem

trait CoinprismEnvironment {
  def host : String
  def v1 = "v1/"
  def addresses = "addresses/"
  def transactions = "transactions/"
  def assets = "assets/"
  def issueAsset = "issueasset"
  def sendAsset : String = "sendasset"
  def sendBitcoin = "sendbitcoin"
  def bitcoinAssetSwap = "bitcoinassetswap"
  def signTransaction = "signtransaction"
  def sendrawtransaction = "sendrawtransaction"
  def version = v1

  def url = host + version
  implicit val coinprismSystem = ActorSystem("Coinprism-Api-Actor-System")
}

trait CoinprismProduction extends CoinprismEnvironment {
  override def host = "https://api.coinprism.com/"
}

trait CoinprismTest extends CoinprismEnvironment {
  override def host = "https://testnet.api.coinprism.com/"
}