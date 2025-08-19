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

package uk.gov.hmrc.iossintermediaryregistration.services.crypto

import org.scalacheck.Arbitrary.arbitrary
import play.api.Configuration
import play.api.test.Helpers.running
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec

class EncryptionServiceSpec extends BaseSpec {

  private val testString: String = arbitrary[String].sample.value

  "EncryptionService" - {

    "must encrypt text value" in {

      val textToEncrypt: String = testString

      val application = applicationBuilder.build()

      running(application) {

        val configuration: Configuration = application.configuration

        val service: EncryptionService = new EncryptionService(configuration)

        val result = service.encryptField(textToEncrypt)

        result mustBe a[String]
        result mustNot be(textToEncrypt)
      }
    }

    "must decrypt text value" in {

      val textToEncrypt: String = testString

      val application = applicationBuilder.build()

      running(application) {

        val configuration: Configuration = application.configuration

        val service: EncryptionService = new EncryptionService(configuration)

        val encryptedValue = service.encryptField(textToEncrypt)

        val result = service.decryptField(encryptedValue)

        result mustBe a[String]
        result mustBe textToEncrypt
      }
    }

    "must throw a Security Exception if text value can't be decrypted" in {

      val textToEncrypt: String = testString

      val application = applicationBuilder.build()

      running(application) {

        val configuration: Configuration = application.configuration

        val service: EncryptionService = new EncryptionService(configuration)

        val invalidEncryptedValue = service.encryptField(textToEncrypt) + "any"

        val result = intercept[SecurityException](service.decryptField(invalidEncryptedValue))
        result.getMessage mustBe "Unable to decrypt value"
      }
    }
  }
}
