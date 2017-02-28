/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

import java.util.UUID

import org.joda.time.LocalDateTime
import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

object StatusKind {
  sealed trait Kind {
    val kind: String
  }
  case class Numeric(metric: BigDecimal, units: Option[String], kind: String = "Numeric") extends Kind
  case class Text(metric: String, units: Option[String], kind: String = "Text") extends Kind
}
case class HatStatus(title: String, kind: StatusKind.Kind)
