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

package uk.gov.hmrc.iossintermediaryregistration.controllers.actions

import play.api.mvc.Results.Unauthorized
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VatRequiredAction @Inject()(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[AuthorisedRequest, AuthorisedMandatoryVrnRequest] with Logging {

  override protected def refine[A](request: AuthorisedRequest[A]): Future[Either[Result, AuthorisedMandatoryVrnRequest[A]]] = {

    request.vrn match {
      case None =>
        logger.info("insufficient enrolments")
        Left(Unauthorized).toFuture
      case Some(vrn) =>
        Right(AuthorisedMandatoryVrnRequest(request.request, request.credentials, request.userId, vrn)).toFuture
    }
  }
}
