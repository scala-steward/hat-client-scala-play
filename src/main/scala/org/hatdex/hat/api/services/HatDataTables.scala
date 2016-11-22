/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Augustinas Markevicius <augustinas.markevicius@hatdex.org>, November 2016
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.api.models.ApiDataTable
import play.api.http.Status._
import play.api.Logger
import play.api.libs.json.Json
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
    futureResponse.map { response =>
      response.status match {
        case OK =>
          val jsResponse = response.json.validate[ApiDataTable] recover {
            case e =>
              logger.error(s"Error parsing successful Data Table value response: ${e}")
              throw new RuntimeException(s"Error parsing successful Data Table value response: ${e}")
          }

          jsResponse.get
        case NOT_FOUND =>
          logger.warn(s"Table $source $name not found")
          throw new RuntimeException(s"Table $source $name not found")
        case _ =>
          logger.error(s"Fetching $source $name Data Table for $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Fetching $source $name Data Table for $hatAddress failed")
      }
    }
  }

  def dataTableById(access_token: String, id: Int)(implicit ec: ExecutionContext): Future[ApiDataTable] = {
    logger.debug(s"Get Data Table id=$id from $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/table/$id")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()
    futureResponse.map { response =>
      response.status match {
        case OK =>
          val jsResponse = response.json.validate[ApiDataTable] recover {
            case e =>
              logger.error(s"Error parsing successful Data Table value response: ${e}")
              throw new RuntimeException(s"Error parsing successful Data Table value response: ${e}")
          }
          jsResponse.get
        case NOT_FOUND =>
          logger.warn(s"Table id=$id not found")
          throw new RuntimeException(s"Table id=$id not found")
        case _ =>
          logger.error(s"Fetching Data Table id=$id for $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Fetching Data Table id=$id for $hatAddress failed")
      }
    }
  }

  //    def randomFunction(): Unit = {
  //      val fields = Seq(ApiDataField(None, None, None, None, "fieldName", None))
  //      val subfields = Seq(ApiDataField(None, None, None, None, "uselessField", None))
  //      val subtables = Seq(ApiDataTable(None, None, None, "subtableName", "tableSource", Some(subfields), None))
  //      val table = ApiDataTable(None, None, None, "tableName", "tableSource", Some(fields), Some(subtables))
  //      createDataTable("asdasd", table)
  //    }

  def createDataTable(access_token: String, tableStructure: ApiDataTable)(implicit ec: ExecutionContext): Future[ApiDataTable] = {
    logger.debug(s"Post new Data Table to $hatAddress")

    val request: WSRequest = ws.url(s"$schema$hatAddress/data/table")
      .withVirtualHost(hatAddress)
      .withHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.post(Json.toJson(tableStructure))
    futureResponse.map { response =>
      response.status match {
        case OK =>
          val jsResponse = response.json.validate[ApiDataTable] recover {
            case e =>
              logger.error(s"Error parsing response from successful post of a new Data Table: $e")
              throw new RuntimeException(s"Error parsing response from successful post of a new Data Table: $e")
          }
          jsResponse.get
        case _ =>
          logger.error(s"Creating new table ${tableStructure.source} ${tableStructure.name} for $hatAddress failed, $response, ${response.body}")
          throw new RuntimeException(s"Creating new table ${tableStructure.source} ${tableStructure.name} for $hatAddress failed")
      }
    }
  }

}
