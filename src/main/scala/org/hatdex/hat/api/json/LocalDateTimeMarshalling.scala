/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.json

import play.api.data.validation.ValidationError
import play.api.libs.json._

trait LocalDateTimeMarshalling {
  implicit object DefaultJodaLocalDateTimeWrites extends Writes[org.joda.time.LocalDateTime] {
    def writes(d: org.joda.time.LocalDateTime): JsValue = JsString(d.toDateTime.toString)
  }

  implicit val jodaISODateReads: Reads[org.joda.time.LocalDateTime] = new Reads[org.joda.time.LocalDateTime] {
    import org.joda.time.LocalDateTime

    val df = org.joda.time.format.ISODateTimeFormat.dateTimeParser()

    def reads(json: JsValue): JsResult[LocalDateTime] = json match {
      case JsNumber(d) => JsSuccess(new LocalDateTime(d.toLong))
      case JsString(s) => parseDateTime(s) match {
        case Some(d) => JsSuccess(d)
        case None    => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", "ISO8601"))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDateTime(input: String): Option[LocalDateTime] = {
      scala.util.control.Exception.allCatch[LocalDateTime] opt LocalDateTime.parse(input, df)
    }

  }
}
