/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.json

import org.hatdex.hat.api.models._
import org.joda.time.LocalDateTime
import play.api.libs.json._

trait HatJsonUtilities {
  /*
   * Reformats API Data table with values to a format where field/subtable name is an object key and values or subtables are the values
   */
  def flattenTableValues(dataTable: ApiDataTable): JsValue = {
    val fieldObjects = dataTable.fields.map { fields =>
      Map[String, JsValue](
        fields flatMap { field =>
          val maybeValues = field.values collect {
            case values if values.length == 1 => Json.toJson(values.head.value)
            case values if values.nonEmpty    => Json.toJson(values.map(_.value))
          }
          maybeValues.map(values => field.name -> values)
        }: _*)
    }

    val subtableObjects = dataTable.subTables.map { subtables =>
      Map[String, JsValue](subtables map { subtable =>
        subtable.name -> flattenTableValues(subtable)
      }: _*)
    }

    JsObject(fieldObjects.getOrElse(Map()) ++ subtableObjects.getOrElse(Map()))
  }

  def flattenRecordValues(record: ApiDataRecord): JsObject = {
    val recordDataTables = Map(record.tables.getOrElse(Seq()).map { table =>
      table.name -> flattenTableValues(table)
    }: _*)
    JsObject(Map(
      "id" -> Json.toJson(record.id.get),
      "name" -> Json.toJson(record.name),
      "lastUpdated" -> Json.toJson(record.lastUpdated.getOrElse(LocalDateTime.now()).toDateTime.toString()),
      "data" -> JsObject(recordDataTables)
    ))
  }
}

