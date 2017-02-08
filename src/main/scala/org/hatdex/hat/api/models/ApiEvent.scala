/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

case class ApiEvent(
  id: Option[Int],
  name: String,
  staticProperties: Option[Seq[ApiPropertyRelationshipStatic]],
  dynamicProperties: Option[Seq[ApiPropertyRelationshipDynamic]],
  events: Option[Seq[ApiEventRelationship]],
  locations: Option[Seq[ApiLocationRelationship]],
  people: Option[Seq[ApiPersonRelationship]],
  things: Option[Seq[ApiThingRelationship]],
  organisations: Option[Seq[ApiOrganisationRelationship]])

case class ApiEventRelationship(relationshipType: String, event: ApiEvent)