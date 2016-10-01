/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

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
  dataTableStats: Option[Seq[DataTableStats]],
  logEntry: String)

case class DataCreditStats(
  statsType: String = "datacredit",
  operation: String,
  time: LocalDateTime,
  user: User,
  dataTableStats: Option[Seq[DataTableStats]],
  logEntry: String) extends DataStats("datacredit", time, dataTableStats, logEntry)

case class DataDebitStats(
  statsType: String = "datadebit",
  dataDebit: ApiDataDebit,
  operation: String,
  time: LocalDateTime,
  user: User,
  dataTableStats: Option[Seq[DataTableStats]],
  logEntry: String) extends DataStats("datadebit", time, dataTableStats, logEntry)

case class DataStorageStats(
  statsType: String = "storage",
  time: LocalDateTime,
  dataTableStats: Seq[DataTableStats],
  logEntry: String) extends DataStats("storage", time, Some(dataTableStats), logEntry)