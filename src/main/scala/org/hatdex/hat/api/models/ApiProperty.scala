/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

case class ApiProperty(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  description: Option[String],
  propertyType: ApiSystemType,
  unitOfMeasurement: ApiSystemUnitofmeasurement)

case class ApiRelationship(relationshipType: String)

case class ApiPropertyRelationshipDynamic(
  id: Option[Int],
  property: ApiProperty,
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  relationshipType: String,
  field: ApiDataField)

case class ApiPropertyRelationshipStatic(
  id: Option[Int],
  property: ApiProperty,
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  relationshipType: String,
  field: ApiDataField,
  record: ApiDataRecord)

case class ApiSystemType(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  description: Option[String])

case class ApiSystemUnitofmeasurement(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  description: Option[String],
  symbol: Option[String])
