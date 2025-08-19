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

package uk.gov.hmrc.iossintermediaryregistration.repositories

import com.typesafe.config.Config
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.iossintermediaryregistration.config.AppConfig
import uk.gov.hmrc.iossintermediaryregistration.crypto.SavedUserAnswersEncryptor
import uk.gov.hmrc.iossintermediaryregistration.models.{EncryptedSavedUserAnswers, SavedUserAnswers}
import uk.gov.hmrc.iossintermediaryregistration.services.crypto.EncryptionService
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, DefaultPlayMongoRepositorySupport}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext.Implicits.global

class SaveForLaterRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[EncryptedSavedUserAnswers]
    with CleanMongoCollectionSupport
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with Generators {

  private val mockAppConfig: AppConfig = mock[AppConfig]
  private val mockConfiguration: Configuration = mock[Configuration]
  private val mockConfig = mock[Config]
  private val mockEncryptionService: EncryptionService = new EncryptionService(mockConfiguration)
  private val savedUserAnswersEncryptor = new SavedUserAnswersEncryptor(mockEncryptionService)
  private val secretKey: String = "BPceW4+iuprrfHI68JZoO019BL78fSQzDDXMLxN+Dhc="

  private val savedUserAnswers: SavedUserAnswers = arbitrarySavedUserAnswers.arbitrary.sample.value
  private val additionalVrn: Vrn = arbitraryVrn.arbitrary.sample.value

  private val instant = Instant.now
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  override protected val repository: SaveForLaterRepository = {
    new SaveForLaterRepository(
      mongoComponent = mongoComponent,
      appConfig = mockAppConfig,
      encryptor = savedUserAnswersEncryptor
    )
  }

  when(mockConfiguration.underlying) thenReturn mockConfig
  when(mockConfig.getString(any())) thenReturn secretKey
  when(mockAppConfig.encryptionKey) thenReturn secretKey

  "SaveForLaterRepository" - {

    ".set" - {

      "must insert saved user answers for multiple VRNs" in {

        val updatedAnswers1: SavedUserAnswers = savedUserAnswers
          .copy(lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS))

        val updatedAnswers2: SavedUserAnswers = savedUserAnswers
          .copy(
            vrn = additionalVrn,
            lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
          )

        val insertResult1: SavedUserAnswers = repository.set(updatedAnswers1).futureValue
        val insertResult2: SavedUserAnswers = repository.set(updatedAnswers2).futureValue

        val encryptedRepositoryRecords: Seq[EncryptedSavedUserAnswers] = findAll().futureValue

        val decryptedRepositoryRecords = encryptedRepositoryRecords.map { encryptedAnswers =>
          savedUserAnswersEncryptor.decryptSavedUserAnswers(encryptedAnswers, encryptedAnswers.vrn)
        }

        insertResult1 `mustBe` updatedAnswers1
        insertResult2 `mustBe` updatedAnswers2
        decryptedRepositoryRecords must contain theSameElementsAs Seq(updatedAnswers1, updatedAnswers2)
      }

      "must replace saved user answers for same VRN" in {

        val updatedAnswers1: SavedUserAnswers = savedUserAnswers
          .copy(lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS))

        val updatedAnswers2: SavedUserAnswers = updatedAnswers1
          .copy(
            data = JsObject(Seq("savedUserAnswers" -> Json.toJson("updatedSavedUserAnswers"))),
            lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
          )

        val insertResult1: SavedUserAnswers = repository.set(updatedAnswers1).futureValue
        val insertResult2: SavedUserAnswers = repository.set(updatedAnswers2).futureValue

        val encryptedRepositoryRecords: Seq[EncryptedSavedUserAnswers] = findAll().futureValue

        val decryptedRepositoryRecords = encryptedRepositoryRecords.map { encryptedAnswers =>
          savedUserAnswersEncryptor.decryptSavedUserAnswers(encryptedAnswers, encryptedAnswers.vrn)
        }

        insertResult1 `mustBe` updatedAnswers1
        insertResult2 `mustBe` updatedAnswers2
        decryptedRepositoryRecords must contain only updatedAnswers2
      }
    }

    ".get" - {

      "must return Saved User Answers when they exist for a specific VRN" in {

        val updatedAnswers: SavedUserAnswers = savedUserAnswers
          .copy(lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS))

        insert(savedUserAnswersEncryptor.encryptSavedUserAnswers(updatedAnswers, updatedAnswers.vrn)).futureValue

        val result = repository.get(updatedAnswers.vrn).futureValue

        result `mustBe` Some(updatedAnswers)
      }

      "must return None when Saved User Answers do not exist for a specific VRN" in {

        val result = repository.get(savedUserAnswers.vrn).futureValue

        result must not be defined
      }
    }

    ".clear" - {

      "must return true when Saved Answers record for specific VRN is deleted" in {

        insert(savedUserAnswersEncryptor.encryptSavedUserAnswers(savedUserAnswers, savedUserAnswers.vrn)).futureValue

        val result = repository.clear(additionalVrn).futureValue

        result `mustBe` true
      }
    }
  }
}
