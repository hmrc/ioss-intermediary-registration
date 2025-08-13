package uk.gov.hmrc.iossintermediaryregistration.repositories

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.config.AppConfig
import uk.gov.hmrc.iossintermediaryregistration.models.SavedUserAnswers
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, DefaultPlayMongoRepositorySupport}

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global

class SaveForLaterRepositorySpec
  extends BaseSpec
    with DefaultPlayMongoRepositorySupport[SavedUserAnswers]
    with CleanMongoCollectionSupport {

  private val mockAppConfig: AppConfig = mock[AppConfig]

  private val savedUserAnswers: SavedUserAnswers = arbitrarySavedUserAnswers.arbitrary.sample.value
  private val additionalVrn: Vrn = arbitraryVrn.arbitrary.sample.value

  override protected val repository: SaveForLaterRepository = {
    new SaveForLaterRepository(
      mongoComponent = mongoComponent,
      appConfig = mockAppConfig
    )
  }

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

        val repositoryRecords: Seq[SavedUserAnswers] = findAll().futureValue

        insertResult1 `mustBe` updatedAnswers1
        insertResult2 `mustBe` updatedAnswers2
        repositoryRecords must contain theSameElementsAs Seq(updatedAnswers1, updatedAnswers2)
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

        val repositoryRecords: Seq[SavedUserAnswers] = findAll().futureValue

        insertResult1 `mustBe` updatedAnswers1
        insertResult2 `mustBe` updatedAnswers2
        repositoryRecords must contain only updatedAnswers2
      }
    }

    ".get" - {

      "must return Saved User Answers when they exist for a specific VRN" in {

        val updatedAnswers: SavedUserAnswers = savedUserAnswers
          .copy(lastUpdated = Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS))

        insert(updatedAnswers).futureValue

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

        insert(savedUserAnswers).futureValue

        val result = repository.clear(vrn).futureValue

        result `mustBe` true
      }
    }
  }
}
