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

sealed abstract class DataStats(
  statsType: String,
  time: LocalDateTime,
  logEntry: String)

trait StatsForTables {
  val time: LocalDateTime
  val user: User
  val dataTableStats: Option[Seq[DataTableStats]]
}

case class DataCreditStats(
  statsType: String = "datacredit",
  operation: String,
  time: LocalDateTime,
  user: User,
  dataTableStats: Option[Seq[DataTableStats]],
  logEntry: String) extends DataStats("datacredit", time, logEntry) with StatsForTables

case class DataDebitStats(
  statsType: String = "datadebit",
  dataDebit: ApiDataDebit,
  operation: String,
  time: LocalDateTime,
  user: User,
  dataTableStats: Option[Seq[DataTableStats]],
  logEntry: String) extends DataStats("datadebit", time, logEntry) with StatsForTables

case class DataStorageStats(
  statsType: String = "storage",
  time: LocalDateTime,
  dataTableStats: Seq[DataTableStats],
  logEntry: String) extends DataStats("storage", time, logEntry)

sealed trait RichDataStats {
  val user: User
  val stats: Seq[EndpointStats]
}

case class EndpointStats(
  endpoint: String,
  propertyStats: HashMap[String, Long])

case class InboundDataStats(
  statsType: String = "inbound",
  time: LocalDateTime,
  user: User,
  stats: Seq[EndpointStats],
  logEntry: String) extends DataStats("inbound", time, logEntry) with RichDataStats

case class OutboundDataStats(
  statsType: String = "outbound",
  time: LocalDateTime,
  user: User,
  dataDebitId: String,
  stats: Seq[EndpointStats],
  logEntry: String) extends DataStats("outbound", time, logEntry) with RichDataStats
