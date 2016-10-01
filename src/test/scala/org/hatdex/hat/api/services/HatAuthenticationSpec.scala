/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.services

import org.hatdex.hat.api.services.MockHatServer.withHatClient
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.Result
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import play.api.mvc.Results

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source._

// Using global execution context in tests only
import scala.concurrent.ExecutionContext.Implicits.global

class HatAuthenticationSpec(implicit ee: ExecutionEnv) extends Specification {

  //  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }
  def awaiting[T]: Future[MatchResult[T]] => Result = { _.await }

  sequential

  "HAT Authentication client" should {
    "retrieve public key" in {
      withHatClient { client =>
        client.retrievePublicKey() map { result =>
          result must startWith("-----BEGIN PUBLIC KEY-----")
        } await (1, 10.seconds)
      }
    }

    "return access token on sucessful login" in {
      withHatClient { client =>
        client.authenticateForToken("user", "pa55") map { result =>
          val validAccessToken = fromInputStream(Results.getClass.getClassLoader.getResourceAsStream("validAccessToken")).mkString
          result must beEqualTo(validAccessToken)
        } await (1, 10.seconds)
      }
    }

    "return error on failed login" in {
      withHatClient { client =>
        client.authenticateForToken("use", "pa55") map { res =>
          res must beEqualTo("")
        } recover {
          case e =>
            e.getMessage must beEqualTo("Unauthorized")
        } await (1, 10.seconds)
      }
    }
  }
}
