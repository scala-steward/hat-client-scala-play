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
import org.hatdex.hat.api.models.ApiDataDebitOut
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{ JsError, JsSuccess }
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
              logger.error(s"Error parsing successful Data Debit value response: ${e}")
              Future.failed(new RuntimeException(s"Error parsing successful Data Debit value response: ${e}"))
          }
        // Convert to ApiDataDebitOut - if validation has failed, it will have thrown an error already
        case _ =>
          logger.error(s"Fetching Data Debit $dataDebitId values from $hatAddress failed, $response, ${response.body}")
          Future.failed(new RuntimeException(s"Fetching Data Debit $dataDebitId values from $hatAddress failed"))
      }
    }
  }

}
