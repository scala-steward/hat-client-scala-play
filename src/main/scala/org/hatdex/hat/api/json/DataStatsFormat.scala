/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.json

import org.hatdex.hat.api.models._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

import scala.collection.immutable.HashMap

trait DataStatsFormat extends DataDebitFormats {
  implicit val mapReads: Reads[HashMap[String, Long]] = new Reads[HashMap[String, Long]] {
    def reads(jv: JsValue): JsResult[HashMap[String, Long]] = {
      val fields: Seq[(String, Long)] = jv.as[JsObject].fields.map {
        case (k, v) =>
          k -> v.as[Long]
      }
      JsSuccess(HashMap[String, Long](fields: _*))
    }
  }

  implicit val mapWrites: Writes[HashMap[String, Long]] = new Writes[HashMap[String, Long]] {
    def writes(map: HashMap[String, Long]): JsValue =
      Json.obj(map.map {
        case (s, o) =>
          val ret: (String, JsValueWrapper) = s.toString -> JsNumber(o)
          ret
      }.toSeq: _*)
  }

  implicit val mapFormat: Format[HashMap[String, Long]] = Format(mapReads, mapWrites)

  implicit val endpointStatsFormat: Format[EndpointStats] = Json.format[EndpointStats]

  implicit protected val dataCreditStatsFormat: Format[DataCreditStats]       = Json.format[DataCreditStats]
  implicit protected val dataStorageStatsFormat: Format[DataStorageStats]     = Json.format[DataStorageStats]
  implicit protected val inboundDataStatsFormat: Format[InboundDataStats]     = Json.format[InboundDataStats]
  implicit protected val outboundDataStatsFormat: Format[OutboundDataStats]   = Json.format[OutboundDataStats]
  implicit protected val richDataDebitFormat                                  = RichDataJsonFormats.richDataDebitFormat
  implicit protected val dataDebitFormat                                      = RichDataJsonFormats.dataDebitFormat
  implicit protected val dataDebitEventFormat: Format[DataDebitEvent]         = Json.format[DataDebitEvent]
  implicit protected val dataDebitOperationFormat: Format[DataDebitOperation] = Json.format[DataDebitOperation]

  implicit val dataStatsFormat: Format[DataStats] = new Format[DataStats] {
    def reads(json: JsValue): JsResult[DataStats] =
      (json \ "statsType").as[String] match {
        case "datacredit" => Json.fromJson[DataCreditStats](json)(dataCreditStatsFormat)
        case "storage" => Json.fromJson[DataStorageStats](json)(dataStorageStatsFormat)
        case "inbound" => Json.fromJson[InboundDataStats](json)(inboundDataStatsFormat)
        case "outbound" => Json.fromJson[OutboundDataStats](json)(outboundDataStatsFormat)
        case "datadebitEvent" => Json.fromJson[DataDebitEvent](json)(dataDebitEventFormat)
        case "datadebit" => Json.fromJson[DataDebitOperation](json)(dataDebitOperationFormat)
        case statsType => JsError(s"Unexpected JSON value $statsType in $json")
      }

    def writes(stats: DataStats): JsValue = {
      val statsJson = stats match {
        case ds: DataCreditStats => Json.toJson(ds)(dataCreditStatsFormat)
        case ds: DataStorageStats => Json.toJson(ds)(dataStorageStatsFormat)
        case ds: InboundDataStats => Json.toJson(ds)(inboundDataStatsFormat)
        case ds: OutboundDataStats => Json.toJson(ds)(outboundDataStatsFormat)
        case ds: DataDebitEvent => Json.toJson(ds)(dataDebitEventFormat)
        case ds: DataDebitOperation => Json.toJson(ds)(dataDebitOperationFormat)
      }
      statsJson.as[JsObject].+(("statsType", Json.toJson(stats.statsType)))
    }
  }
}

object DataStatsFormat extends DataStatsFormat
