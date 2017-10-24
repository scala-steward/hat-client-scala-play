/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.models.EndpointData
import org.hatdex.hat.api.services.Errors.UnauthorizedActionException
import org.hatdex.hat.api.services.MockHatServer.withHatClient
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.Result
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import play.api.Logger
import play.api.libs.json.{ JsArray, Json }
import play.api.mvc.Results

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source._

class RichDataSpec(implicit ee: ExecutionEnv) extends Specification {
  val logger = Logger(this.getClass)
  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }

  sequential

  import org.hatdex.hat.api.models.RichDataJsonFormats._

  "HAT Rich data client" should {
    val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
    val dataRecords = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("flexiRecordsSaved.json"))

    val data = dataRecords.as[Seq[EndpointData]]
    val jsonData = JsArray(data.map(_.data))

    "post new records in batch" in {
      withHatClient { client =>
        client.saveData(validAccessToken, "rumpel", "locations", jsonData) map { record =>
          logger.debug(s"REceived records: ${record}")
          record.length must beEqualTo(data.length)
        } await (1, 10.seconds)
      }
    }

    "get data for permitted endpoints" in {
      withHatClient { client =>
        client.getData(validAccessToken, "rumpel", "locations") map { record =>
          record.length must beEqualTo(data.length)
        } await (1, 10.seconds)
      }
    }

    "throw an exception for unauthorized endpoints" in {
      withHatClient { client =>
        client.getData(validAccessToken, "private", "locations") map { res =>
          res must beEqualTo("")
        } recover {
          case e => e must beAnInstanceOf[UnauthorizedActionException]
        } await (1, 10.seconds)
      }
    }
  }

}
