package com.coinprism.blockchain

import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsNull
import spray.json.JsArray
import spray.json.JsBoolean
import spray.json.JsObject
import spray.json.JsNumber

case class AssetMetaInfo(asset_id: String, metadata_url: String, verified_issuer: Boolean,
  name: String, contract_url: Option[String], name_short: String, issuer: Option[String],
  description: String, description_mime: String, asset_type: String, divisibility: Long,
  icon_url: String, image_url: String)
object AssetMetaInfoProtocol extends DefaultJsonProtocol {

  implicit object AssetMetaInfoProtocol extends RootJsonFormat[AssetMetaInfo] {

    override def read(jsValue: JsValue): AssetMetaInfo = {
      val obj = jsValue.asJsObject
      val fields = obj.fields
      val asset_id = fields("asset_id").convertTo[String]
      val metadata_url = fields("metadata_url").convertTo[String]
      val verified_issuer = fields("verified_issuer").convertTo[Boolean]
      val name = fields("name").convertTo[String]
      val contract_url = fields("contract_url") match {
        case JsString(s) => Some(s)
        case JsNull => None
        case _ => throw new RuntimeException("Contact_url should always be either a string or null")
      }
      val name_short = fields("name_short").convertTo[String]
      val issuer = fields("issuer") match {
        case JsString(s) => Some(s)
        case JsNull => None
        case _ => throw new RuntimeException("Issuer for AssetMetaInfo must either be null or a string")
      }
      val description = fields("description").convertTo[String]
      val description_mime = fields("description_mime").convertTo[String]
      val asset_type = fields("type").convertTo[String]
      val divisibility = fields("divisibility").convertTo[Long]
      val icon_url = fields("icon_url").convertTo[String]
      val image_url = fields("image_url").convertTo[String]
      AssetMetaInfo(asset_id, metadata_url, verified_issuer, name, contract_url, name_short, issuer, description,
        description_mime, asset_type, divisibility, icon_url, image_url)
    }

    override def write(info: AssetMetaInfo) = {
      val m: Map[String, JsValue] = Map(
        "asset_id" -> JsString(info.asset_id),
        "metadata_url" -> JsString(info.metadata_url),
        "verified_issuer" -> JsBoolean(info.verified_issuer),
        "name" -> JsString(info.name),
        "contract_url" -> (info.contract_url  match { 
          case Some(c) => JsString(c)
          case None => JsNull
        }),
        "name_short" -> JsString(info.name_short),
        "issuer" -> (info.issuer match {
          case Some(a) => JsString(a)
          case None => JsNull
        }),
        "description" -> JsString(info.description),
        "description_mime" -> JsString(info.description_mime),
        "asset_type" -> JsString(info.asset_type),
        "divisibility" -> JsNumber(info.divisibility),
        "icon_url" -> JsString(info.icon_url),
        "image_url" -> JsString(info.image_url))
      JsObject(m)
    }
  }
}