package uk.gov.hmrc.iossintermediaryregistration.services

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.iossintermediaryregistration.base.BaseSpec
import uk.gov.hmrc.iossintermediaryregistration.connectors.{GetVatInfoConnector, RegistrationConnector}
import uk.gov.hmrc.iossintermediaryregistration.models.*
import uk.gov.hmrc.iossintermediaryregistration.models.des.VatCustomerInfo
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.display.{EtmpDisplayRegistration, RegistrationWrapper}
import uk.gov.hmrc.iossintermediaryregistration.models.amend.AmendResult.AmendSucceeded
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.amend.AmendRegistrationResponse
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.responses.EtmpEnrolmentResponse
import uk.gov.hmrc.iossintermediaryregistration.models.responses.{EtmpException, NotFound, ServerError}
import uk.gov.hmrc.iossintermediaryregistration.testutils.RegistrationData.etmpRegistrationRequest
import uk.gov.hmrc.iossintermediaryregistration.utils.FutureSyntax.FutureOps

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationServiceSpec extends BaseSpec with BeforeAndAfterEach {

  private val mockRegistrationConnector: RegistrationConnector = mock[RegistrationConnector]
  private val mockGetVatInfoConnector: GetVatInfoConnector = mock[GetVatInfoConnector]
  private val registrationService = new RegistrationService(mockRegistrationConnector, mockGetVatInfoConnector)

  implicit private val hc: HeaderCarrier = new HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(
      mockRegistrationConnector,
      mockGetVatInfoConnector
    )
  }

  "RegistrationService" - {

    ".createRegistration" - {

      "must create registration request and return a successful ETMP enrolment response" in {

        val etmpEnrolmentResponse =
          EtmpEnrolmentResponse(
            processingDateTime = LocalDateTime.now(stubClock),
            formBundleNumber = "123456789",
            vrn = vrn.vrn,
            intRef = "test",
            businessPartner = "test businessPartner"
          )

        when(mockRegistrationConnector.createRegistration(etmpRegistrationRequest)) thenReturn Right(etmpEnrolmentResponse).toFuture

        val app = applicationBuilder
          .build()

        running(app) {

          registrationService.createRegistration(etmpRegistrationRequest).futureValue mustBe Right(etmpEnrolmentResponse)
          verify(mockRegistrationConnector, times(1)).createRegistration(eqTo(etmpRegistrationRequest))
        }
      }
    }

    ".getRegistration" - {

      val vatCustomerInfo: VatCustomerInfo = arbitraryVatCustomerInfo.arbitrary.sample.value
      val etmpDisplayRegistration: EtmpDisplayRegistration = arbitraryEtmpDisplayRegistration.arbitrary.sample.value
      val registrationWrapper: RegistrationWrapper = RegistrationWrapper(
        vatInfo = vatCustomerInfo,
        etmpDisplayRegistration = etmpDisplayRegistration
      )
      "must return a Registration Wrapper payload when both VatCustomerInfo and EtmpDisplayRegistration are successfully retrieved from ETMP" in {

        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Right(vatCustomerInfo).toFuture
        when(mockRegistrationConnector.getRegistration(any())) thenReturn Right(etmpDisplayRegistration).toFuture

        val result = registrationService.getRegistrationWrapper(intermediaryNumber, vrn).futureValue

        result `mustBe` registrationWrapper
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(vrn))(any())
        verify(mockRegistrationConnector, times(1)).getRegistration(eqTo(intermediaryNumber))
      }

      "must throw an ETMPException when both vatCustomerInfo and etmpDisplayRegistration cannot be retrieved from ETMP" in {

        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Left(ServerError).toFuture
        when(mockRegistrationConnector.getRegistration(any())) thenReturn Left(NotFound).toFuture

        val errorMessage = s"There was an error retrieving both vatCustomerInfo and etmpDisplayRegistration from ETMP" +
          s"with errors: ${ServerError.body} and ${NotFound.body}."

        val result = registrationService.getRegistrationWrapper(intermediaryNumber, vrn).failed

        whenReady(result) { exp =>
          exp `mustBe` a[EtmpException]
          exp.getMessage `mustBe` errorMessage
        }
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(vrn))(any())
        verify(mockRegistrationConnector, times(1)).getRegistration(eqTo(intermediaryNumber))
      }

      "must throw an ETMPException when vatCustomerInfo cannot be retrieved from ETMP" in {

        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Left(ServerError).toFuture
        when(mockRegistrationConnector.getRegistration(any())) thenReturn Right(etmpDisplayRegistration).toFuture

        val errorMessage = s"There was an error retrieving vatCustomerInfo from ETMP" +
          s"with errors: ${ServerError.body}."

        val result = registrationService.getRegistrationWrapper(intermediaryNumber, vrn).failed

        whenReady(result) { exp =>
          exp `mustBe` a[EtmpException]
          exp.getMessage `mustBe` errorMessage
        }
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(vrn))(any())
        verify(mockRegistrationConnector, times(1)).getRegistration(eqTo(intermediaryNumber))
      }

      "must throw an ETMPException when etmpDisplayRegistration cannot be retrieved from ETMP" in {

        when(mockGetVatInfoConnector.getVatCustomerDetails(any())(any())) thenReturn Right(vatCustomerInfo).toFuture
        when(mockRegistrationConnector.getRegistration(any())) thenReturn Left(NotFound).toFuture

        val errorMessage = s"There was an error retrieving etmpDisplayRegistration from ETMP" +
          s"with errors: ${NotFound.body}."

        val result = registrationService.getRegistrationWrapper(intermediaryNumber, vrn).failed

        whenReady(result) { exp =>
          exp `mustBe` a[EtmpException]
          exp.getMessage `mustBe` errorMessage
        }
        verify(mockGetVatInfoConnector, times(1)).getVatCustomerDetails(eqTo(vrn))(any())
        verify(mockRegistrationConnector, times(1)).getRegistration(eqTo(intermediaryNumber))
      }
    }
  }

  ".amendRegistration" - {

    "must create registration request and return a successful ETMP enrolment response" in {

      val amendRegistrationResponse = AmendRegistrationResponse(
        processingDateTime = LocalDateTime.now(stubClock),
        formBundleNumber = "123456789",
        vrn = vrn.vrn,
        intermediary = "test",
        businessPartner = "test businessPartner"
      )

      when(mockRegistrationConnector.amendRegistration(etmpAmendRegistrationRequest)) thenReturn Right(amendRegistrationResponse).toFuture

      val app = applicationBuilder
        .build()

      running(app) {

        registrationService.amendRegistration(etmpAmendRegistrationRequest).futureValue mustBe AmendSucceeded
        verify(mockRegistrationConnector, times(1)).amendRegistration(eqTo(etmpAmendRegistrationRequest))
      }
    }
  }
}

