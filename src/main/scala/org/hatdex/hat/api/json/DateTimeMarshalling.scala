/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.json

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
      case _ => JsError(Seq(JsPath() -> Seq(JsonValidationError("validate.error.expected.date"))))
    }

    private def parseDateTime(input: String): Option[DateTime] = {
      scala.util.control.Exception.allCatch[DateTime] opt DateTime.parse(input, df)
    }

  }
}
