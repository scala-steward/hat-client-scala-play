/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.models.HatService
import org.hatdex.hat.api.services.Errors.{ ApiException, UnauthorizedActionException }
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }

trait HatApplications {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import org.hatdex.hat.api.json.HatJsonFormats._

  def getApplications(access_token: String)(implicit ec: ExecutionContext): Future[Seq[HatService]] = {

    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/application")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[Seq[HatService]] match {
            case s: JsSuccess[Seq[HatService]] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing Application listing response: $e")
              Future.failed(new ApiException(s"Error parsing Application listing response: $e"))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Getting applications for hat $hatAddress forbidden"))
        case _ =>
          logger.error(s"Listing applications for $hatAddress failed, $response, ${response.body}")
          Future.failed(new ApiException(s"Listing applications for $hatAddress failed unexpectedly"))
      }
    }
  }

  def saveApplication(access_token: String, application: HatService)(implicit ec: ExecutionContext): Future[HatService] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/application")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.post(Json.toJson(application))

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[HatService] match {
            case s: JsSuccess[HatService] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing response from a successful application update: $e")
              Future.failed(new ApiException(s"Error parsing response from a successful application update: $e"))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Saving application for hat $hatAddress forbidden"))
        case _ =>
          logger.error(s"Saving application for $hatAddress failed, $response, ${response.body}")
          Future.failed(new ApiException(s"Saving application for $hatAddress failed unexpectedly"))
      }
    }
  }

}
