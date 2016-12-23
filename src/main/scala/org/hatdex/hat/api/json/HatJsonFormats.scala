/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.json

import org.hatdex.hat.api.models.ComparisonOperators.ComparisonOperator
import org.hatdex.hat.api.models._
import org.joda.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{ Failure, Success, Try }

trait HatJsonFormats extends HatJsonUtilities with UuidMarshalling with LocalDateTimeMarshalling {
  implicit val userReads = Json.format[User]
  implicit val accessTokenFormat = Json.format[AccessToken]

  implicit val ApiDataFieldFormat: Format[ApiDataField] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "dateCreated").formatNullable[LocalDateTime] and
    (__ \ "lastUpdated").formatNullable[LocalDateTime] and
    (__ \ "tableId").formatNullable[Int] and
    (__ \ "name").format[String] and
    (__ \ "values").lazyFormatNullable(implicitly[Format[Seq[ApiDataValue]]])
  )(ApiDataField.apply, unlift(ApiDataField.unapply))

  implicit val ApiDataTableFormat: Format[ApiDataTable] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "dateCreated").formatNullable[LocalDateTime] and
    (__ \ "lastUpdated").formatNullable[LocalDateTime] and
    (__ \ "name").format[String] and
    (__ \ "source").format[String] and
    (__ \ "fields").formatNullable[Seq[ApiDataField]] and
    (__ \ "subTables").lazyFormatNullable(implicitly[Format[Seq[ApiDataTable]]])
  )(ApiDataTable.apply, unlift(ApiDataTable.unapply))

  implicit val ApiDataRecordFormat: Format[ApiDataRecord] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "dateCreated").formatNullable[LocalDateTime] and
    (__ \ "lastUpdated").formatNullable[LocalDateTime] and
    (__ \ "name").format[String] and
    (__ \ "tables").formatNullable[Seq[ApiDataTable]]
  )(ApiDataRecord.apply, unlift(ApiDataRecord.unapply))

  implicit val ApiDataValueFormat: Format[ApiDataValue] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "dateCreated").formatNullable[LocalDateTime] and
    (__ \ "lastUpdated").formatNullable[LocalDateTime] and
    (__ \ "value").format[String] and
    (__ \ "field").formatNullable[ApiDataField] and
    (__ \ "record").formatNullable[ApiDataRecord]
  )(ApiDataValue.apply, unlift(ApiDataValue.unapply))

  implicit val comparisonOperatorReads: Format[ComparisonOperator] = new Format[ComparisonOperator] {
    def reads(json: JsValue): JsResult[ComparisonOperator] = Try(ComparisonOperators.fromString(json.as[String])) match {
      case Success(operator) => JsSuccess(operator)
      case Failure(e)        => JsError(s"Unexpected JSON value $json")
    }

    def writes(operator: ComparisonOperator): JsValue = {
      Json.toJson(operator.toString)
    }
  }

  implicit val ApiRecordValuesFormat = Json.format[ApiRecordValues]

  implicit val dataSourceDatasetFormat = Json.format[DataSourceDataset]
  implicit val dataSourceStructureFormat = Json.format[DataSourceStructure]

  implicit val ApiBundleTableCondition = Json.format[ApiBundleTableCondition]
  implicit val ApiBundleTableSlice = Json.format[ApiBundleTableSlice]
  implicit val ApiBundleTableFormat = Json.format[ApiBundleTable]
  implicit val ApiBundleCombinationFormat = Json.format[ApiBundleCombination]
  implicit val ApiBundleContextlessFormat = Json.format[ApiBundleContextless]

  implicit val ApiBundleContextPropertySelection = Json.format[ApiBundleContextPropertySelection]
  implicit val ApiBundleContextEntitySelectionFormat = Json.format[ApiBundleContextEntitySelection]

  implicit val ApiBundleContextFormat: Format[ApiBundleContext] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "dateCreated").formatNullable[LocalDateTime] and
    (__ \ "lastUpdated").formatNullable[LocalDateTime] and
    (__ \ "name").format[String] and
    (__ \ "entities").formatNullable[Seq[ApiBundleContextEntitySelection]] and
    (__ \ "bundles").lazyFormatNullable(implicitly[Format[Seq[ApiBundleContext]]])
  )(ApiBundleContext.apply, unlift(ApiBundleContext.unapply))

  implicit val ApiDataDebitFormat = Json.format[ApiDataDebit]

  implicit val DataFieldStatsFormat = Json.format[DataFieldStats]
  implicit val DataTableStatsFormat: Format[DataTableStats] = (
    (__ \ "name").format[String] and
    (__ \ "source").format[String] and
    (__ \ "fields").format[Seq[DataFieldStats]] and
    (__ \ "subTables").lazyFormatNullable(implicitly[Format[Seq[DataTableStats]]]) and
    (__ \ "valueCount").format[Int])(DataTableStats.apply, unlift(DataTableStats.unapply))

  implicit val dataSourceFieldFormat: Format[DataSourceField] = (
    (__ \ "name").format[String] and
    (__ \ "description").format[String] and
    (__ \ 'fields).lazyFormatNullable(implicitly[Format[List[DataSourceField]]]))(DataSourceField.apply, unlift(DataSourceField.unapply))

  implicit val errorMessage: Format[ErrorMessage] = Json.format[ErrorMessage]
  implicit val successResponse: Format[SuccessResponse] = Json.format[SuccessResponse]

  implicit val apiRelationshipFormat: Format[ApiRelationship] = Json.format[ApiRelationship]
  implicit val apiGenericIdFormat: Format[ApiGenericId] = Json.format[ApiGenericId]
}

object HatJsonFormats extends HatJsonFormats
