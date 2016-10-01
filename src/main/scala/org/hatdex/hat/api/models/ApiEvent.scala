/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
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