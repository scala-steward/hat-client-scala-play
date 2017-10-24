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

import scala.util.Try

trait UuidMarshalling {
  object UUIDReads extends Reads[java.util.UUID] {
    def parseUUID(s: String): Option[java.util.UUID] = Try(java.util.UUID.fromString(s)).toOption

    def reads(json: JsValue) = {
      json match {
        case JsString(s) =>
          parseUUID(s).map(JsSuccess(_)).getOrElse(JsError(Seq(JsPath() -> Seq(JsonValidationError("Expected UUID string")))))
        case _ =>
          JsError(Seq(JsPath() -> Seq(JsonValidationError("Expected UUID string"))))
      }
    }
  }

  object UUIDWrites extends Writes[java.util.UUID] {
    def writes(uuid: java.util.UUID): JsValue = JsString(uuid.toString)
  }

  implicit val uuidFormat: Format[java.util.UUID] = Format(UUIDReads, UUIDWrites)
}
