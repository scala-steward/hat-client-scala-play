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
import java.util.UUID

trait UuidMarshalling {
  object UUIDReads extends Reads[UUID] {
    def parseUUID(s: String): Option[UUID] = Try(UUID.fromString(s)).toOption

    def reads(json: JsValue): JsResult[UUID] =
      json match {
        case JsString(s) =>
          parseUUID(s)
            .map(JsSuccess(_))
            .getOrElse(JsError(Seq(JsPath() -> Seq(JsonValidationError("Expected UUID string")))))
        case _ =>
          JsError(Seq(JsPath() -> Seq(JsonValidationError("Expected UUID string"))))
      }
  }

  object UUIDWrites extends Writes[UUID] {
    def writes(uuid: UUID): JsValue = JsString(uuid.toString)
  }

  implicit val uuidFormat: Format[UUID] = Format(UUIDReads, UUIDWrites)
}
