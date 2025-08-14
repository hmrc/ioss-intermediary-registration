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

package uk.gov.hmrc.iossintermediaryregistration.models.requests

import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class SaveForLaterRequestSpec extends BaseSpec {

  private val saveForLaterRequest: SaveForLaterRequest = arbitrarySaveForLaterRequest.arbitrary.sample.value

  "SaveForLaterRequest" - {

    "must serialise/deserialise to and from SaveForLaterRequest" - {

      val json = Json.obj(
        "vrn" -> saveForLaterRequest.vrn,
        "data" -> saveForLaterRequest.data
      )

      val expectedResult = SaveForLaterRequest(
        vrn = saveForLaterRequest.vrn,
        data = saveForLaterRequest.data
      )

      Json.toJson(expectedResult) `mustBe` json
      json.validate[SaveForLaterRequest] `mustBe` JsSuccess(expectedResult)
    }

    "must handle missing fields during deserialization" in {

      val expectedJson = Json.obj()

      expectedJson.validate[SaveForLaterRequest] `mustBe` a[JsError]
    }

    "must handle invalid data during deserialization" in {

      val expectedJson = Json.obj(
        "vrn" -> 12345
      )

      expectedJson.validate[SaveForLaterRequest] `mustBe` a[JsError]
    }
  }
}
