/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 2 / 2017
 *
 */

import sbt.Keys._
import sbt._

////*******************************
//// Basic settings
////*******************************
object BuildSettings extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.hatdex",
    version := "2.6.9",
    resolvers ++= Dependencies.resolvers,
    scalaVersion := Dependencies.Versions.scalaVersion,
    crossScalaVersions := Dependencies.Versions.crossScala,
    name := "HAT Client Scala Play",
    description := "HAT HTTP API wrapper in Scala",
    licenses += ("Mozilla Public License 2.0", url("https://www.mozilla.org/en-US/MPL/2.0")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/Hub-of-all-Things/hat-client-scala-play"),
        "scm:git@github.com:Hub-of-all-Things/hat-client-scala-play.git"
      )
    ),
    homepage := Some(url("https://hubofallthings.com")),
    developers := List(
      Developer(
        id    = "AndriusA",
        name  = "Andrius Aucinas",
        email = "andrius@smart-e.org",
        url   = url("http://smart-e.org")
      )
    ),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-language:postfixOps", // Allow postfix operators
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
      ),
    scalacOptions in Test ~= { (options: Seq[String]) =>
      options filterNot (_ == "-Ywarn-dead-code") // Allow dead code in tests (to support using mockito).
    },
    parallelExecution in Test := false,
    fork in Test := true,
    // Needed to avoid https://github.com/travis-ci/travis-ci/issues/3775 in forked tests
    // in Travis with `sudo: false`.
    // See https://github.com/sbt/sbt/issues/653
    // and https://github.com/travis-ci/travis-ci/issues/3775
    javaOptions += "-Xmx1G") ++ scalariformPrefs

  // Scalariform settings for automatic code reformatting
  import com.typesafe.sbt.SbtScalariform._
  import scalariform.formatter.preferences._

  lazy val scalariformPrefs = Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(FormatXml, false)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(CompactControlReadability, true)
      .setPreference(DanglingCloseParenthesis, Prevent))
}
