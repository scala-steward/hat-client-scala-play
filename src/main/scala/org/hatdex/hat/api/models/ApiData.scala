/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

case class ApiDataField(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  tableId: Option[Int],
  name: String,
  values: Option[Seq[ApiDataValue]])

case class ApiDataRecord(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  tables: Option[Seq[ApiDataTable]])

case class ApiDataTable(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  source: String,
  fields: Option[Seq[ApiDataField]],
  subTables: Option[Seq[ApiDataTable]])

case class ApiDataValue(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  value: String,
  field: Option[ApiDataField],
  record: Option[ApiDataRecord])

case class ApiRecordValues(
  record: ApiDataRecord,
  values: Seq[ApiDataValue])