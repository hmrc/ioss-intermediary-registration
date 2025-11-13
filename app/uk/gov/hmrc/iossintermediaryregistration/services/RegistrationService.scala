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

package uk.gov.hmrc.iossintermediaryregistration.services

import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.iossintermediaryregistration.connectors.RegistrationHttpParser.{AmendEtmpRegistrationResponse, CreateEtmpRegistrationResponse}
import uk.gov.hmrc.iossintermediaryregistration.connectors.{GetVatInfoConnector, RegistrationConnector}
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.EtmpRegistrationRequest
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.amend.{AmendRegistrationResponse, EtmpAmendRegistrationRequest}
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.display.RegistrationWrapper
import uk.gov.hmrc.iossintermediaryregistration.models.responses.EtmpException
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationService @Inject()(
                                     registrationConnector: RegistrationConnector,
                                     getVatInfoConnector: GetVatInfoConnector
                                   )(implicit executionContext: ExecutionContext) extends Logging {

  def createRegistration(etmpRegistrationRequest: EtmpRegistrationRequest): Future[CreateEtmpRegistrationResponse] = {
    registrationConnector.createRegistration(etmpRegistrationRequest)
  }

  def getRegistrationWrapper(intermediaryNumber: String, vrn: Vrn)(implicit hc: HeaderCarrier): Future[RegistrationWrapper] = {

    for {
      vatCustomerInfoResponse <- getVatInfoConnector.getVatCustomerDetails(vrn)
      etmpDisplayRegistrationResponse <- registrationConnector.getRegistration(intermediaryNumber)
    } yield (vatCustomerInfoResponse, etmpDisplayRegistrationResponse) match {
      case (Right(vatCustomerInfo), Right(etmpDisplayRegistration)) =>
        RegistrationWrapper(
          vatInfo = vatCustomerInfo,
          etmpDisplayRegistration = etmpDisplayRegistration
        )

      case (Left(vatCustomerInfoError), Left(etmpDisplayRegistrationError)) =>
        val errorMessage = s"There was an error retrieving both vatCustomerInfo and etmpDisplayRegistration from ETMP" +
          s"with errors: ${vatCustomerInfoError.body} and ${etmpDisplayRegistrationError.body}."
        logger.error(errorMessage)
        throw EtmpException(errorMessage)

      case (Left(vatCustomerInfoError), _) =>
        val errorMessage = s"There was an error retrieving vatCustomerInfo from ETMP" +
          s"with errors: ${vatCustomerInfoError.body}."
        logger.error(errorMessage)
        throw EtmpException(errorMessage)

      case (_, Left(etmpDisplayRegistrationError)) =>
        val errorMessage = s"There was an error retrieving etmpDisplayRegistration from ETMP" +
          s"with errors: ${etmpDisplayRegistrationError.body}."
        logger.error(errorMessage)
        throw EtmpException(errorMessage)
    }
  }

  def amendRegistration(etmpRegistrationRequest: EtmpAmendRegistrationRequest): Future[AmendEtmpRegistrationResponse] = {
    registrationConnector.amendRegistration(etmpRegistrationRequest)
  }
}
