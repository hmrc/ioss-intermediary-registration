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

package uk.gov.hmrc.iossintermediaryregistration.base

import generators.Generators
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.iossintermediaryregistration.controllers.actions.{AuthAction, FakeAuthAction}
import uk.gov.hmrc.iossintermediaryregistration.models.DesAddress
import uk.gov.hmrc.iossintermediaryregistration.models.des.VatCustomerInfo

import java.time.format.DateTimeFormatter
import java.time.{Clock, LocalDate, ZoneId}
import java.util.Locale

trait BaseSpec
  extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with Generators {

  protected val vrn: Vrn = Vrn("123456789")

  val stubClock: Clock = Clock.fixed(LocalDate.now.atStartOfDay(ZoneId.systemDefault).toInstant, ZoneId.systemDefault)

  protected def applicationBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(bind[AuthAction].to[FakeAuthAction])

  val userId: String = "12345-userId"
  val testCredentials: Credentials = Credentials(userId, "GGW")

  val vatCustomerInfo: VatCustomerInfo =
    VatCustomerInfo(
      registrationDate = Some(LocalDate.now(stubClock)),
      desAddress = DesAddress("Line 1", None, None, None, None, Some("AA11 1AA"), "GB"),
      organisationName = Some("Company name"),
      singleMarketIndicator = true,
      individualName = None,
      deregistrationDecisionDate = None
    )

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    .withLocale(Locale.UK)
    .withZone(ZoneId.of("GMT"))

}



