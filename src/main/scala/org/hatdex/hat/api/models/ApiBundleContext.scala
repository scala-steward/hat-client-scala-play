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
