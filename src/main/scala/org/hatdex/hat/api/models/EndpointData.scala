package org.hatdex.hat.api.models

import java.util.UUID

import org.hatdex.hat.api.json.HatJsonFormats
import org.joda.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait RichDataJsonFormats extends HatJsonFormats {

  implicit val endpointDataFormat: Format[EndpointData] = (
    (__ \ "endpoint").format[String] and
    (__ \ "recordId").formatNullable[UUID] and
    (__ \ "data").format[JsValue] and
    (__ \ "links").lazyFormatNullable(implicitly[Format[Seq[EndpointData]]]))(EndpointData.apply, unlift(EndpointData.unapply))

  private val fieldTransDateTimeExtractFormat = Json.format[FieldTransformation.DateTimeExtract]
  private val fieldTransTimestampExtractFormat = Json.format[FieldTransformation.TimestampExtract]

  private implicit val apiFieldTransformationFormat: Format[FieldTransformation.Transformation] = new Format[FieldTransformation.Transformation] {
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

  private implicit val apiFilterOperatorFormat: Format[FilterOperator.Operator] = new Format[FilterOperator.Operator] {
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

  implicit val endpointQueryFormat: Format[EndpointQuery] = (
    (__ \ "endpoint").format[String] and
    (__ \ "mapping").formatNullable[JsValue] and
    (__ \ "filters").formatNullable[Seq[EndpointQueryFilter]] and
    (__ \ "links").lazyFormatNullable(implicitly[Format[Seq[EndpointQuery]]]))(EndpointQuery.apply, unlift(EndpointQuery.unapply))

  implicit val propertyQueryFormat: Format[PropertyQuery] = Json.format[PropertyQuery]

  implicit val endpointDatabundleFormat: Format[EndpointDataBundle] = Json.format[EndpointDataBundle]
  implicit val debitBundleFormat: Format[DebitBundle] = Json.format[DebitBundle]
  implicit val dataDebitRequestFormat: Format[DataDebitRequest] = Json.format[DataDebitRequest]
  implicit val dataDebitFormat: Format[RichDataDebit] = Json.format[RichDataDebit]

}

object RichDataJsonFormats extends RichDataJsonFormats

case class EndpointData(
  endpoint: String,
  recordId: Option[UUID],
  data: JsValue,
  links: Option[Seq[EndpointData]])

object FilterOperator {
  trait Operator {
    val operator: String
  }

  case class In(value: JsValue) extends Operator {
    val operator = "in"
  }
  case class Contains(value: JsValue) extends Operator {
    val operator = "contains"
  }
  case class Between(lower: JsValue, upper: JsValue) extends Operator {
    val operator = "between"
  }
  case class Find(search: JsValue) extends Operator {
    val operator = "matches"
  }
}

object FieldTransformation {
  sealed trait Transformation
  case class Identity() extends Transformation
  case class DateTimeExtract(part: String) extends Transformation
  case class TimestampExtract(part: String) extends Transformation
  case class Searchable() extends Transformation
}

case class EndpointQueryFilter(
    field: String,
    transformation: Option[FieldTransformation.Transformation],
    operator: FilterOperator.Operator) {
  def originalField: List[String] = {
    field.split('.').toList
  }
}

case class EndpointQuery(
    endpoint: String,
    mapping: Option[JsValue],
    filters: Option[Seq[EndpointQueryFilter]],
    links: Option[Seq[EndpointQuery]]) {
  def originalField(field: String): Option[List[String]] = {
    mapping.flatMap { m =>
      (m \ field)
        .toOption
        .map(_.as[String].split('.').toList)
    } orElse {
      Some(field.split('.').toList)
    }
  }
}

case class PropertyQuery(
  endpoints: List[EndpointQuery],
  orderBy: Option[String],
  ordering: Option[String],
  limit: Int)

case class EndpointDataBundle(
    name: String,
    bundle: Map[String, PropertyQuery]) {

  lazy val flatEndpointQueries: Seq[EndpointQuery] = bundle.values
    .flatMap(_.endpoints.flatMap(endpointQueries))
    .toSeq

  def endpointQueries(endpointQuery: EndpointQuery): Seq[EndpointQuery] = {
    endpointQuery.links
      .map(_.flatMap(endpointQueries))
      .getOrElse(Seq()) :+ endpointQuery
  }
}

case class RichDataDebit(
    dataDebitKey: String,
    dateCreated: LocalDateTime,
    client: User,
    bundles: Seq[DebitBundle]) {

  lazy val currentBundle: Option[DebitBundle] = bundles.sortBy(_.dateCreated).headOption

  private implicit def dateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan(_ isAfter _)
  lazy val activeBundle: Option[DebitBundle] =
    bundles.filter(_.enabled).sortBy(_.dateCreated).headOption
  lazy val lastUpdated: LocalDateTime =
    bundles.sortBy(_.dateCreated).headOption.map(_.dateCreated).getOrElse(LocalDateTime.now())

}

case class DebitBundle(
  dateCreated: LocalDateTime,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  rolling: Boolean,
  enabled: Boolean,
  bundle: EndpointDataBundle)

case class DataDebitRequest(
  bundle: EndpointDataBundle,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  rolling: Boolean)

