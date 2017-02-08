/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

case class ApiOrganisation(
  id: Option[Int],
  name: String,
  staticProperties: Option[Seq[ApiPropertyRelationshipStatic]],
  dynamicProperties: Option[Seq[ApiPropertyRelationshipDynamic]],
  organisations: Option[Seq[ApiOrganisationRelationship]],
  locations: Option[Seq[ApiLocationRelationship]],
  things: Option[Seq[ApiThingRelationship]])

case class ApiOrganisationRelationship(relationshipType: String, organisation: ApiOrganisation)