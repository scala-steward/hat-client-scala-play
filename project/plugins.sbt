logLevel := Level.Warn

resolvers += Resolver.typesafeRepo("releases")

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")

// Code Quality
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

// S3 based SBT resolver
resolvers += Resolver.jcenterRepo
addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.19.0")

// run "sbt dependencyUpdates" to check maven for updates or "sbt ";dependencyUpdates; reload plugins; dependencyUpdates" for sbt plugins
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

resolvers += "HAT Library Artifacts Snapshots" at "https://s3-eu-west-1.amazonaws.com/library-artifacts-snapshots.hubofallthings.com"
resolvers += "HAT Library Artifacts Releases" at "https://s3-eu-west-1.amazonaws.com/library-artifacts-releases.hubofallthings.com"
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.4")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.19")
addSbtPlugin("io.dataswift" % "sbt-scalafmt-common" % "0.1.1")

// ScalaFMT, ScalaFIX and Tools Common
// addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.4")
// addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.19")
// addSbtPlugin("io.dataswift" % "sbt-scalafmt-common" % "0.1.1-SNAPSHOT")
