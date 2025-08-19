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
import uk.gov.hmrc.iossintermediaryregistration.connectors.GetVatInfoConnector
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers
import uk.gov.hmrc.iossintermediaryregistration.models.des.VatCustomerInfo
import uk.gov.hmrc.iossintermediaryregistration.models.requests.SaveForLaterRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.SaveForLaterResponse
import uk.gov.hmrc.iossintermediaryregistration.repositories.SaveForLaterRepository
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import java.time.{Clock, Instant}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SaveForLaterService @Inject()(
                                     saveForLaterRepository: SaveForLaterRepository,
                                     getVatInfoConnector: GetVatInfoConnector,
                                     clock: Clock
                                   )(implicit executionContext: ExecutionContext) extends Logging {


  def saveUserAnswers(saveForLaterRequest: SaveForLaterRequest): Future[SavedUserAnswers] = {
    val savedUserAnswers: SavedUserAnswers = SavedUserAnswers(
      vrn = saveForLaterRequest.vrn,
      data = saveForLaterRequest.data,
      lastUpdated = Instant.now(clock)
    )

    saveForLaterRepository.set(savedUserAnswers)
  }

  def getSavedUserAnswers(vrn: Vrn)(implicit hc: HeaderCarrier): Future[Option[SaveForLaterResponse]] = {
    saveForLaterRepository.get(vrn).flatMap(mapMaybeUserAnswers)
  }

  def deleteSavedUserAnswers(vrn: Vrn): Future[Boolean] = {
    saveForLaterRepository.clear(vrn)
  }

  private def mapMaybeUserAnswers(maybeSavedUserAnswers: Option[SavedUserAnswers])(implicit hc: HeaderCarrier): Future[Option[SaveForLaterResponse]] = {
    val maybeSaveForLaterResponse = maybeSavedUserAnswers.map { savedUserAnswers =>
      getVatInfo(savedUserAnswers.vrn).map { vatCustomerInfo =>
        SaveForLaterResponse(savedUserAnswers, vatCustomerInfo)
      }
    }

    maybeSaveForLaterResponse match {
      case Some(saveForLaterResponse) => saveForLaterResponse.map(Option.apply)
      case None => None.toFuture
    }
  }

  private def getVatInfo(vrn: Vrn)(implicit hc: HeaderCarrier): Future[VatCustomerInfo] = {
    getVatInfoConnector.getVatCustomerDetails(vrn).map {
      case Right(vatInfo) =>
        vatInfo

      case Left(error) =>
        val message: String = s"There was an error retrieving VatCustomerInfo for the provided VRN with errors: $error."
        val exception = new IllegalStateException(message)
        logger.error(message)
        throw exception
    }
  }
}

