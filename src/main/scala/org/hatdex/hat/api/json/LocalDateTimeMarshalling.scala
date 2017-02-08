/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.json

import play.api.data.validation.ValidationError
import play.api.libs.json._

trait LocalDateTimeMarshalling {
  implicit object DefaultJodaLocalDateTimeWrites extends Writes[org.joda.time.LocalDateTime] {
    def writes(d: org.joda.time.LocalDateTime): JsValue = JsString(d.toDateTime().toString("yyyy-MM-dd'T'HH:mm:ssZ"))
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
