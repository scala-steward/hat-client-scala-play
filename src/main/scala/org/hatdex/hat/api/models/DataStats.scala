/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

import scala.collection.immutable.HashMap

case class DataFieldStats(
    name: String,
    tableName: String,
    tableSource: String,
    valueCount: Int)

case class DataTableStats(
    name: String,
    source: String,
    fields: Seq[DataFieldStats],
    subTables: Option[Seq[DataTableStats]],
    valueCount: Int)

sealed trait DataStats {
  val statsType: String
  val time: LocalDateTime
  val logEntry: String
}

trait StatsForTables {
  val time: LocalDateTime
  val user: User
  val dataTableStats: Option[Seq[DataTableStats]]
}

case class DataCreditStats(
    operation: String,
    time: LocalDateTime,
    user: User,
    dataTableStats: Option[Seq[DataTableStats]],
    logEntry: String) extends DataStats with StatsForTables {
  final val statsType: String = "datacredit"
}

case class DataDebitStats(
    dataDebit: ApiDataDebit,
    operation: String,
    time: LocalDateTime,
    user: User,
    dataTableStats: Option[Seq[DataTableStats]],
    logEntry: String) extends DataStats with StatsForTables {
  final val statsType: String = "datadebit"
}

case class DataStorageStats(
    time: LocalDateTime,
    dataTableStats: Seq[DataTableStats],
    logEntry: String) extends DataStats {
  final val statsType: String = "storage"
}

sealed trait RichDataStats {
  val user: User
  val stats: Seq[EndpointStats]
}

case class EndpointStats(
    endpoint: String,
    propertyStats: HashMap[String, Long])

case class InboundDataStats(
    time: LocalDateTime,
    user: User,
    stats: Seq[EndpointStats],
    logEntry: String) extends DataStats with RichDataStats {
  final val statsType: String = "inbound"
}

case class OutboundDataStats(
    time: LocalDateTime,
    user: User,
    dataDebitId: String,
    stats: Seq[EndpointStats],
    logEntry: String) extends DataStats with RichDataStats {
  final val statsType: String = "outbound"
}

case class DataDebitEvent(
    dataDebit: RichDataDebit,
    operation: String,
    time: LocalDateTime,
    user: User,
    logEntry: String) extends DataStats {
  final val statsType: String = "datadebitEvent"
}
