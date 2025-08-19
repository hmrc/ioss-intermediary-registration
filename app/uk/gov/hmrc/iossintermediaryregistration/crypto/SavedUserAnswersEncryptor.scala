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

package uk.gov.hmrc.iossintermediaryregistration.crypto

import play.api.libs.json.Json
import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.iossintermediaryregistration.models.{EncryptedSavedUserAnswers, SavedUserAnswers}
import uk.gov.hmrc.iossintermediaryregistration.services.crypto.EncryptionService

import javax.inject.Inject

class SavedUserAnswersEncryptor @Inject()(
                                           encryptionService: EncryptionService,
                                         ) {

  def encryptSavedUserAnswers(savedUserAnswers: SavedUserAnswers, vrn: Vrn): EncryptedSavedUserAnswers = {
    def encryptAnswerValue(answerValue: String): String = encryptionService.encryptField(answerValue)

    EncryptedSavedUserAnswers(
      vrn = vrn,
      data = encryptAnswerValue(savedUserAnswers.data.toString),
      lastUpdated = savedUserAnswers.lastUpdated
    )
  }

  def decryptSavedUserAnswers(encryptedSavedUserAnswers: EncryptedSavedUserAnswers, vrn: Vrn): SavedUserAnswers = {
    def decryptValue(encryptedValue: String): String = encryptionService.decryptField(encryptedValue)

    SavedUserAnswers(
      vrn = vrn,
      data = Json.parse(decryptValue(encryptedSavedUserAnswers.data)),
      lastUpdated = encryptedSavedUserAnswers.lastUpdated
    )
  }
}
