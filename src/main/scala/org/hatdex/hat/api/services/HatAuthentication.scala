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

import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.api.models.User
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }

trait HatAuthentication {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import HatJsonFormats._

  def retrievePublicKey()(implicit ec: ExecutionContext): Future[String] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/publickey")
      .withHeaders("Accept" -> "text/plain")
      .withVirtualHost(hatAddress)

    request.get().map { response =>
      logger.debug(s"Hat $hatAddress public key response: $response")
      response.status match {
        case OK => response.body
        case _  => throw new RuntimeException("Public Key could not be retrieved")
      }
    }
  }

  def authenticateForToken(username: String, password: String)(implicit ec: ExecutionContext): Future[String] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/users/access_token")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json")
      .withHeaders("username" -> username)
      .withHeaders("password" -> password)

    logger.debug(s"Authenticate for token with HAT at ${request.url}")

    val futureResponse: Future[WSResponse] = request.get()
    futureResponse.map { response =>
      logger.debug(s"Authenticate for token with HAT at ${request.url} responded ${response.status}: ${response.body}")
      response.status match {
        case OK =>
          logger.debug(s"Response: ${response.status} ${response.body}")
          (response.json \ "accessToken")
            .validate[String]
            .getOrElse {
              throw new RuntimeException("Unauthorized")
            }
        case _ =>
          logger.error(s"Could not get auth response: ${response.status} ${response.body}")
          throw new RuntimeException("Unauthorized")
      }
    } recover {
      case e =>
        logger.error(s"Could not get auth response: ${e.getMessage}", e)
        throw new RuntimeException("Unauthorized")
    }
  }

  def createAccount(access_token: String, hatUser: User)(implicit ec: ExecutionContext): Future[UUID] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/users/user")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    logger.debug(s"Create account request ${request.uri}")
    val futureResponse: Future[WSResponse] = request.post(Json.toJson(hatUser))
    futureResponse.map { response =>
      response.status match {
        case CREATED =>
          logger.info(s"Account for ${hatUser.name} on HAT $hatAddress created")
          hatUser.userId
        case _ =>
          logger.error(s"Account creation for ${hatUser.name} on HAT $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Account creation for ${hatUser.name} failed")
      }
    }
  }

  def enableAccount(access_token: String, userId: UUID)(implicit ec: ExecutionContext): Future[Boolean] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/users/user/$userId/enable")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    logger.debug(s"Enable account $userId on $hatAddress")
    val futureResponse: Future[WSResponse] = request.put("")
    futureResponse.map { response =>
      response.status match {
        case OK =>
          logger.debug(s"Account for $userId on HAT $hatAddress enabled")
          true
        case _ =>
          logger.error(s"Account enabling for $userId on HAT $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Account enabling for $userId failed")
      }
    }
  }

}
