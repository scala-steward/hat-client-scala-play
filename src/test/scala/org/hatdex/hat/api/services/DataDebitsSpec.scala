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

import org.hatdex.hat.api.services.MockHatServer.withHatClient
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.Result
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import play.api.mvc.Results

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source._

class DataDebitsSpec(implicit ee: ExecutionEnv) extends Specification {

  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }

  sequential

  "Data Debit client" should {
    val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString

    "fetch and decode data" in {
      withHatClient { client =>
        client.dataDebitValues(validAccessToken, UUID.fromString("cedaaf28-3ae8-4676-aae7-100a1fb5079f")) map { res =>
          res.key must beSome
          res.key must beEqualTo(Some(UUID.fromString("cedaaf28-3ae8-4676-aae7-100a1fb5079f")))
          res.bundleContextless must beSome
          res.bundleContextless.get.dataGroups("facebook").head.data must not beEmpty
        } await (1, 10.seconds)
      }
    }

    "return error on failed login" in {
      withHatClient { client =>
        client.dataDebitValues("asdasd", UUID.fromString("cedaaf28-3ae8-4676-aae7-100a1fb5079f")) map { res =>
          res must beEqualTo("")
        } recover {
          case e =>
            e.getMessage must beEqualTo("Fetching Data Debit cedaaf28-3ae8-4676-aae7-100a1fb5079f values from  failed")
        } await (1, 10.seconds)
      }
    }
  }
}
