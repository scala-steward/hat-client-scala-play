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
import java.util.UUID

import org.joda.time.DateTime

object HatFileStatus {
  sealed trait Status {
    val status: String
  }
  case class New(status: String = "New") extends Status
  case class Initialized(status: String = "Initialized") extends Status
  case class Completed(
      size: Long,
      status: String = "Completed")
      extends Status
  case class Deleted(status: String = "Deleted") extends Status
}

case class ApiHatFilePermissions(
    userId: UUID,
    contentReadable: Boolean)

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
    contentUrl: Option[String] = None,
    contentType: Option[String],
    contentPublic: Option[Boolean] = Some(false),
    permissions: Option[Seq[ApiHatFilePermissions]] = None)
