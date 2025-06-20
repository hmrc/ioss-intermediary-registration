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

package uk.gov.hmrc.iossintermediaryregistration.controllers

import play.api.libs.json.Json
import play.api.mvc.Action
import uk.gov.hmrc.iossintermediaryregistration.connectors.ValidateCoreRegistrationConnector
import uk.gov.hmrc.iossintermediaryregistration.controllers.actions.AuthenticatedControllerComponents
import uk.gov.hmrc.iossintermediaryregistration.logging.Logging
import uk.gov.hmrc.iossintermediaryregistration.models.core.CoreRegistrationRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ValidateCoreRegistrationController @Inject()(
                                                    cc: AuthenticatedControllerComponents,
                                                    validateCoreRegistrationConnector: ValidateCoreRegistrationConnector,
                                                  )
                                                  (implicit ec: ExecutionContext)
  extends BackendController(cc) with Logging {

  def post: Action[CoreRegistrationRequest] = cc.authAndRequireVat()(parse.json[CoreRegistrationRequest]).async {
    implicit request =>

      validateCoreRegistrationConnector.validateCoreRegistration(request.body).map {
        case Left(value) => InternalServerError(Json.toJson(value.body))
        case Right(value) =>
          logger.info(s"Received ${Json.toJson(value)} from core validation endpoint")
          Ok(Json.toJson(value))

      }
  }

}