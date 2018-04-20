package org.hatdex.hat.api.models

case class FormattedText(
    text: String,
    markdown: Option[String],
    html: Option[String])
