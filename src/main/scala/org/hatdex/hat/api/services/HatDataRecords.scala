/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Augustinas Markevicius <augustinas.markevicius@hatdex.org>, November 2016
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.models.ApiRecordValues
import play.api.http.Status._
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }

trait HatDataRecords {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import org.hatdex.hat.api.json.HatJsonFormats._

  def createBatchRecords(access_token: String, records: Seq[ApiRecordValues])(implicit ec: ExecutionContext): Future[Seq[ApiRecordValues]] = {
    logger.debug(s"Post ${records.length} new records to $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/record/values")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.post(Json.toJson(records))

    futureResponse.map { response =>
      response.status match {
        case OK =>
          val jsResponse = response.json.validate[Seq[ApiRecordValues]] recover {
            case e =>
              logger.error(s"Error parsing response from a successful data records post: $e")
              throw new RuntimeException(s"Error parsing response from a successful data records post: $e")
          }

          jsResponse.get
        case _ =>
          logger.error(s"Creating ${records.length} new records for $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Creating ${records.length} new records for $hatAddress failed")
      }
    }
  }
}
