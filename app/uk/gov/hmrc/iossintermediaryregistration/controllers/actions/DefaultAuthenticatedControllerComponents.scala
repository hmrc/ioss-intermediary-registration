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

import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.*

import javax.inject.Inject
import scala.concurrent.ExecutionContext

trait AuthenticatedControllerComponents extends ControllerComponents {

  def actionBuilder: DefaultActionBuilder

  def identify: AuthAction

  def requireVat: VatRequiredAction

  def auth(): ActionBuilder[AuthorisedRequest, AnyContent] =
    actionBuilder andThen
      identify

  def authAndRequireVat(): ActionBuilder[AuthorisedMandatoryVrnRequest, AnyContent] =
    auth() andThen
      requireVat
}

case class DefaultAuthenticatedControllerComponents @Inject()(
                                                               actionBuilder: DefaultActionBuilder,
                                                               parsers: PlayBodyParsers,
                                                               messagesApi: MessagesApi,
                                                               langs: Langs,
                                                               fileMimeTypes: FileMimeTypes,
                                                               executionContext: ExecutionContext,
                                                               identify: AuthAction,
                                                               requireVat: VatRequiredAction
                                                             ) extends AuthenticatedControllerComponents
