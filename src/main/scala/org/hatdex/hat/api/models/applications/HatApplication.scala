package org.hatdex.hat.api.models.applications

import org.joda.time.DateTime

case class HatApplication(
    application: Application,
    setup: Boolean,
    active: Boolean,
    applicationToken: Option[String],
    needsUpdating: Option[Boolean],
    mostRecentData: Option[DateTime])
