/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.json

import org.hatdex.hat.api.models.{ DataCreditStats, DataDebitStats, DataStats, DataStorageStats }
import play.api.libs.json._

trait DataStatsFormat extends HatJsonFormats {
  implicit val dataDebitStatsFormat = Json.format[DataDebitStats]
  implicit val dataCreditStatsFormat = Json.format[DataCreditStats]
  implicit val dataStorageStatsFormat = Json.format[DataStorageStats]

  implicit val dataStatsFormat: Format[DataStats] = new Format[DataStats] {
    def reads(json: JsValue): JsResult[DataStats] = (json \ "statsType").as[String] match {
      case "dataDebit"  => Json.fromJson[DataDebitStats](json)(dataDebitStatsFormat)
      case "dataCredit" => Json.fromJson[DataDebitStats](json)(dataDebitStatsFormat)
      case "storage"    => Json.fromJson[DataDebitStats](json)(dataDebitStatsFormat)
      case statsType    => JsError(s"Unexpected JSON value $statsType in $json")
    }

    def writes(stats: DataStats): JsValue = {
      stats match {
        case ds: DataDebitStats   => Json.toJson(ds)(dataDebitStatsFormat)
        case ds: DataCreditStats  => Json.toJson(ds)(dataCreditStatsFormat)
        case ds: DataStorageStats => Json.toJson(ds)(dataStorageStatsFormat)
      }
    }
  }
}

object DataStatsFormat
