package uk.gov.hmrc.iossintermediaryregistration.models.core

import play.api.libs.json.{Json, OFormat}

import java.time.Instant

case class EisErrorResponse(
                             timestamp: Instant,
                             error: String,
                             errorMessage: String
                           )

object EisErrorResponse {

  implicit val format: OFormat[EisErrorResponse] = Json.format[EisErrorResponse]

}
