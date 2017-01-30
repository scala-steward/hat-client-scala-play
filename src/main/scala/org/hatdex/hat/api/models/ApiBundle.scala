/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

import scala.collection.immutable.Map

object ComparisonOperators {
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

  def fromString(value: String): ComparisonOperator = {
    Vector(
      equal, notEqual, greaterThan, lessThan, like //      dateGreaterThan, dateLessThan,
      //      dateWeekdayGreaterThan, dateWeekdayLessThan,
      //      dateHourGreaterThan, dateHourLessThan
      ).find(_.toString == value).get
  }

  val comparisonOperators: Set[ComparisonOperator] = Set(equal, notEqual, greaterThan, lessThan, like)
  //    dateGreaterThan, dateLessThan, dateWeekdayGreaterThan, dateWeekdayLessThan, dateHourGreaterThan, dateHourLessThan)
}

import org.hatdex.hat.api.models.ComparisonOperators.ComparisonOperator

case class ApiBundleTableCondition(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  field: ApiDataField,
  value: String,
  operator: ComparisonOperator)

case class ApiBundleTableSlice(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  table: ApiDataTable,
  conditions: Seq[ApiBundleTableCondition])

case class ApiBundleTable(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  table: ApiDataTable, // Used to tag which table is bundled
  slices: Option[Seq[ApiBundleTableSlice]],
  data: Option[Seq[ApiDataRecord]])

// Data is optional, only used on the outbound

case class ApiBundleCombination(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  bundleTable: ApiBundleTable,
  bundleJoinField: Option[ApiDataField],
  bundleTableField: Option[ApiDataField],
  operator: Option[ComparisonOperator])

case class ApiBundleDataSourceField(name: String, description: String, fields: Option[List[ApiBundleDataSourceField]])
case class ApiBundleDataSourceDataset(name: String, description: String, fields: List[ApiBundleDataSourceField])
case class ApiBundleDataSourceStructure(source: String, datasets: List[ApiBundleDataSourceDataset])

case class ApiBundleContextless(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  sources: Option[Seq[ApiBundleDataSourceStructure]])

case class ApiBundleContextlessDatasetData(
  name: String,
  table: ApiDataTable, // Used to tag which table is bundled
  data: Option[Seq[ApiDataRecord]])

// Data is optional, only used on the outbound

case class ApiBundleContextlessData(
  id: Int,
  name: String,
  dataGroups: Map[String, Seq[ApiBundleContextlessDatasetData]])
