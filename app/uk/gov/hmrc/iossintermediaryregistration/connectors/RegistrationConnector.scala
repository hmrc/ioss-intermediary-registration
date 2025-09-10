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

import play.api.http.HeaderNames.*
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, StringContextOps}
import uk.gov.hmrc.iossintermediaryregistration.config.{CreateRegistrationConfig, EtmpDisplayRegistrationConfig}
import uk.gov.hmrc.iossintermediaryregistration.connectors.RegistrationHttpParser.*
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.EtmpRegistrationRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.UnexpectedResponseStatus

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class RegistrationConnector @Inject()(
                                            httpClientV2: HttpClientV2,
                                            createRegistrationConfig: CreateRegistrationConfig,
                                            etmpDisplayRegistrationConfig: EtmpDisplayRegistrationConfig
                                          )(implicit ec: ExecutionContext) extends Logging {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private def createHeaders(correlationId: String): Seq[(String, String)] = createRegistrationConfig.eisEtmpCreateHeaders(correlationId)

  private def getHeaders(correlationId: String): Seq[(String, String)] = etmpDisplayRegistrationConfig.eisEtmpGetHeaders(correlationId)

  def createRegistration(registration: EtmpRegistrationRequest): Future[CreateEtmpRegistrationResponse] = {

    val correlationId = UUID.randomUUID.toString
    val headersWithCorrelationId = createHeaders(correlationId)
    val headersWithoutAuth = headersWithCorrelationId.filterNot {
      case (key, _) => key.matches(AUTHORIZATION)
    }

    logger.info(s"Sending create request to etmp with headers $headersWithoutAuth")

    httpClientV2.post(url"${createRegistrationConfig.baseUrl}vec/iosssubscription/subdatatransfer/v1")
      .withBody(Json.toJson(registration))
      .setHeader(headersWithCorrelationId: _*)
      .execute[CreateEtmpRegistrationResponse].recover {
        case e: HttpException =>
          logger.error(s"Unexpected response from etmp registration ${e.getMessage}", e)
          Left(UnexpectedResponseStatus(e.responseCode, s"Unexpected response from ${serviceName}, received status ${e.responseCode}"))
      }
  }

  def getRegistration(intermediaryNumber: String): Future[EtmpDisplayRegistrationResponse] = {

    val correlationId = UUID.randomUUID.toString
    val headersWithCorrelationId = getHeaders(correlationId)

    httpClientV2.get(url"${etmpDisplayRegistrationConfig.baseUrl}vec/iossregistration/viewreg/v1/$intermediaryNumber")
      .setHeader(headersWithCorrelationId: _*)
      .execute[EtmpDisplayRegistrationResponse].recover {
        case e: HttpException =>
          logger.error(s"Unexpected response from ETMP Display Registration ${e.getMessage}", e)
          Left(UnexpectedResponseStatus(e.responseCode, s"Unexpected response from ETMP Display Registration with status ${e.responseCode}"))
      }
  }
}
