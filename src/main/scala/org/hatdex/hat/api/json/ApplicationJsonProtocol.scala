package org.hatdex.hat.api.json

import org.hatdex.hat.api.models.applications._
import org.hatdex.hat.api.models.{ DataDebitRequest, RichDataJsonFormats, UserRole }
import play.api.libs.json._

import scala.util.Try

trait ApplicationJsonProtocol extends DataFeedItemJsonProtocol {
  protected implicit val drawableFormat: Format[Drawable] = Json.format[Drawable]
  protected implicit val applicationGraphicsFormat: Format[ApplicationGraphics] = Json.format[ApplicationGraphics]
  protected implicit val formattedTextFormat: Format[FormattedText] = Json.format[FormattedText]

  protected implicit val versionFormat: Format[Version] = new Format[Version] {
    def reads(json: JsValue): JsResult[Version] =
      Try(Version(json.as[String]))
        .map(v => JsSuccess(v))
        .recover({
          case e => JsError(e.getMessage)
        })
        .get

    def writes(version: Version): JsValue = JsString(version.toString)
  }

  protected implicit val applicationInfoFormat: Format[ApplicationInfo] = Json.format[ApplicationInfo]

  protected implicit val userRoleFormat: Format[UserRole] = HatJsonFormats.userRoleFormat
  protected implicit val dataDebitRequestFormat: Format[DataDebitRequest] = RichDataJsonFormats.dataDebitRequestFormat
  protected implicit val applicationPermissionsFormat: Format[ApplicationPermissions] = Json.format[ApplicationPermissions]

  // Application Status JSON formats

  protected implicit val applicationStatusInternalFormat: Format[ApplicationStatus.Internal] = Json.format[ApplicationStatus.Internal]
  protected implicit val applicationStatusExternalFormat: Format[ApplicationStatus.External] = Json.format[ApplicationStatus.External]

  protected implicit val applicationStatusformat: Format[ApplicationStatus.Status] = new Format[ApplicationStatus.Status] {
    def reads(json: JsValue): JsResult[ApplicationStatus.Status] = (json \ "kind").as[String] match {
      case "Internal" => Json.fromJson[ApplicationStatus.Internal](json)(applicationStatusInternalFormat)
      case "External" => Json.fromJson[ApplicationStatus.External](json)(applicationStatusExternalFormat)
      case kind       => JsError(s"Unexpected JSON value $kind in $json")
    }

    def writes(status: ApplicationStatus.Status): JsValue = {
      val (statusJson, statusKind) = status match {
        case s: ApplicationStatus.Internal => (Json.toJson(s)(applicationStatusInternalFormat), JsString("Internal"))
        case s: ApplicationStatus.External => (Json.toJson(s)(applicationStatusExternalFormat), JsString("External"))
      }
      statusJson.as[JsObject] + (("kind", statusKind))
    }
  }

  // Application Settings JSON formats

  protected implicit val applicationSetupSettingsFormat: Format[ApplicationSetup.SetupSettings] = Json.format[ApplicationSetup.SetupSettings]
  protected implicit val applicationSetupOnboardingStepFormat: Format[ApplicationSetup.OnboardingStep] = Json.format[ApplicationSetup.OnboardingStep]
  protected implicit val applicationSetupInternalFormat: Format[ApplicationSetup.Internal] = Json.format[ApplicationSetup.Internal]
  protected implicit val applicationSetupExternalFormat: Format[ApplicationSetup.External] = Json.format[ApplicationSetup.External]

  protected implicit val applicationSetupFormat: Format[ApplicationSetup.Setup] = new Format[ApplicationSetup.Setup] {
    def reads(json: JsValue): JsResult[ApplicationSetup.Setup] = (json \ "kind").as[String] match {
      case "External" => Json.fromJson[ApplicationSetup.External](json)(applicationSetupExternalFormat)
      case "Internal" => Json.fromJson[ApplicationSetup.Internal](json)(applicationSetupInternalFormat)
      case kind       => JsError(s"Unexpected JSON value $kind in $json")
    }

    def writes(status: ApplicationSetup.Setup): JsValue = {
      val (statusJson, statusKind) = status match {
        case s: ApplicationSetup.Internal => (Json.toJson(s)(applicationSetupInternalFormat), JsString("Internal"))
        case s: ApplicationSetup.External => (Json.toJson(s)(applicationSetupExternalFormat), JsString("External"))
      }
      statusJson.as[JsObject] + (("kind", statusKind))
    }
  }

  // Application Kind JSON formats

  protected implicit val applicationKindDataPlugFormat: Format[ApplicationKind.DataPlug] = Json.format[ApplicationKind.DataPlug]
  protected implicit val applicationKindToolFormat: Format[ApplicationKind.Tool] = Json.format[ApplicationKind.Tool]
  protected implicit val applicationKindAppFormat: Format[ApplicationKind.App] = Json.format[ApplicationKind.App]

  protected implicit val applicationKindFormat: Format[ApplicationKind.Kind] = new Format[ApplicationKind.Kind] {
    def reads(json: JsValue): JsResult[ApplicationKind.Kind] = (json \ "kind").as[String] match {
      case "DataPlug" => Json.fromJson[ApplicationKind.DataPlug](json)(applicationKindDataPlugFormat)
      case "Tool"     => Json.fromJson[ApplicationKind.Tool](json)(applicationKindToolFormat)
      case "App"      => Json.fromJson[ApplicationKind.App](json)(applicationKindAppFormat)
      case kind       => JsError(s"Unexpected JSON value $kind in $json")
    }

    def writes(status: ApplicationKind.Kind): JsValue = {
      val (kindJson, kind) = status match {
        case k: ApplicationKind.DataPlug => (Json.toJson(k)(applicationKindDataPlugFormat), JsString("DataPlug"))
        case k: ApplicationKind.Tool     => (Json.toJson(k)(applicationKindToolFormat), JsString("Tool"))
        case k: ApplicationKind.App      => (Json.toJson(k)(applicationKindAppFormat), JsString("App"))
      }
      kindJson.as[JsObject] + (("kind", kind))
    }
  }

  implicit val applicationFormat: Format[Application] = Json.format[Application]

  implicit val applicationHistoryFormat: Format[ApplicationHistory] = Json.format[ApplicationHistory]

  implicit val applicationStatusFormat: Format[HatApplication] = Json.format[HatApplication]
}

object ApplicationJsonProtocol extends ApplicationJsonProtocol {

}
