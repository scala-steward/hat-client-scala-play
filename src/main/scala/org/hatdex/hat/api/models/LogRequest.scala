package org.hatdex.hat.api.models

import play.api.libs.json.{ Format, Json }

case class LogRequest(
    actionCode: String,
    message: Option[String],
    logGroup: Option[String])

object LogRequest {
  implicit val hatLogRequestFormat: Format[LogRequest] = Json.format[LogRequest]
}
