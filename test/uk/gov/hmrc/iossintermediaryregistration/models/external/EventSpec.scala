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

class EventSpec extends BaseSpec {

  private val event: Event = arbitraryEvent.arbitrary.sample.value

  "Event" - {

    "must deserialise/serialise to and from Event" in {

      val json = Json.obj(
        "eventId" -> event.eventId,
        "subject" -> event.subject,
        "groupId" -> event.groupId,
        "event" -> event.event
      )

      val expectedResult = Event(
        eventId = event.eventId,
        subject = event.subject,
        groupId = event.groupId,
        event = event.event
      )

      Json.toJson(expectedResult) mustBe json
      json.validate[Event] mustBe JsSuccess(expectedResult)
    }
  }
}

