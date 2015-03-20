package com.coinprism.transaction

import com.coinprism.transaction.AssetDestinationProtocol.AssetAddressFormat
import spray.json._

import scala.collection.breakOut

/**
 * Created by chris on 3/15/15.
 */
case class UnsignedTxHexWithPrivateKey(hex : String, privateKeys : List[String])

object UnsignedTxHexWithPrivateKeyProtocol extends DefaultJsonProtocol {

  implicit object UnsignedTxHexWithPrivateKeyFormat extends RootJsonFormat[UnsignedTxHexWithPrivateKey] {
    override def read(jsValue : JsValue) = {
      val obj = jsValue.asJsObject
      val Seq(hex, pivateKeyArray) = obj.getFields("transaction", "keys")

      // convert JsArray to List[ AssetDestination ]
      val privateKeyList : List[String] = pivateKeyArray match {
        case ja: JsArray => {
          ja.elements.toList.map(k => k.convertTo[String])
        }
        case _ => throw new RuntimeException(
          "Expected an array go for the privatey keys, got something else")
      }
      UnsignedTxHexWithPrivateKey(hex.convertTo[String],privateKeyList)
  }

    override def write(unsignedTxWithPrivateKey : UnsignedTxHexWithPrivateKey ) = {


      val privateKeys  = unsignedTxWithPrivateKey.privateKeys.map(k =>
        JsString(k))(breakOut): Vector[JsValue]

      val m : Map[String,JsValue]  = Map("transaction" ->
        JsString(unsignedTxWithPrivateKey.hex),
        "keys" -> JsArray(privateKeys))

      JsObject(m)
    }

  }
}
