package com.coinprism.config

import akka.actor.ActorSystem

trait CoinprismEnvironment {
  def coinprismHost : String
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
  def coinprismUrl = coinprismHost + version
  implicit val coinprismSystem = ActorSystem("Coinprism-Api-Actor-System")

}

trait CoinprismProduction extends CoinprismEnvironment {
  override def coinprismHost = "https://api.coinprism.com/"
}

trait CoinprismTest extends CoinprismEnvironment {
  override def coinprismHost = "https://testnet.api.coinprism.com/"
}