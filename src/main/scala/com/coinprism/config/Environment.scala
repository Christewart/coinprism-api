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
  def version = v1

  implicit val system = ActorSystem("Coinprism-Api-Actor-System")
  import system.dispatcher
}

trait Production extends Environment {
  override def host = "https://api.coinprism.com/"
}

trait Test extends Environment {
  override def host = "https://private-anon-0d71c0e2f-coinprism.apiary-mock.com/"
}