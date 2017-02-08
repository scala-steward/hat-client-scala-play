/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.api.models.ApiDataTable
import play.api.http.Status._
import play.api.Logger
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future }

trait HatDataTables {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import HatJsonFormats._

  def dataTableByName(access_token: String, name: String, source: String)(implicit ec: ExecutionContext): Future[ApiDataTable] = {
    logger.debug(s"Get $source $name Data Table from $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/table")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)
      .withQueryString("name" -> name, "source" -> source)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[ApiDataTable] match {
            case s: JsSuccess[ApiDataTable] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing successful Data Table value response: $e")
              Future.failed(new RuntimeException(s"Error parsing successful Data Table value response: $e"))
          }
        case NOT_FOUND =>
          logger.warn(s"Table $source $name not found")
          Future.failed(new RuntimeException(s"Table $source $name not found"))
        case _ =>
          logger.error(s"Fetching $source $name Data Table for $hatAddress failed, $response, ${response.body}")
          Future.failed(new RuntimeException(s"Fetching $source $name Data Table for $hatAddress failed"))
      }
    }
  }

  def dataTableById(access_token: String, id: Int)(implicit ec: ExecutionContext): Future[ApiDataTable] = {
    logger.debug(s"Get Data Table id=$id from $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/table/$id")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[ApiDataTable] match {
            case s: JsSuccess[ApiDataTable] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing successful Data Table value response: $e")
              Future.failed(new RuntimeException(s"Error parsing successful Data Table value response: $e"))
          }
        case NOT_FOUND =>
          logger.warn(s"Table id=$id not found")
          Future.failed(new RuntimeException(s"Table id=$id not found"))
        case _ =>
          logger.error(s"Fetching Data Table id=$id for $hatAddress failed, $response, ${response.body}")
          Future.failed(new RuntimeException(s"Fetching Data Table id=$id for $hatAddress failed"))
      }
    }
  }

  def createDataTable(access_token: String, tableStructure: ApiDataTable)(implicit ec: ExecutionContext): Future[ApiDataTable] = {
    logger.debug(s"Post new Data Table to $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/table")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.post(Json.toJson(tableStructure))

    futureResponse.flatMap { response =>
      response.status match {
        case CREATED =>
          response.json.validate[ApiDataTable] match {
            case s: JsSuccess[ApiDataTable] => Future.successful(s.get)
            case e: JsError =>
              logger.error(s"Error parsing response from successful post of a new Data Table: $e")
              Future.failed(new RuntimeException(s"Error parsing response from successful post of a new Data Table: $e"))
          }
        case _ =>
          logger.error(s"Creating new table ${tableStructure.source} ${tableStructure.name} for $hatAddress failed, $response, ${response.body}")
          Future.failed(new RuntimeException(s"Creating new table ${tableStructure.source} ${tableStructure.name} for $hatAddress failed"))
      }
    }
  }

}
