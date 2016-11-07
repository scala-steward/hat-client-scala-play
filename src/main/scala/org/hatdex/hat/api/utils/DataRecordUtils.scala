/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.utils

import org.hatdex.hat.api.models.{ ApiDataRecord, ApiDataTable }
import org.joda.time.LocalDateTime

trait DataRecordUtils {
  /*
   * Reformats API Data table with values to a format where field/subtable name is an object key and values or subtables are the values
   */
  def flattenTableValues(dataTable: ApiDataTable): Map[String, Any] = {
    val fieldObjects = dataTable.fields.map { fields =>
      Map[String, Any](
        fields flatMap { field =>
          val maybeValues = field.values match {
            case Some(values) if values.isEmpty     => None
            case Some(values) if values.length == 1 => Some(values.head.value)
            case Some(values)                       => Some(values.map(_.value))
            case None                               => None
          }
          maybeValues.map { values => field.name -> values }
        }: _*)
    }

    val subtableObjects = dataTable.subTables.map { subtables =>
      Map[String, Any](subtables map { subtable =>
        subtable.name -> flattenTableValues(subtable)
      }: _*)
    }

    fieldObjects.getOrElse(Map()) ++ subtableObjects.getOrElse(Map())
  }

  def flattenRecordValues(record: ApiDataRecord): Map[String, Any] = {
    val recordDataTables = Map(record.tables.getOrElse(Seq()).map { table =>
      table.name -> flattenTableValues(table)
    }: _*)
    Map(
      "id" -> record.id.get,
      "name" -> record.name,
      "lastUpdated" -> record.lastUpdated.getOrElse(LocalDateTime.now()).toDateTime.toString(),
      "data" -> recordDataTables
    )
  }
}
