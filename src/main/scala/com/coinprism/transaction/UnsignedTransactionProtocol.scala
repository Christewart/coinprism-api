package com.coinprism.transaction

import spray.json.{JsNull, JsValue, RootJsonFormat, DefaultJsonProtocol}
import com.coinprism.blockchain.OutputProtocol
import com.coinprism.blockchain.InputProtocol
import com.coinprism.blockchain.Output
import com.coinprism.blockchain.Input
case class UnsignedTransaction(inputs: Option[List[Input]], outputs: Option[List[Output]],
                               amount : Option[Long],
                               fees : Option[Long], raw : Option[String])
object UnsignedTransactionProtocol extends DefaultJsonProtocol {
  import OutputProtocol._
  import InputProtocol._

  implicit object UnsignedTransactionFormat extends RootJsonFormat[UnsignedTransaction]  {

    /**
     * We have two formats that an unsigned transaction can be in, one is Raw (hexadecimal encoding)
     * the other is a json format
     * @param jsValue
     * @return
     */
  override def read(jsValue : JsValue ) = {
    val obj = jsValue.asJsObject
      //check if we received a raw transaction
    if (obj.fields.keySet.exists(s => s == "raw")) {
      val raw = obj.fields("raw").convertTo[String]
      UnsignedTransaction(None,None,None,None,Some(raw))
    }
    else {
      unsignedTransactionFormat.read(jsValue)
    }
  }


    override def write(uTx : UnsignedTransaction) = {
      JsNull
    }

  }

  val unsignedTransactionFormat = jsonFormat5(UnsignedTransaction.apply)


}