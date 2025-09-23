/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.iossintermediaryregistration.connectors

import play.api.http.Status.{CREATED, NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.iossintermediaryregistration.models.*
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.display.EtmpDisplayRegistration
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.amend.AmendRegistrationResponse
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.responses.{EtmpEnrolmentErrorResponse, EtmpEnrolmentResponse}
import uk.gov.hmrc.iossintermediaryregistration.models.responses.*

object RegistrationHttpParser extends BaseHttpParser {

  override val serviceName: String = "etmp registration"

  type CreateEtmpRegistrationResponse = Either[ErrorResponse, EtmpEnrolmentResponse]
  type EtmpDisplayRegistrationResponse = Either[ErrorResponse, EtmpDisplayRegistration]
  type AmendEtmpRegistrationResponse = Either[ErrorResponse, AmendRegistrationResponse]

  implicit object CreateRegistrationReads extends HttpReads[CreateEtmpRegistrationResponse] {
    override def read(method: String, url: String, response: HttpResponse): CreateEtmpRegistrationResponse =
      response.status match {
        case CREATED => response.json.validate[EtmpEnrolmentResponse] match {
          case JsSuccess(enrolmentResponse, _) => Right(enrolmentResponse)
          case JsError(errors) =>
            logger.error(s"Failed trying to parse JSON, but was successfully created ${response.body} $errors")
            Left(InvalidJson)
        }
        case status =>
          if (response.body.nonEmpty) {
            response.json.validate[EtmpEnrolmentErrorResponse] match {
              case JsSuccess(enrolmentErrorResponse, _) =>
                Left(EtmpEnrolmentError(
                  code = enrolmentErrorResponse.errorDetail.errorCode.getOrElse("No error code"),
                  body = enrolmentErrorResponse.errorDetail.errorMessage.getOrElse("No error message")
                ))
              case JsError(errors) =>
                logger.error(s"Failed trying to parse JSON with status $status and body ${response.body} json parse error: $errors")
                Left(UnexpectedResponseStatus(status, s"Unexpected response from ${serviceName}, received status $status"))
            }
          } else {
            logger.error(s"Failed trying to parse empty JSON with status ${response.status} and body ${response.body}")
            logger.warn(s"Unexpected response from core registration, received status $status")
            Left(UnexpectedResponseStatus(status, s": Unexpected response from ${serviceName}, received status $status"))
          }
      }
  }

  implicit object EtmpDisplayRegistrationReads extends HttpReads[EtmpDisplayRegistrationResponse] {

    override def read(method: String, url: String, response: HttpResponse): EtmpDisplayRegistrationResponse = {
      response.status match {
        case OK =>
          response.json.validate[EtmpDisplayRegistration] match {
            case JsSuccess(etmpDisplayRegistrationResponse, _) => Right(etmpDisplayRegistrationResponse)
            case JsError(errors) =>
              logger.error(s"Failed trying to parse EtmpDisplayRegistration response JSON with response status: ${response.status}, with errors: $errors.")
              Left(InvalidJson)
          }

        case status =>
          logger.error(s"An unknown error occurred when trying to retrieve EtmpDisplayRegistration with status: $status and response body: ${response.body}.")
          Left(ServerError)
      }
    }
  }

  implicit object AmendRegistrationReads extends HttpReads[AmendEtmpRegistrationResponse] {
    override def read(method: String, url: String, response: HttpResponse): AmendEtmpRegistrationResponse =
      response.status match {
        case OK => response.json.validate[AmendRegistrationResponse] match {
          case JsSuccess(amendRegistrationResponse, _) => Right(amendRegistrationResponse)
          case JsError(errors) =>
            logger.error(s"Failed trying to parse JSONwith status ${response.status} and body ${response.body} errors $errors")
            Left(InvalidJson)
        }
        case NOT_FOUND =>
          logger.warn(s"url not reachable")
          Left(NotFound)
        case status =>
          logger.error(s"Unknown error happened on amend registration $status with body ${response.body}")
          Left(ServerError)
      }
  }
}
