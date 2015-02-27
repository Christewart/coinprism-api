package com.coinprism.config

import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import akka.actor.ActorSystem
trait ActorSystemConfig {

  implicit val timeout = Timeout(5 seconds)
  lazy val actorSystem = ActorSystem("Coinprism-api")
  
}