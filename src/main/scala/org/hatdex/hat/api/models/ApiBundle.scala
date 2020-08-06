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

import scala.collection.immutable.Map

@Deprecated
object ComparisonOperators {
  @Deprecated
  sealed trait ComparisonOperator
  case object equal extends ComparisonOperator
  case object notEqual extends ComparisonOperator
  case object greaterThan extends ComparisonOperator
  case object lessThan extends ComparisonOperator
  case object like extends ComparisonOperator
  //  case object dateGreaterThan extends ComparisonOperator
  //  case object dateLessThan extends ComparisonOperator
  //  case object dateWeekdayGreaterThan extends ComparisonOperator
  //  case object dateWeekdayLessThan extends ComparisonOperator
  //  case object dateHourGreaterThan extends ComparisonOperator
  //  case object dateHourLessThan extends ComparisonOperator

  def fromString(value: String): ComparisonOperator =
    Vector(
      equal,
      notEqual,
      greaterThan,
      lessThan,
      like //      dateGreaterThan, dateLessThan,
      //      dateWeekdayGreaterThan, dateWeekdayLessThan,
      //      dateHourGreaterThan, dateHourLessThan
    ).find(_.toString == value).get

  val comparisonOperators: Set[ComparisonOperator] = Set(equal, notEqual, greaterThan, lessThan, like)
  //    dateGreaterThan, dateLessThan, dateWeekdayGreaterThan, dateWeekdayLessThan, dateHourGreaterThan, dateHourLessThan)
}

import org.hatdex.hat.api.models.ComparisonOperators.ComparisonOperator

@Deprecated
case class ApiBundleTableCondition(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    field: ApiDataField,
    value: String,
    operator: ComparisonOperator)

@Deprecated
case class ApiBundleTableSlice(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    table: ApiDataTable,
    conditions: Seq[ApiBundleTableCondition])

@Deprecated
case class ApiBundleTable(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    table: ApiDataTable, // Used to tag which table is bundled
    slices: Option[Seq[ApiBundleTableSlice]],
    data: Option[Seq[ApiDataRecord]]) // Data is optional, only used on the outbound

@Deprecated
case class ApiBundleCombination(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    bundleTable: ApiBundleTable,
    bundleJoinField: Option[ApiDataField],
    bundleTableField: Option[ApiDataField],
    operator: Option[ComparisonOperator])

@Deprecated
case class ApiBundleDataSourceField(
    name: String,
    description: String,
    fields: Option[List[ApiBundleDataSourceField]])
@Deprecated
case class ApiBundleDataSourceDataset(
    name: String,
    description: String,
    fields: List[ApiBundleDataSourceField])
@Deprecated
case class ApiBundleDataSourceStructure(
    source: String,
    datasets: List[ApiBundleDataSourceDataset])

@Deprecated
case class ApiBundleContextless(
    id: Option[Int],
    dateCreated: Option[LocalDateTime],
    lastUpdated: Option[LocalDateTime],
    name: String,
    sources: Option[Seq[ApiBundleDataSourceStructure]])

@Deprecated
case class ApiBundleContextlessDatasetData(
    name: String,
    table: ApiDataTable, // Used to tag which table is bundled
    data: Option[Seq[ApiDataRecord]]) // Data is optional, only used on the outbound

@Deprecated
case class ApiBundleContextlessData(
    id: Int,
    name: String,
    dataGroups: Map[String, Seq[ApiBundleContextlessDatasetData]])
