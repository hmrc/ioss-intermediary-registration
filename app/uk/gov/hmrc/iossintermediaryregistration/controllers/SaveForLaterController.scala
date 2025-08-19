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
import play.api.mvc.Results.Created
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.iossintermediaryregistration.controllers.actions.AuthenticatedControllerComponents
import uk.gov.hmrc.iossintermediaryregistration.models.requests.SaveForLaterRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.SaveForLaterResponse
import uk.gov.hmrc.iossintermediaryregistration.services.SaveForLaterService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SaveForLaterController @Inject()(
                                        cc: AuthenticatedControllerComponents,
                                        saveForLaterService: SaveForLaterService
                                      )(implicit executionContext: ExecutionContext) extends BackendController(cc) {

  def post(): Action[SaveForLaterRequest] = cc.authAndRequireVat()(parse.json[SaveForLaterRequest]).async {
    implicit request =>
      saveForLaterService.saveUserAnswers(request.body).map { savedUserAnswers =>
        Created(Json.toJson(savedUserAnswers))
      }
  }

  def get(): Action[AnyContent] = cc.authAndRequireVat().async {
    implicit request =>
      saveForLaterService.getSavedUserAnswers(request.vrn).map {
        case Some(saveForLaterResponse: SaveForLaterResponse) =>
          Ok(Json.toJson(saveForLaterResponse))

        case _ =>
          NotFound
      }
  }

  def delete(): Action[AnyContent] = cc.authAndRequireVat().async {
    implicit request =>
      saveForLaterService.deleteSavedUserAnswers(request.vrn).map { isDeleted =>
        Ok(Json.toJson(isDeleted))
      }
  }
}