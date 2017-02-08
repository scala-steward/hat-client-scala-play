/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.services

import akka.util.ByteString
import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.api.models.{ ApiRecordValues, ApiDataTable }
import play.api.http.HttpEntity
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.routing.sird._
import play.api.test._
import play.core.server.Server

import scala.io.Source._

object MockHatServer {

  import HatJsonFormats._

  def withMockHatServerClient[T](block: WSClient => T): T = {
    Server.withRouter() {
      case GET(p"/publickey") => Action {
        Results.Ok.sendResource("testPublicKey.pem").as("text/plain")
      }
      case GET(p"/users/access_token") => Action { request =>
        //        Logger.info("Responding to access token request")
        val requestHeaders = request.headers.toSimpleMap
        val maybeUsername = requestHeaders.get("username")
        val maybePassword = requestHeaders.get("password")

        (maybeUsername, maybePassword) match {
          case (Some("user"), Some("pa55")) => Results.Ok.sendResource("jwtValidToken.json").as("application/json")
          case _                            => Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
        }
      }
      case GET(p"/dataDebit/cedaaf28-3ae8-4676-aae7-100a1fb5079f/values") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
        val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString

        maybeAccessToken match {
          case Some(token) if token == validAccessToken => Results.Ok.sendResource("dataDebitOut.json").as("application/json")
          case _                                        => Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
        }
      }
      case GET(p"/dataDebit/cedaaf28-3ae8-4676-aae7-100a1fb5079a/values") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
        val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString

        val ddUnauthorizedMessage =
          """
            |{
            |    "message": "Forbidden",
            |    "cause": "You do not have rights to access values for this data debit"
            |}
          """.stripMargin

        maybeAccessToken match {
          case Some(token) if token == validAccessToken => Results.Forbidden.sendEntity(HttpEntity.Strict(ByteString(ddUnauthorizedMessage), Some("application/json")))
          case _                                        => Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
        }
      }
      case GET(p"/data/table") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
        val maybeTableName = request.getQueryString("name")
        val maybeTableSource = request.getQueryString("source")

        val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString

        (maybeTableName, maybeTableSource, maybeAccessToken) match {
          case (Some("events"), Some("calendar"), Some(token)) if token == validAccessToken => Results.Ok.sendResource("tableFound.json").as("application/json")
          case (Some(_), Some(_), Some(token)) if token == validAccessToken => Results.NotFound.sendResource("tableNotFound.json").as("application/json")
          case _ => Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
        }
      }
      case GET(p"/data/table/${ int(id) }") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")

        val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString

        (id, maybeAccessToken) match {
          case (58, Some(token)) if token == validAccessToken => Results.Ok.sendResource("tableFound.json").as("application/json")
          case (_, Some(token)) if token == validAccessToken => Results.NotFound.sendResource("tableIdNotFound.json").as("application/json")
          case _ => Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
        }
      }
      case POST(p"/data/table") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
        request.body.asJson.map { bodyJson =>
          bodyJson.validate[ApiDataTable] match {
            case s: JsSuccess[ApiDataTable] =>
              val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
              val expectedTable = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("expectedTableValue.json")).as[ApiDataTable]

              val response = (s.get, maybeAccessToken) match {
                case (body, Some(token)) if token == validAccessToken && body == expectedTable =>
                  Results.Created.sendResource("tableFound.json").as("application/json")
                case (body, Some(token)) if token == validAccessToken =>
                  Results.BadRequest.sendResource("").as("application/json")
                case _ =>
                  Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
              }
              response
            case e: JsError => Results.Ok.sendResource("")
            case _          => Results.Ok.sendResource("")
          }
        } getOrElse {
          Results.BadRequest.sendResource("Not JSON!")
        }
      }
      case POST(p"/data/record/values") => Action { request =>
        val maybeAccessToken = request.headers.toSimpleMap.get("X-Auth-Token")
        request.body.asJson.map { bodyJson =>
          bodyJson.validate[Seq[ApiRecordValues]] match {
            case s: JsSuccess[Seq[ApiRecordValues]] =>
              val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
              val expectedRecord = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("recordsSubmission.json")).as[Seq[ApiRecordValues]]

              val response = (s.get, maybeAccessToken) match {
                case (body, Some(token)) if token == validAccessToken && body == expectedRecord =>
                  Results.Created.sendResource("recordsPosted.json").as("application/json")
                case (body, Some(token)) if token == validAccessToken =>
                  Results.BadRequest.sendResource("").as("application/json")
                case _ =>
                  Results.Unauthorized.sendResource("authInvalid.json").as("application/json")
              }
              response
            case e: JsError => Results.Ok.sendResource("")
            case _          => Results.Ok.sendResource("")
          }
        } getOrElse {
          Results.BadRequest.sendResource("Not JSON!")
        }
      }
    } { implicit port =>
      WsTestClient.withClient { client =>
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
