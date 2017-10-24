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
    bundleContextual: Option[Seq[ApiEntity]])
