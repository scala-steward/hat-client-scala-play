package org.hatdex.hat.api.models

import play.api.libs.json.JsValue

case class PayloadWrapper(
    data: JsValue,
    next: Option[String] = None,
    startId: Option[String] = None,
    limit: Option[Int] = None)
