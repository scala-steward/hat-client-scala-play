/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Augustinas Markevicius <augustinas.markevicius@hatdex.org>, November 2016
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.json.HatJsonFormats
import org.hatdex.hat.api.models.ApiDataTable
import org.hatdex.hat.api.services.MockHatServer.withHatClient
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.Result
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import play.api.mvc.Results

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source._
import scala.concurrent.ExecutionContext.Implicits.global

class DataTablesSpec(implicit ee: ExecutionEnv) extends Specification {

  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }

  sequential

  import HatJsonFormats._

  "Data Table client" should {
    val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
    val tableStructure = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("tableStructure.json")).as[ApiDataTable]

    "fetch and decode table data by name and source" in {
      withHatClient { client =>
        client.dataTableByName(validAccessToken, "events", "calendar") map { table =>
          table.name must beEqualTo("events")
          table.source must beEqualTo("calendar")
          table.id must beSome(58)
        } await (1, 10.seconds)
      }
    }

    "fetch and decode table data by table ID" in {
      withHatClient { client =>
        client.dataTableById(validAccessToken, 58) map { table =>
          table.name must beEqualTo("events")
          table.source must beEqualTo("calendar")
          // table.id must beEqualTo(58)
        } await (1, 10.seconds)
      }
    }

    "return an error for non-existent table" in {
      withHatClient { client =>
        client.dataTableByName(validAccessToken, "randomtable", "calendar").map { _ =>
          throw new RuntimeException("Must not get here")
        }.recover {
          case e =>
            e.getMessage must beEqualTo(s"Table calendar randomtable not found")
        } await (1, 10.seconds)
      }
    }

    "return an error for non-existent table ID" in {
      withHatClient { client =>
        client.dataTableById(validAccessToken, 1) map { _ =>
          throw new RuntimeException("Must not get here")
        } recover {
          case e =>
            e.getMessage must beEqualTo(s"Table id=1 not found")
        } await (1, 10.seconds)
      }
    }

    "post new table to HAT" in {
      withHatClient { client =>
        client.createDataTable(validAccessToken, tableStructure) map { table =>
          table.name must beEqualTo("events")
          table.source must beEqualTo("calendar")
        } await (1, 10.seconds)
      }
    }

    "return error on failed login" in {
      withHatClient { client =>
        client.dataTableByName("invalid_token", "events", "calendar") map { res =>
          res must beEqualTo("")
        } recover {
          case e =>
            e.getMessage must beEqualTo("Fetching calendar events Data Table for  failed")
        } await (1, 10.seconds)
      }
    }
  }
}
