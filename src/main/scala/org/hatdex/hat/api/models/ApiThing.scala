/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

/**
 * API format of the Thing, with only the name as the mandatory field
 */
case class ApiThing(
  id: Option[Int],
  name: String,
  staticProperties: Option[Seq[ApiPropertyRelationshipStatic]],
  dynamicProperties: Option[Seq[ApiPropertyRelationshipDynamic]],
  things: Option[Seq[ApiThingRelationship]],
  people: Option[Seq[ApiPersonRelationship]])

case class ApiThingRelationship(relationshipType: String, thing: ApiThing)