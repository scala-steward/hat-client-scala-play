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
import play.shaded.ahc.org.asynchttpclient.exception.RemotelyClosedException
import org.hatdex.hat.api.services.MockHatServer.withHatClient
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.Logger
import play.api.libs.json.{ JsArray, Json }
import play.api.mvc.Results

import scala.concurrent.{ Await }
import scala.concurrent.duration._
import scala.io.Source._

class RichDataSpec(implicit ee: ExecutionEnv) extends Specification with RichDataSpecContext {
  val logger = Logger(this.getClass)

  sequential

  "HAT Rich data client" should {
    "post new records in batch" in {
      withHatClient { client =>
        logger.debug(s"Saving records: ${jsonData}")
        val eventuallyRecord = client.saveData(validAccessToken, "rumpel", "locations", jsonData)
        val record           = Await.result(eventuallyRecord, 20.seconds)
        logger.debug(s"Received records: ${record}")
        record.length must beEqualTo(data.length)
      }
    }

    "get data for permitted endpoints" in {
      withHatClient { client =>
        val eventuallyRecord = client.getData(validAccessToken, "rumpel", "locations")
        val record           = Await.result(eventuallyRecord, 20.seconds)
        record.length must beEqualTo(data.length)
      }
    }

    "throw an exception for unauthorized endpoints" in {
      withHatClient { client =>
        client.getData(validAccessToken, "private", "locations") map { res =>
          res must beEqualTo("")
        } recover {
          case uae: UnauthorizedActionException => uae must beAnInstanceOf[UnauthorizedActionException]
          case rce: RemotelyClosedException =>
            logger.info(s"We expected an UnauthorizedActionException, but received a RemotelyClosedException")
            println(s"We expected an UnauthorizedActionException, but received a RemotelyClosedException")
            ok
          case e =>
            logger.info(s"We expected an UnauthorizedActionException, but received a ${e}")
            println(s"We expected an UnauthorizedActionException, but received a ${e}")
            ko
        }
      }
    }
  }

}

trait RichDataSpecContext extends Scope {
  import org.hatdex.hat.api.json.RichDataJsonFormats._
  val validAccessToken = fromInputStream(
    Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/validAccessToken")
  ).mkString
  val dataRecords =
    Json.parse(Results.getClass.getClassLoader.getResourceAsStream("hat-test-messages/flexiRecordsSaved.json"))

  val data     = dataRecords.as[Seq[EndpointData]]
  val jsonData = JsArray(data.map(_.data))
}
