/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.json

import org.hatdex.hat.api.models.ComparisonOperators.ComparisonOperator
import org.hatdex.hat.api.models._
import org.joda.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{ Failure, Success, Try }

trait HatJsonFormats extends HatJsonUtilities with UuidMarshalling with LocalDateTimeMarshalling {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val userRoleFormat: Format[UserRole] = new Format[UserRole] {
    def reads(json: JsValue): JsResult[UserRole] = {
      val role   = (json \ "role").as[String]
      val detail = (json \ "detail").asOpt[String]
      JsSuccess(UserRole.userRoleDeserialize(role, detail))
    }

    def writes(role: UserRole): JsValue =
      Json.toJson(
        Map("role" -> Option(role.title), "detail" -> role.extra)
          .filter(_._2.isDefined)
      )
  }

  implicit val userReads: OFormat[User]                = Json.format[User]
  implicit val accessTokenFormat: OFormat[AccessToken] = Json.format[AccessToken]

  implicit val ApiDataFieldFormat: Format[ApiDataField] = ((__ \ "id").formatNullable[Int] and
      (__ \ "dateCreated").formatNullable[LocalDateTime] and
      (__ \ "lastUpdated").formatNullable[LocalDateTime] and
      (__ \ "tableId").formatNullable[Int] and
      (__ \ "name").format[String] and
      (__ \ "values")
        .lazyFormatNullable(implicitly[Format[Seq[ApiDataValue]]]))(ApiDataField.apply, unlift(ApiDataField.unapply))

  implicit val ApiDataTableFormat: Format[ApiDataTable] = ((__ \ "id").formatNullable[Int] and
      (__ \ "dateCreated").formatNullable[LocalDateTime] and
      (__ \ "lastUpdated").formatNullable[LocalDateTime] and
      (__ \ "name").format[String] and
      (__ \ "source").format[String] and
      (__ \ "fields").formatNullable[Seq[ApiDataField]] and
      (__ \ "subTables")
        .lazyFormatNullable(implicitly[Format[Seq[ApiDataTable]]]))(ApiDataTable.apply, unlift(ApiDataTable.unapply))

  implicit val ApiDataRecordFormat: Format[ApiDataRecord] = ((__ \ "id").formatNullable[Int] and
      (__ \ "dateCreated").formatNullable[LocalDateTime] and
      (__ \ "lastUpdated").formatNullable[LocalDateTime] and
      (__ \ "name").format[String] and
      (__ \ "tables").formatNullable[Seq[ApiDataTable]])(ApiDataRecord.apply, unlift(ApiDataRecord.unapply))

  implicit val ApiDataValueFormat: Format[ApiDataValue] = ((__ \ "id").formatNullable[Int] and
      (__ \ "dateCreated").formatNullable[LocalDateTime] and
      (__ \ "lastUpdated").formatNullable[LocalDateTime] and
      (__ \ "value").format[String] and
      (__ \ "field").formatNullable[ApiDataField] and
      (__ \ "record").formatNullable[ApiDataRecord])(ApiDataValue.apply, unlift(ApiDataValue.unapply))

  implicit val comparisonOperatorReads: Format[ComparisonOperator] = new Format[ComparisonOperator] {
    def reads(json: JsValue): JsResult[ComparisonOperator] =
      Try(ComparisonOperators.fromString(json.as[String])) match {
        case Success(operator) => JsSuccess(operator)
        case Failure(e)        => JsError(s"Unexpected JSON value $json")
      }

    def writes(operator: ComparisonOperator): JsValue =
      Json.toJson(operator.toString)
  }

  implicit val ApiRecordValuesFormat: OFormat[ApiRecordValues] = Json.format[ApiRecordValues]

  implicit val ApiBundleDataSourceFieldFormat: Format[ApiBundleDataSourceField] = ((__ \ "name").format[String] and
      (__ \ "description").format[String] and
      (__ \ "fields").lazyFormatNullable(implicitly[Format[List[ApiBundleDataSourceField]]]))(
    ApiBundleDataSourceField.apply,
    unlift(ApiBundleDataSourceField.unapply)
  )


  implicit val ApiBundleDataSourceDatasetFormat: OFormat[ApiBundleDataSourceDataset] =
    Json.format[ApiBundleDataSourceDataset]
  implicit val ApiBundleDataSourceStructureFormat: OFormat[ApiBundleDataSourceStructure] =
    Json.format[ApiBundleDataSourceStructure]
  implicit val ApiBundleContextlessFormat: OFormat[ApiBundleContextless] = Json.format[ApiBundleContextless]

  implicit val ApiBundleContextPropertySelection: OFormat[ApiBundleContextPropertySelection] =
    Json.format[ApiBundleContextPropertySelection]
  implicit val ApiBundleContextEntitySelectionFormat: OFormat[ApiBundleContextEntitySelection] =
    Json.format[ApiBundleContextEntitySelection]

  implicit val ApiBundleContextFormat: Format[ApiBundleContext] = ((__ \ "id").formatNullable[Int] and
      (__ \ "dateCreated").formatNullable[LocalDateTime] and
      (__ \ "lastUpdated").formatNullable[LocalDateTime] and
      (__ \ "name").format[String] and
      (__ \ "entities").formatNullable[Seq[ApiBundleContextEntitySelection]] and
      (__ \ "bundles").lazyFormatNullable(implicitly[Format[Seq[ApiBundleContext]]]))(ApiBundleContext.apply,
                                                                                      unlift(ApiBundleContext.unapply)
  )

  implicit val DataFieldStatsFormat: OFormat[DataFieldStats] = Json.format[DataFieldStats]
  implicit val DataTableStatsFormat: Format[DataTableStats] = ((__ \ "name").format[String] and
      (__ \ "source").format[String] and
      (__ \ "fields").format[Seq[DataFieldStats]] and
      (__ \ "subTables").lazyFormatNullable(implicitly[Format[Seq[DataTableStats]]]) and
      (__ \ "valueCount").format[Int])(DataTableStats.apply, unlift(DataTableStats.unapply))

  implicit val errorMessage: Format[ErrorMessage]       = Json.format[ErrorMessage]
  implicit val successResponse: Format[SuccessResponse] = Json.format[SuccessResponse]

  implicit val apiGenericIdFormat: Format[ApiGenericId] = Json.format[ApiGenericId]

  implicit val statusNewFormat: Format[HatFileStatus.New]                 = Json.format[HatFileStatus.New]
  implicit val statusInitializedFormat: Format[HatFileStatus.Initialized] = Json.format[HatFileStatus.Initialized]
  implicit val statusCompletedFormat: Format[HatFileStatus.Completed]     = Json.format[HatFileStatus.Completed]
  implicit val statusDeletedFormat: Format[HatFileStatus.Deleted]         = Json.format[HatFileStatus.Deleted]

  implicit val apiHatFileStatusFormat: Format[HatFileStatus.Status] = new Format[HatFileStatus.Status] {
    def reads(json: JsValue): JsResult[HatFileStatus.Status] =
      (json \ "status").as[String] match {
        case "Initialized" => Json.fromJson[HatFileStatus.Initialized](json)(statusInitializedFormat)
        case "New"         => Json.fromJson[HatFileStatus.New](json)(statusNewFormat)
        case "Completed"   => Json.fromJson[HatFileStatus.Completed](json)(statusCompletedFormat)
        case "Deleted"     => Json.fromJson[HatFileStatus.Deleted](json)(statusDeletedFormat)
        case status        => JsError(s"Unexpected JSON value $status in $json")
      }

    def writes(stats: HatFileStatus.Status): JsValue =
      stats match {
        case ds: HatFileStatus.Initialized => Json.toJson(ds)(statusInitializedFormat)
        case ds: HatFileStatus.New         => Json.toJson(ds)(statusNewFormat)
        case ds: HatFileStatus.Completed   => Json.toJson(ds)(statusCompletedFormat)
        case ds: HatFileStatus.Deleted     => Json.toJson(ds)(statusDeletedFormat)
      }
  }

  implicit val apiHatFilePermissionFormat: Format[ApiHatFilePermissions] = Json.format[ApiHatFilePermissions]
  implicit val apiHatFileFormat: Format[ApiHatFile]                      = Json.format[ApiHatFile]

  implicit val statusKindNumericFormat: Format[StatusKind.Numeric] = Json.format[StatusKind.Numeric]
  implicit val statusKindTextFormat: Format[StatusKind.Text]       = Json.format[StatusKind.Text]
  implicit val hatStatusKindFormat: Format[StatusKind.Kind] = new Format[StatusKind.Kind] {
    def reads(json: JsValue): JsResult[StatusKind.Kind] =
      (json \ "kind").as[String] match {
        case "Numeric" => Json.fromJson[StatusKind.Numeric](json)(statusKindNumericFormat)
        case "Text"    => Json.fromJson[StatusKind.Text](json)(statusKindTextFormat)
        case kind      => JsError(s"Unexpected JSON value $kind in $json")
      }

    def writes(stats: StatusKind.Kind): JsValue =
      stats match {
        case ds: StatusKind.Numeric => Json.toJson(ds)(statusKindNumericFormat)
        case ds: StatusKind.Text    => Json.toJson(ds)(statusKindTextFormat)
      }
  }
  implicit val hatStatusFormat: Format[HatStatus] = Json.format[HatStatus]

  implicit val hatServiceFormat: Format[HatService] = Json.format[HatService]
}

object HatJsonFormats extends HatJsonFormats
