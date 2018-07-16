package org.hatdex.hat.api.json

import java.util.UUID

import org.hatdex.hat.api.models._
import org.joda.time.{ DateTime, Duration, LocalDateTime, Period }
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.Try

trait RichDataJsonFormats extends HatJsonFormats with JodaWrites with JodaReads {

  val endpointDataWrites: Writes[EndpointData] = (
    (__ \ "endpoint").write[String] and
    (__ \ "recordId").writeNullable[UUID] and
    (__ \ "sourceTimestamp").writeNullable[DateTime] and
    (__ \ "sourceUniqueId").writeNullable[String] and
    (__ \ "data").write[JsValue] and
    (__ \ "links").lazyWriteNullable(implicitly[Format[Seq[EndpointData]]]))(unlift(EndpointData.unapply))

  val endpointDataReads: Reads[EndpointData] = (
    (__ \ "endpoint").read[String].filter(JsonValidationError("Endpoint invalid"))(_.matches("[0-9a-z-/]+")) and
    (__ \ "recordId").readNullable[UUID] and
    (__ \ "sourceTimestamp").readNullable[DateTime] and
    (__ \ "sourceUniqueId").readNullable[String] and
    (__ \ "data").read[JsValue] and
    (__ \ "links").lazyReadNullable(implicitly[Reads[Seq[EndpointData]]]))(EndpointData.apply _)

  implicit val endpointDataFormat: Format[EndpointData] = Format(endpointDataReads, endpointDataWrites)

  private val fieldTransDateTimeExtractFormat = Json.format[FieldTransformation.DateTimeExtract]
  private val fieldTransTimestampExtractFormat = Json.format[FieldTransformation.TimestampExtract]

  implicit val apiFieldTransformationFormat: Format[FieldTransformation.Transformation] = new Format[FieldTransformation.Transformation] {
    def reads(json: JsValue): JsResult[FieldTransformation.Transformation] = (json \ "transformation").as[String] match {
      case "identity"         => JsSuccess(FieldTransformation.Identity())
      case "datetimeExtract"  => Json.fromJson[FieldTransformation.DateTimeExtract](json)(fieldTransDateTimeExtractFormat)
      case "timestampExtract" => Json.fromJson[FieldTransformation.TimestampExtract](json)(fieldTransTimestampExtractFormat)
      case "searchable"       => JsSuccess(FieldTransformation.Searchable())
      case transformation     => JsError(s"Unexpected JSON value $transformation in $json")
    }

    def writes(transformation: FieldTransformation.Transformation): JsValue = {
      val (transformed, tType) = transformation match {
        case _: FieldTransformation.Identity          => (Json.obj(), JsString("identity"))
        case ds: FieldTransformation.DateTimeExtract  => (Json.toJson(ds)(fieldTransDateTimeExtractFormat), JsString("datetimeExtract"))
        case ds: FieldTransformation.TimestampExtract => (Json.toJson(ds)(fieldTransTimestampExtractFormat), JsString("timestampExtract"))
        case _: FieldTransformation.Searchable        => (Json.obj(), JsString("searchable"))
      }
      transformed.as[JsObject] + (("transformation", tType))
    }
  }

  private val filterOperatorContainsFormat = Json.format[FilterOperator.Contains]
  private val filterOperatorInFormat = Json.format[FilterOperator.In]
  private val filterOperatorBetweenFormat = Json.format[FilterOperator.Between]
  private val filterOperatorFindFormat = Json.format[FilterOperator.Find]

  implicit val apiFilterOperatorFormat: Format[FilterOperator.Operator] = new Format[FilterOperator.Operator] {
    def reads(json: JsValue): JsResult[FilterOperator.Operator] = (json \ "operator").as[String] match {
      case "contains"     => Json.fromJson[FilterOperator.Contains](json)(filterOperatorContainsFormat)
      case "in"           => Json.fromJson[FilterOperator.In](json)(filterOperatorInFormat)
      case "between"      => Json.fromJson[FilterOperator.Between](json)(filterOperatorBetweenFormat)
      case "find"         => Json.fromJson[FilterOperator.Find](json)(filterOperatorFindFormat)
      case transformation => JsError(s"Unexpected JSON value $transformation in $json")
    }

    def writes(transformation: FilterOperator.Operator): JsValue = {
      val (transformed, tType) = transformation match {
        case ds: FilterOperator.Contains => (Json.toJson(ds)(filterOperatorContainsFormat), JsString("contains"))
        case ds: FilterOperator.In       => (Json.toJson(ds)(filterOperatorInFormat), JsString("in"))
        case ds: FilterOperator.Between  => (Json.toJson(ds)(filterOperatorBetweenFormat), JsString("between"))
        case ds: FilterOperator.Find     => (Json.toJson(ds)(filterOperatorFindFormat), JsString("find"))
      }
      transformed.as[JsObject] + (("operator", tType))
    }
  }

  implicit val endpointQueryFilterFormat: Format[EndpointQueryFilter] = Json.format[EndpointQueryFilter]

  val endpointQueryRead: Reads[EndpointQuery] = (
    (__ \ "endpoint").read[String].filter(JsonValidationError("Endpoint invalid"))(_.matches("[0-9a-z-/]+")) and
    (__ \ "mapping").readNullable[JsValue] and
    (__ \ "filters").readNullable[Seq[EndpointQueryFilter]] and
    (__ \ "links").lazyReadNullable(implicitly[Format[Seq[EndpointQuery]]]))(EndpointQuery.apply _)

  val endpointQueryWrites: Writes[EndpointQuery] = (
    (__ \ "endpoint").write[String] and
    (__ \ "mapping").writeNullable[JsValue] and
    (__ \ "filters").writeNullable[Seq[EndpointQueryFilter]] and
    (__ \ "links").lazyWriteNullable(implicitly[Format[Seq[EndpointQuery]]]))(unlift(EndpointQuery.unapply))

  implicit val endpointQueryFormat: Format[EndpointQuery] = Format(endpointQueryRead, endpointQueryWrites)

  implicit val propertyQueryFormat: Format[PropertyQuery] = Json.format[PropertyQuery]

  private val endpointDatabundleRead: Reads[EndpointDataBundle] = (
    (__ \ "name").read[String].filter(JsonValidationError("Bundle name invalid"))(_.matches("[0-9a-zA-Z-]+")) and
    (__ \ "bundle").read[Map[String, PropertyQuery]])(EndpointDataBundle.apply _)

  private val endpointDatabundleWrite: Writes[EndpointDataBundle] = Json.writes[EndpointDataBundle]

  implicit val endpointDatabundleFormat: Format[EndpointDataBundle] = Format(endpointDatabundleRead, endpointDatabundleWrite)

  @Deprecated
  implicit val debitBundleFormat: Format[DebitBundle] = Json.format[DebitBundle]
  @Deprecated
  implicit val dataDebitRequestFormat: Format[DataDebitRequest] = Json.format[DataDebitRequest]

  private val richDataDebitReads: Reads[RichDataDebit] = (
    (__ \ "dataDebitKey").read[String].filter(JsonValidationError("Data Debit Key invalid"))(_.matches("[0-9a-zA-Z-]+")) and
    (__ \ "dateCreated").read[LocalDateTime] and
    (__ \ "client").read[User] and
    (__ \ "bundles").read[Seq[DebitBundle]])(RichDataDebit.apply _)
  private val richDataDebitWrites: Writes[RichDataDebit] = Json.writes[RichDataDebit]

  @Deprecated
  implicit val richDataDebitFormat: Format[RichDataDebit] = Format(richDataDebitReads, richDataDebitWrites)
  @Deprecated
  implicit val dataDebitValuesFormat: Format[RichDataDebitData] = Json.format[RichDataDebitData]

  /*
  * Duration Json formats
   */
  implicit val durationWrites: Writes[Duration] = new Writes[Duration] {
    def writes(o: Duration): JsValue = JsNumber(o.getMillis)
  }
  implicit val durationReads: Reads[Duration] = new Reads[Duration] {
    def reads(json: JsValue): JsResult[Duration] = json match {
      case JsNumber(value) ⇒ JsSuccess(new Duration(value.toLong))
      case JsString(value) ⇒ Try(Duration.parse(value)).map(p ⇒ JsSuccess(p))
        .recover({ case e ⇒ JsError(Seq(JsPath() -> Seq(JsonValidationError(s"Could not parse period: ${e.getMessage}")))) })
        .get
      case _ ⇒ JsError(Seq(JsPath() -> Seq(JsonValidationError("validate.error.expected.period"))))
    }
  }

  /*
  * Period Json formats
   */
  implicit val periodWrites: Writes[Period] = new Writes[Period] {
    def writes(o: Period): JsValue = JsNumber(o.getMillis)
  }
  implicit val periodReads: Reads[Period] = new Reads[Period] {
    def reads(json: JsValue): JsResult[Period] = json match {
      case JsNumber(value) ⇒ JsSuccess(new Period(value.toLong))
      case JsString(value) ⇒ Try(Period.parse(value)).map(p ⇒ JsSuccess(p))
        .recover({ case e ⇒ JsError(Seq(JsPath() -> Seq(JsonValidationError(s"Could not parse period: ${e.getMessage}")))) })
        .get
      case _ ⇒ JsError(Seq(JsPath() -> Seq(JsonValidationError("validate.error.expected.period"))))
    }
  }

  implicit val dataDebitSetupRequestFormat: Format[DataDebitSetupRequest] = Json.format[DataDebitSetupRequest]
  implicit val dataDebitDataFormat: Format[DataDebitData] = Json.format[DataDebitData]

  import OWritesOps.from
  private val dataDebitPermissionsWrites: Writes[DataDebitPermissions] = Json.writes[DataDebitPermissions]
    .addField("active", _.active)
    .addField("end", _.end)

  private val dataDebitPermissionsReads: Reads[DataDebitPermissions] = Json.reads[DataDebitPermissions]
  implicit val dataDebitPermissionsFormat: Format[DataDebitPermissions] = Format(dataDebitPermissionsReads, dataDebitPermissionsWrites)

  private val ddWrites: Writes[DataDebit] = Json.writes[DataDebit]
    .addField("active", _.active)
    .addField("accepted", _.accepted)
    .addField("start", _.start)
    .addField("end", _.end)
    .addField("permissionsActive", _.activePermissions)
    .addField("permissionsLatest", _.currentPermissions)

  private val dataDebitReads: Reads[DataDebit] = Json.reads[DataDebit]
  implicit val dataDebitFormat: Format[DataDebit] = Format(dataDebitReads, ddWrites)
}

object RichDataJsonFormats extends RichDataJsonFormats