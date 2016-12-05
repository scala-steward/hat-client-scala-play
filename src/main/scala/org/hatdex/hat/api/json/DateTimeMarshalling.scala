/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.json

import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._

trait DateTimeMarshalling {
  implicit object DefaultJodaDateTimeWrites extends Writes[org.joda.time.DateTime] {
    def writes(d: org.joda.time.DateTime): JsValue = JsString(d.toString)
  }

  implicit val jodaISODateReads: Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    import org.joda.time.DateTime

    val df = org.joda.time.format.ISODateTimeFormat.dateTimeParser()

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
      case JsString(s) => parseDateTime(s) match {
        case Some(d) => JsSuccess(d)
        case None    => JsSuccess(new DateTime(0)) // JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", "ISO8601"))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDateTime(input: String): Option[DateTime] = {
      scala.util.control.Exception.allCatch[DateTime] opt DateTime.parse(input, df)
    }

  }
}
