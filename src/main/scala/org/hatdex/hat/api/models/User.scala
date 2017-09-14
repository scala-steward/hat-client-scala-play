/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

package org.hatdex.hat.api.models

import java.util.UUID

case class User(
    userId: UUID,
    email: String,
    pass: Option[String],
    name: String,
    role: String,
    roles: Seq[UserRole]) {
  /*
   * Return a copy of the user's object without sensitive details
   */
  lazy val clean: User = this.copy(pass = None)
}

sealed abstract class UserRole(roleTitle: String) {
  def title: String = roleTitle.toLowerCase

  def name: String = this.toString.replaceAll("\\(.*\\)", "")

  def extra: Option[String] = None
}

object UserRole {
  //noinspection ScalaStyle
  def userRoleDeserialize(userRole: String, roleExtra: Option[String]): UserRole = {
    (userRole, roleExtra) match {
      case (role, None) =>
        role match {
          case "owner"      => Owner()
          case "platform"   => Platform()
          case "validate"   => Validate()
          case "datadebit"  => DataDebitOwner("")
          case "datacredit" => DataCredit("")
          case _            => UnknownRole()
        }
      case (role, Some(extra)) =>
        role match {
          case "datadebit"      => DataDebitOwner(extra)
          case "datacredit"     => DataCredit(extra)
          case "namespacewrite" => NamespaceWrite(extra)
          case "namespaceread"  => NamespaceRead(extra)
          case _                => UnknownRole()
        }
    }
  }
}

// Owner
case class Owner() extends UserRole("owner")

case class Validate() extends UserRole("validate")

case class UnknownRole() extends UserRole("unknown")

// Clients
case class DataDebitOwner(dataDebitId: String) extends UserRole(s"datadebit") {
  override def extra: Option[String] = Some(dataDebitId)
}

case class DataCredit(endpoint: String) extends UserRole(s"datacredit") {
  override def extra: Option[String] = Some(endpoint)
}

case class Platform() extends UserRole("platform")

case class NamespaceWrite(namespace: String) extends UserRole(s"namespacewrite") {
  override def extra: Option[String] = Some(namespace)
}

case class NamespaceRead(namespace: String) extends UserRole(s"namespaceread") {
  override def extra: Option[String] = Some(namespace)
}

