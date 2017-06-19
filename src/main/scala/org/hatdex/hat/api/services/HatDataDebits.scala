/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.services

import java.util.UUID

import org.hatdex.hat.api.json.DataDebitFormats
import org.hatdex.hat.api.models.{ ApiBundleContextless, ApiBundleDataSourceStructure, ApiDataDebit, ApiDataDebitOut }
import org.joda.time.DateTime
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }

trait HatDataDebits {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import DataDebitFormats._

  def dataDebitValues(access_token: String, dataDebitId: UUID)(implicit ec: ExecutionContext): Future[ApiDataDebitOut] = {
    logger.debug(s"Get Data Debit $dataDebitId values from $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/dataDebit/$dataDebitId/values")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()
    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[ApiDataDebitOut] match {
            case s: JsSuccess[ApiDataDebitOut] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing successful Data Debit value response: $e")
              Future.failed(new RuntimeException(s"Error parsing successful Data Debit value response: $e"))
          }
        // Convert to ApiDataDebitOut - if validation has failed, it will have thrown an error already
        case _ =>
          logger.error(s"Fetching Data Debit $dataDebitId values from $hatAddress failed, $response, ${response.body}")
          Future.failed(new RuntimeException(s"Fetching Data Debit $dataDebitId values from $hatAddress failed"))
      }
    }
  }

  def proposeDataDebit(access_token: String, title: String,
    starts: DateTime, expires: DateTime,
    definitionStructure: List[ApiBundleDataSourceStructure])(implicit ec: ExecutionContext): Future[ApiDataDebit] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/dataDebit/propose")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val bundle = ApiBundleContextless(None, None, None, s"Bundle for Offer $title", Some(definitionStructure))

    val dataDebit = ApiDataDebit(None, None, None,
      title, starts.toLocalDateTime, expires.toLocalDateTime,
      enabled = None, rolling = false, sell = true,
      0, "contextless", Some(bundle), None)

    logger.debug(s"Submitting Data Debit request for $hatAddress: ${Json.toJson(dataDebit).toString()}")
    val futureResponse: Future[WSResponse] = request.post(Json.toJson(dataDebit))

    futureResponse.flatMap { response =>
      response.status match {
        case CREATED =>
          val dd = Json.parse(response.body).as[ApiDataDebit]
          logger.info(s"Data debit created: $dd")
          Future.successful(dd)
        case BAD_REQUEST =>
          logger.error(s"Bad request when creating data debit: ${response.body}")
          Future.failed(new RuntimeException(s"Bad request when creating data debit: ${response.body}"))
        case _ =>
          logger.error(s"Unexpected error when creating data debit: ${response.body}")
          Future.failed(new RuntimeException(s"Unexpected error when creating data debit: ${response.body}"))
      }
    }
  }

}
