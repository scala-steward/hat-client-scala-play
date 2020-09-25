package org.hatdex.hat.api.models

import play.api.libs.json._

case class PdaEmailVerificationRequest(
    email: String,
    applicationId: String)

object ApiAuthenticationFormats {
  implicit val passwordResetRequestFormat: OFormat[PdaEmailVerificationRequest] = Json.format[PdaEmailVerificationRequest]
}
