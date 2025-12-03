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

package uk.gov.hmrc.iossintermediaryregistration.models.external


import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EventDataSpec extends BaseSpec {

  private val eventData: EventData = arbitraryEventData.arbitrary.sample.value

  "EventData" - {

    "must deserialise/serialise to and from EventData" in {

      val json = Json.obj(
        "emailAddress" -> eventData.emailAddress,
        "tags" -> eventData.tags
      )

      val expectedResult = EventData(
        emailAddress = eventData.emailAddress,
        tags = eventData.tags
      )

      Json.toJson(expectedResult) mustBe json
      json.validate[EventData] mustBe JsSuccess(expectedResult)
    }
  }
}

