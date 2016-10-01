/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
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