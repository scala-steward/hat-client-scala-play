/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
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
