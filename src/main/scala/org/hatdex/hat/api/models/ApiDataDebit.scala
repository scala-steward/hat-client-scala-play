/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

import java.util.UUID

import org.joda.time.LocalDateTime

case class ApiDataDebit(
  key: Option[UUID],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  enabled: Option[Boolean],
  rolling: Boolean,
  sell: Boolean,
  price: Float,
  kind: String,
  bundleContextless: Option[ApiBundleContextless],
  bundleContextual: Option[ApiBundleContext])

case class ApiDataDebitOut(
  key: Option[UUID],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  enabled: Option[Boolean],
  rolling: Boolean,
  sell: Boolean,
  price: Float,
  kind: String,
  bundleContextless: Option[ApiBundleContextlessData],
  bundleContextual: Option[Seq[ApiEntity]]
)
