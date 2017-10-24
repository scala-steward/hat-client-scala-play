package org.hatdex.hat.api.services

import org.hatdex.hat.api.models.{ DataDebitRequest, RichDataDebit, RichDataDebitData }
import org.hatdex.hat.api.services.Errors.{ ApiException, UnauthorizedActionException }
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws.{ WSClient, WSRequest, WSResponse }

import scala.concurrent.{ ExecutionContext, Future }

trait HatDataDebitsV2 {
  val logger: Logger
  val ws: WSClient
  val schema: String
  val hatAddress: String

  import org.hatdex.hat.api.models.RichDataJsonFormats._

  def getDataDebit(access_token: String, dataDebitId: String)(implicit ec: ExecutionContext): Future[RichDataDebit] = {

    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/data-debit/$dataDebitId")
      .withVirtualHost(hatAddress)
      .withHttpHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[RichDataDebit] match {
            case s: JsSuccess[RichDataDebit] => Future.successful(s.get)
            case e: JsError =>
              val message = s"Error parsing response from a successful data debit request: $e"
              logger.error(message)
              Future.failed(new ApiException(message))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Retrieving data debit $dataDebitId from $hatAddress unauthorized"))
        case _ =>
          logger.error(s"Retrieving data debit $dataDebitId from $hatAddress failed ${response.body}")
          Future.failed(new ApiException(s"Retrieving data debit $dataDebitId from $hatAddress failed ${response.body}"))
      }
    }
  }

  def listDataDebits(access_token: String)(implicit ec: ExecutionContext): Future[Seq[RichDataDebit]] = {

    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/data-debit")
      .withVirtualHost(hatAddress)
      .withHttpHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[Seq[RichDataDebit]] match {
            case s: JsSuccess[Seq[RichDataDebit]] => Future.successful(s.get)
            case e: JsError =>
              val message = s"Error parsing response from a successful data debit request: $e"
              logger.error(message)
              Future.failed(new ApiException(message))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Retrieving data debits from $hatAddress unauthorized"))
        case _ =>
          logger.error(s"Retrieving data debits from $hatAddress failed ${response.body}")
          Future.failed(new ApiException(s"Retrieving data debits from $hatAddress failed ${response.body}"))
      }
    }
  }

  def getDataDebitValues(access_token: String, dataDebitId: String)(implicit ec: ExecutionContext): Future[RichDataDebitData] = {

    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/data-debit/$dataDebitId/values")
      .withVirtualHost(hatAddress)
      .withHttpHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.get()

    futureResponse.flatMap { response =>
      response.status match {
        case OK =>
          response.json.validate[RichDataDebitData] match {
            case s: JsSuccess[RichDataDebitData] => Future.successful(s.get)
            case e: JsError =>
              val message = s"Error parsing response from a successful data debit values request: $e"
              logger.error(message)
              Future.failed(new ApiException(message))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Retrieving data debit $dataDebitId values from $hatAddress unauthorized"))
        case _ =>
          logger.error(s"Retrieving data debit $dataDebitId values from $hatAddress failed ${response.body}")
          Future.failed(new ApiException(s"Retrieving data debit $dataDebitId values from $hatAddress failed ${response.body}"))
      }
    }
  }

  def registerDataDebit(access_token: String, dataDebitId: String, dataDebit: DataDebitRequest)(implicit ec: ExecutionContext): Future[RichDataDebit] = {
    val request: WSRequest = ws.url(s"$schema$hatAddress/api/v2/data-debit/$dataDebitId")
      .withVirtualHost(hatAddress)
      .withHttpHeaders("Accept" -> "application/json", "X-Auth-Token" -> access_token)

    val futureResponse: Future[WSResponse] = request.post(Json.toJson(dataDebit))

    futureResponse.flatMap { response =>
      response.status match {
        case CREATED =>
          response.json.validate[RichDataDebit] match {
            case s: JsSuccess[RichDataDebit] => Future.successful(s.get)
            case e: JsError =>
              val message = s"Error parsing response from a successful data debit registration request: $e"
              logger.error(message)
              Future.failed(new ApiException(message))
          }
        case FORBIDDEN =>
          Future.failed(UnauthorizedActionException(s"Registering data debit $dataDebitId with $hatAddress unauthorized"))
        case _ =>
          logger.error(s"Registering data debit $dataDebitId with $hatAddress failed ${response.body}")
          Future.failed(new ApiException(s"Registering data debit $dataDebitId with $hatAddress failed ${response.body}"))
      }
    }
  }
}
