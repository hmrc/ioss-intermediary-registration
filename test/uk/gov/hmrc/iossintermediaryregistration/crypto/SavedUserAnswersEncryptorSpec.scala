package uk.gov.hmrc.iossintermediaryregistration.crypto

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.models.{EncryptedSavedUserAnswers, SavedUserAnswers}
import uk.gov.hmrc.iossintermediaryregistration.services.crypto.EncryptionService

class SavedUserAnswersEncryptorSpec extends BaseSpec {

  private val mockEncryptionService: EncryptionService = mock[EncryptionService]

  private val savedUserAnswers: SavedUserAnswers = arbitrarySavedUserAnswers.arbitrary.sample.value
  private val encryptedValue: String = "encryptedValue"

  "SavedUserAnswersEncryptor" - {

    ".encrypt" - {

      "must encrypt and return a EncryptedSavedUserAnswers" in {

        when(mockEncryptionService.encryptField(any())) thenReturn encryptedValue

        val service = new SavedUserAnswersEncryptor(mockEncryptionService)

        val expectedResult = EncryptedSavedUserAnswers(savedUserAnswers.vrn, encryptedValue, savedUserAnswers.lastUpdated)
        val result = service.encryptSavedUserAnswers(savedUserAnswers, savedUserAnswers.vrn)

        result mustBe expectedResult
      }
    }

    ".decrypt" - {

      "must decrypt and return a SavedUserAnswers" in {

        when(mockEncryptionService.decryptField(any())) thenReturn Json.parse(savedUserAnswers.data.toString()).toString()

        val service = new SavedUserAnswersEncryptor(mockEncryptionService)

        val encryptedSavedUserAnswers = EncryptedSavedUserAnswers(savedUserAnswers.vrn, encryptedValue, savedUserAnswers.lastUpdated)

        val expectedResult = savedUserAnswers
        val result = service.decryptSavedUserAnswers(encryptedSavedUserAnswers, encryptedSavedUserAnswers.vrn)

        result mustBe expectedResult
      }
    }
  }
}
