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

package uk.gov.hmrc.iossintermediaryregistration.models.etmp

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EtmpIdTypeSpec extends BaseSpec with ScalaCheckPropertyChecks {

  "EtmpIdType" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(EtmpIdType.values)

      forAll(gen) {
        etmpIdType =>

          JsString(etmpIdType.toString).validate[EtmpIdType].asOpt.value mustBe etmpIdType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String].suchThat(!EtmpMessageType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValues =>

          JsString(invalidValues).validate[EtmpMessageType] mustBe JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(EtmpIdType.values)

      forAll(gen) {
        etmpIdType =>

          Json.toJson(etmpIdType) mustBe JsString(etmpIdType.toString)
      }
    }
  }
}

