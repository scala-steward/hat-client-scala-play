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

import akka.util.ByteString
import org.hatdex.hat.api.models.{ ApiDataTable, ApiRecordValues, EndpointData, ErrorMessage }
import play.api.Logger
import play.api.http.{ DefaultFileMimeTypesProvider, FileMimeTypesConfiguration, HttpEntity }
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.routing.sird._
import play.core.server.Server

import scala.io.Source._

object MockHatServer {

  import org.hatdex.hat.api.models.RichDataJsonFormats._

  private val logger = Logger(this.getClass)

  implicit val fileMimeTypes = new DefaultFileMimeTypesProvider(FileMimeTypesConfiguration(Map(
    "json" -> "application/json",
    "pem" -> "text/plain"))).get

  def withMockHatServerClient[T](block: WSClient => T): T = {
    Server.withRouterFromComponents() { components =>
      import components.{ defaultActionBuilder => Action }
      {
        case GET(p"/publickey") => Action {
          Results.Ok.sendResource("hat-test-messages/testPublicKey.pem")
        }
        case GET(p"/users/access_token") => Action { request =>
          //        Logger.info("Responding to access token request")
          val requestHeaders = request.headers.toSimpleMap
          val maybeUsername = requestHeaders.get("username")
          val maybePassword = requestHeaders.get("password")

          (maybeUsername, maybePassword) match {
            case (Some("user"), Some("pa55")) => Results.Ok.sendResource("hat-test-messages/jwtValidToken.json")
            case _                            => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
        case GET(p"/dataDebit/cedaaf28-3ae8-4676-aae7-100a1fb5079f/values") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          maybeAccessToken match {
            case Some(token) if token == validAccessToken => Results.Ok.sendResource("hat-test-messages/dataDebitOut.json")
            case _                                        => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
        case GET(p"/dataDebit/cedaaf28-3ae8-4676-aae7-100a1fb5079a/values") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          val ddUnauthorizedMessage =
            """
            |{
            |    "message": "Forbidden",
            |    "cause": "You do not have rights to access values for this data debit"
            |}
          """.stripMargin

          maybeAccessToken match {
            case Some(token) if token == validAccessToken => Results.Forbidden.sendEntity(HttpEntity.Strict(ByteString(ddUnauthorizedMessage), Some("application/json")))
            case _                                        => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
        case GET(p"/data/table") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
          val maybeTableName = request.getQueryString("name")
          val maybeTableSource = request.getQueryString("source")

          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          (maybeTableName, maybeTableSource, maybeAccessToken) match {
            case (Some("events"), Some("calendar"), Some(token)) if token == validAccessToken => Results.Ok.sendResource("hat-test-messages/tableFound.json")
            case (Some(_), Some(_), Some(token)) if token == validAccessToken => Results.NotFound.sendResource("hat-test-messages/tableNotFound.json")
            case _ => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
        case GET(p"/data/table/${ int(id) }") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")

          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          (id, maybeAccessToken) match {
            case (58, Some(token)) if token == validAccessToken => Results.Ok.sendResource("hat-test-messages/tableFound.json")
            case (_, Some(token)) if token == validAccessToken => Results.NotFound.sendResource("hat-test-messages/tableIdNotFound.json")
            case _ => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
        case POST(p"/data/table") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
          request.body.asJson.map { bodyJson =>
            bodyJson.validate[ApiDataTable] match {
              case s: JsSuccess[ApiDataTable] =>
                val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString
                val expectedTable = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/expectedTableValue.json")).as[ApiDataTable]

                val response = (s.get, maybeAccessToken) match {
                  case (body, Some(token)) if token == validAccessToken && body == expectedTable =>
                    Results.Created.sendResource("hat-test-messages/tableFound.json")
                  case (body, Some(token)) if token == validAccessToken =>
                    Results.Ok.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
                  case _ =>
                    Results.Unauthorized.sendResource("authInvalid.json")
                }
                response
              case e: JsError => Results.Ok.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
              case _          => Results.Ok.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
            }
          } getOrElse {
            Results.BadRequest.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("Not JSON!").toString()), Some("application/json")))
          }
        }
        case POST(p"/data/record/values") => Action { request =>
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
          request.body.asJson.map { bodyJson =>
            bodyJson.validate[Seq[ApiRecordValues]] match {
              case s: JsSuccess[Seq[ApiRecordValues]] =>
                val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString
                val expectedRecord = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/recordsSubmission.json")).as[Seq[ApiRecordValues]]

                val response = (s.get, maybeAccessToken) match {
                  case (body, Some(token)) if token == validAccessToken && body == expectedRecord =>
                    Results.Created.sendResource("hat-test-messages/recordsPosted.json")
                  case (body, Some(token)) if token == validAccessToken =>
                    Results.BadRequest.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
                  case _ =>
                    Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
                }
                response
              case e: JsError => Results.Ok.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
              case _          => Results.Ok.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("").toString()), Some("application/json")))
            }
          } getOrElse {
            Results.BadRequest.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("Not JSON!").toString()), Some("application/json")))
          }
        }
        case POST(p"/api/v2/data/$namespace/$endpoint") => Action { request =>
          logger.info(s"POST /api/v2/data/$namespace/$endpoint")
          request.body.asJson.map {
            case array: JsArray =>
              val result = array.value.map(EndpointData(s"$namespace/$endpoint", Some(UUID.randomUUID), _, None))
              Results.Created.sendEntity(HttpEntity.Strict(ByteString(Json.toJson(result).toString()), Some("application/json")))
            case value: JsValue =>
              val result = EndpointData(s"$namespace/$endpoint", Some(UUID.randomUUID), value, None)
              Results.Created.sendEntity(HttpEntity.Strict(ByteString(Json.toJson(result).toString()), Some("application/json")))
          } getOrElse {
            Results.BadRequest.sendEntity(HttpEntity.Strict(ByteString(Json.toJson("Not JSON!").toString()), Some("application/json")))
          }
        }
        case GET(p"/api/v2/data/$namespace/$endpoint") => Action { request =>
          logger.info(s"GET /api/v2/data/$namespace/$endpoint")
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")

          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          (namespace, endpoint, maybeAccessToken) match {
            case ("rumpel", "locations", Some(token)) if token == validAccessToken =>
              Results.Ok.sendResource("hat-test-messages/flexiRecordsSaved.json")
            case ("rumpel", _, Some(token)) if token == validAccessToken =>
              Results.Ok.sendEntity(HttpEntity.Strict(ByteString("[]"), Some("application/json")))
            case (_, _, Some(token)) if token == validAccessToken =>
              Results.Forbidden.sendEntity(
                HttpEntity.Strict(ByteString(Json.toJson(ErrorMessage("Forbidden", "Access Denied")).toString), Some("application/json")))
            case _ => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }

        case GET(p"/api/v2/data-debit/$dataDebitId/values") => Action { request =>
          logger.info(s"GET /api/v2/data-debit/$dataDebitId/values")
          val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")

          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")).mkString

          (dataDebitId, maybeAccessToken) match {
            case ("nodata", Some(token)) if token == validAccessToken =>
              Results.Ok.sendResource("hat-test-messages/dataDebitValuesEmpty.json")
            case ("locations", Some(token)) if token == validAccessToken =>
              Results.Ok.sendResource("hat-test-messages/dataDebitValuesLocations.json")
            case (_, Some(token)) if token == validAccessToken =>
              Results.Forbidden.sendEntity(
                HttpEntity.Strict(ByteString(Json.toJson(ErrorMessage("Forbidden", "Access Denied")).toString), Some("application/json")))
            case _ => Results.Unauthorized.sendResource("hat-test-messages/authInvalid.json")
          }
        }
      }
    } { implicit port =>
      play.api.test.WsTestClient.withClient { client =>
        block(client)
      }
    }
  }

  def withHatClient[T](block: HatClient => T): T = {
    withMockHatServerClient { client =>
      block(new HatClient(client, "", ""))
    }
  }
}
