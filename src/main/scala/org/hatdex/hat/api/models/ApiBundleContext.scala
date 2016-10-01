/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.hat.api.models

import org.joda.time.LocalDateTime

case class ApiBundleContextEntitySelection(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  entityName: Option[String],
  entityId: Option[Int],
  entityKind: Option[String],
  properties: Option[Seq[ApiBundleContextPropertySelection]])

case class ApiBundleContextPropertySelection(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  propertyRelationshipKind: Option[String],
  propertyRelationshipId: Option[Int],
  propertyName: Option[String],
  propertyType: Option[String],
  propertyUnitofmeasurement: Option[String])

case class ApiBundleContext(
  id: Option[Int],
  dateCreated: Option[LocalDateTime],
  lastUpdated: Option[LocalDateTime],
  name: String,
  entities: Option[Seq[ApiBundleContextEntitySelection]],
  bundles: Option[Seq[ApiBundleContext]])
