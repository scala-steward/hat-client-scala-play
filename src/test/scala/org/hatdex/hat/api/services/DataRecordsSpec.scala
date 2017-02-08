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
import org.hatdex.hat.api.models.ApiRecordValues
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

class DataRecordsSpec(implicit ee: ExecutionEnv) extends Specification {

  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }

  sequential

  import HatJsonFormats._

  "Data Record client" should {
    val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
    val dataRecords = Json.parse(Results.getClass.getClassLoader.getResourceAsStream("recordsSubmission.json"))

    val data = dataRecords.as[Seq[ApiRecordValues]]

    "post new records in batch" in {
      withHatClient { client =>
        client.createBatchRecords(validAccessToken, data) map { record =>
          record.length must beEqualTo(2)
          record(0).record.id must beEqualTo(Some(40))
          record(1).record.id must beEqualTo(Some(41))
        } await (1, 10.seconds)
      }
    }
  }
}
