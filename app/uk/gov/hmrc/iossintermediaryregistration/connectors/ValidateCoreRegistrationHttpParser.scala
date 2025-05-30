package uk.gov.hmrc.iossintermediaryregistration.connectors

import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.models.responses.EisError
import uk.gov.hmrc.iossintermediaryregistration.models.responses.ErrorResponse
import uk.gov.hmrc.iossintermediaryregistration.models.responses.InvalidJson
import uk.gov.hmrc.iossintermediaryregistration.models.responses.UnexpectedResponseStatus
import uk.gov.hmrc.iossintermediaryregistration.models.core._

import java.time.Instant
import java.util.UUID
object ValidateCoreRegistrationHttpParser extends Logging {

  type ValidateCoreRegistrationResponse = Either[ErrorResponse, CoreRegistrationValidationResult]

  implicit object ValidateCoreRegistrationReads extends HttpReads[ValidateCoreRegistrationResponse] {
    override def read(method: String, url: String, response: HttpResponse): ValidateCoreRegistrationResponse = {
      response.status match {
        case OK => response.json.validate[CoreRegistrationValidationResult] match {
          case JsSuccess(validateCoreRegistration, _) => Right(validateCoreRegistration)
          case JsError(errors) =>
            logger.error(s"Failed trying to parse JSON $errors. JSON was ${response.json}")
            Left(InvalidJson)
        }

        case status =>
          logger.info(s"Response received from EIS ${response.status} with body ${response.body}")
          if (response.body.isEmpty) {
            val uuid = UUID.randomUUID()
            logger.error(s"Response received from EIS ${response.status} with empty body and self-generated correlationId $uuid")
            Left(
              EisError(
                EisErrorResponse(Instant.now(), status.toString, "The response body was empty")
              ))
          } else {
            response.json.validateOpt[EisErrorResponse] match {
              case JsSuccess(Some(eisErrorResponse), _) =>
                logger.error(s"There was an error from EIS when submitting a validation with status $status and $eisErrorResponse")
                Left(EisError(eisErrorResponse))

              case _ =>
                logger.error(s"Received UnexpectedResponseStatus with status code $status with body ${response.body}")
                Left(UnexpectedResponseStatus(status, s"Received unexpected response code $status"))
            }
          }
      }
    }
  }

}

