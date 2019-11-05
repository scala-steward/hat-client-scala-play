package org.hatdex.hat.api.models.applications

import org.joda.time.DateTime

case class HatApplication(
    application: Application,
    setup: Boolean,
    enabled: Boolean,
    active: Boolean,
    needsUpdating: Option[Boolean],
    dependenciesEnabled: Option[Boolean],
    mostRecentData: Option[DateTime])
