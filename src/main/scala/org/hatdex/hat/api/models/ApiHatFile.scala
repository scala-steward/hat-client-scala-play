/*
 * Copyright (C) 2017 HAT Data Exchange Ltd
 * SPDX-License-Identifier: AGPL-3.0
 *
 * This file is part of the Hub of All Things project (HAT).
 *
 * HAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation, version 3 of
 * the License.
 *
 * HAT is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General
 * Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>
 * 2 / 2017
 */

package org.hatdex.hat.api.models
import org.joda.time.DateTime

object HatFileStatus {
  sealed trait Status
  object Status {
    def valueOf(value: String): Status = {
      value match {
        case "Initialized" => HatFileStatus.Initialized
        case "New"         => HatFileStatus.New
        case "Completed"   => HatFileStatus.Completed
        case "Deleted"     => HatFileStatus.Deleted
        case _             => throw new IllegalArgumentException("Unrecognised HatFileStatus")
      }
    }
  }

  case object New extends Status
  case object Initialized extends Status
  case object Completed extends Status
  case object Deleted extends Status
}

case class ApiHatFile(
  fileId: Option[String],
  name: String,
  source: String,
  dateCreated: Option[DateTime],
  lastUpdated: Option[DateTime],
  tags: Option[Seq[String]],
  title: Option[String],
  description: Option[String],
  sourceURL: Option[String],
  status: Option[HatFileStatus.Status],
  contentUrl: Option[String] = None)
