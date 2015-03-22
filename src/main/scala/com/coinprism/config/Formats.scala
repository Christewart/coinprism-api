package com.coinprism.config

/**
 * Created by chris on 3/22/15.
 */
object Formats {
  sealed trait ApiFormats
  case object Json extends ApiFormats
  case object Raw extends ApiFormats

  def correctFormat(uri : String, format : ApiFormats) : String = {
    format match {
      case Json => uri + "?format=json"
      case Raw => uri + "?format=raw"
    }
  }
}
