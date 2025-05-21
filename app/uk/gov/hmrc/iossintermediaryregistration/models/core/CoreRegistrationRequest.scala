package uk.gov.hmrc.iossintermediaryregistration.models.core

import play.api.libs.json.{Json, OFormat}

case class CoreRegistrationRequest(source: String,
                                   scheme: Option[String],
                                   searchId: String,
                                   searchIntermediary: Option[String],
                                   searchIdIssuedBy: String)

object CoreRegistrationRequest {
  implicit val format: OFormat[CoreRegistrationRequest] = Json.format[CoreRegistrationRequest]
}
