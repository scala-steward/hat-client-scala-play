package org.hatdex.hat.api.models.applications

import org.hatdex.hat.api.models._
import play.api.libs.json.JsValue

case class Drawable(
    small: Option[String],
    normal: String,
    large: Option[String],
    xlarge: Option[String])

case class FormattedText(
    text: String,
    markdown: Option[String],
    html: Option[String])

case class ApplicationGraphics(
    banner: Drawable,
    logo: Drawable,
    screenshots: Seq[Drawable])

/*
 * Application version should follow Semantic Versioning
 */
case class Version(major: Int, minor: Int, patch: Int) extends Ordered[Version] {
  override def toString: String = s"$major.$minor.$patch"

  def greaterThan(other: Version): Boolean =
    (other.major < major) ||
      (other.major == major && other.minor < minor) ||
      (other.major == major && other.minor == minor && other.patch < patch)

  import scala.math.Ordered.orderingToOrdered

  def compare(that: Version): Int = ((this.major, this.minor, this.patch)) compare ((that.major, that.minor, that.patch))
}

object Version {
  private val version = "(\\d+).(\\d+).(\\d+)".r
  def apply(v: String): Version =
    v match {
      case version(major, minor, patch) => Version(major.toInt, minor.toInt, patch.toInt)
      case _                            => throw new RuntimeException(s"value $v for version number does not match expected format")
    }
}

case class ApplicationInfo(
    version: Version,
    published: Boolean,
    name: String,
    headline: String,
    description: FormattedText,
    dataPreview: Seq[DataFeedItem],
    graphics: ApplicationGraphics)

object ApplicationKind {

  trait Kind {
    val kind: String
  }

  case class DataPlug(url: String) extends Kind {
    val kind: String = "DataPlug"
  }
  case class Tool(url: String) extends Kind {
    val kind: String = "Tool"
  }
  case class App(url: String, iosUrl: Option[String], androidUrl: Option[String]) extends Kind {
    val kind: String = "App"
  }
}

case class ApplicationPermissions(
    rolesGranted: Seq[UserRole],
    dataRequired: Option[DataDebitRequest])

object ApplicationSetup {
  trait Setup {
    val kind: String
    val onboarding: Option[Seq[OnboardingStep]]
    val preferences: Option[ApplicationPreferences]
  }

  case class External(
      url: Option[String],
      iosUrl: Option[String],
      androidUrl: Option[String],
      onboarding: Option[Seq[OnboardingStep]],
      preferences: Option[ApplicationPreferences]) extends Setup {
    final val kind: String = "External"
  }

  case class Internal(
      onboarding: Option[Seq[OnboardingStep]],
      preferences: Option[ApplicationPreferences]) extends Setup {
    final val kind: String = "Internal"
  }

  // Preferences are stored at a specific endpoint to make them accessible as data
  // They are stored as a Map (a JSON object) with values of each preference stored in the HAT,
  // but the rest coming from DEX
  case class ApplicationPreferences(
      endpoint: String,
      preferences: Map[String, Preference])

  case class Preference(
      setting: String,
      description: String,
      kind: String,
      defaultValue: Option[JsValue],
      value: Option[JsValue])

  case class OnboardingStep(
      title: String,
      illustration: Drawable,
      description: String)
}

object ApplicationStatus {
  trait Status {
    val kind: String
    val compatibility: Version
    val recentDataCheckEndpoint: Option[String]
  }

  case class Internal(
      compatibility: Version,
      recentDataCheckEndpoint: Option[String]) extends Status {
    final val kind: String = "Internal"
  }

  case class External(
      compatibility: Version,
      statusUrl: String,
      expectedStatus: Int, // TODO: a more detailed status mapping to include a message from reported status
      recentDataCheckEndpoint: Option[String]) extends Status {
    final val kind: String = "External"
  }
}

case class Application(
    id: String,
    kind: ApplicationKind.Kind,
    info: ApplicationInfo,
    permissions: ApplicationPermissions,
    setup: ApplicationSetup.Setup,
    status: ApplicationStatus.Status) {

  def requiresUpdate(fromApplication: Application): Boolean = {
    // if "compatibility" is set to a greater version than version of application updating from, update is required
    status.compatibility.greaterThan(fromApplication.info.version)
  }

  lazy val dataDebitId: Option[String] = permissions.dataRequired.map(_ => s"app-$id")
}

case class ApplicationHistory(
    current: Application,
    history: Option[Seq[Application]])

