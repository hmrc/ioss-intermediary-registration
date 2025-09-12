package generators

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.domain.Vrn
import uk.gov.hmrc.iossintermediaryregistration.models.*
import uk.gov.hmrc.iossintermediaryregistration.models.des.VatCustomerInfo
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.*
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.display.{EtmpDisplayEuRegistrationDetails, EtmpDisplayRegistration, EtmpDisplaySchemeDetails, RegistrationWrapper}
import uk.gov.hmrc.iossintermediaryregistration.models.requests.SaveForLaterRequest
import uk.gov.hmrc.iossintermediaryregistration.models.responses.SaveForLaterResponse

import java.time.{Instant, LocalDate, LocalDateTime}

trait Generators {

  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] = {
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- Gen.option(arbitrary[String])
        townOrCity <- arbitrary[String]
        county <- Gen.option(arbitrary[String])
        postCode <- arbitrary[String]
      } yield UkAddress(line1, line2, townOrCity, county, postCode)
    }
  }

  implicit val arbitraryAddress: Arbitrary[Address] = {
    Arbitrary {
      Gen.oneOf(
        arbitrary[UkAddress],
        arbitrary[InternationalAddress],
        arbitrary[DesAddress]
      )
    }
  }

  implicit lazy val arbitraryInternationalAddress: Arbitrary[InternationalAddress] = {
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- Gen.option(arbitrary[String])
        townOrCity <- arbitrary[String]
        stateOrRegion <- Gen.option(arbitrary[String])
        postCode <- Gen.option(arbitrary[String])
        country <- arbitrary[Country]
      } yield InternationalAddress(line1, line2, townOrCity, stateOrRegion, postCode, country)
    }
  }

  implicit lazy val arbitraryDesAddress: Arbitrary[DesAddress] = {
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- Gen.option(arbitrary[String])
        line3 <- Gen.option(arbitrary[String])
        line4 <- Gen.option(arbitrary[String])
        line5 <- Gen.option(arbitrary[String])
        postCode <- Gen.option(arbitrary[String])
        countryCode <- Gen.listOfN(2, Gen.alphaChar).map(_.mkString)
      } yield DesAddress(line1, line2, line3, line4, line5, postCode, countryCode)
    }
  }

  implicit lazy val arbitraryCountry: Arbitrary[Country] = {
    Arbitrary {
      for {
        char1 <- Gen.alphaUpperChar
        char2 <- Gen.alphaUpperChar
        name <- arbitrary[String]
      } yield Country(s"$char1$char2", name)
    }
  }

  implicit lazy val arbitraryVatNumberTraderId: Arbitrary[VatNumberTraderId] =
    Arbitrary {
      for {
        vatNumber <- Gen.alphaNumStr
      } yield VatNumberTraderId(vatNumber)
    }

  implicit lazy val arbitraryTaxRefTraderID: Arbitrary[TaxRefTraderID] =
    Arbitrary {
      for {
        taxReferenceNumber <- Gen.alphaNumStr
      } yield TaxRefTraderID(taxReferenceNumber)
    }

  implicit lazy val arbitraryVrn: Arbitrary[Vrn] = {
    Arbitrary {
      for {
        chars <- Gen.listOfN(9, Gen.numChar)
      } yield Vrn(chars.mkString(""))
    }
  }

  implicit val arbitraryVatCustomerInfo: Arbitrary[VatCustomerInfo] = {
    Arbitrary {
      for {
        registrationDate <- arbitrary[LocalDate]
        organisationName <- arbitrary[String]
        individualName <- arbitrary[String]
        singleMarketIndicator <- arbitrary[Boolean]
        deregistrationDecisionDate <- arbitrary[LocalDate]
      }
      yield
        VatCustomerInfo(
          desAddress = arbitraryDesAddress.arbitrary.sample.get,
          registrationDate = Some(registrationDate),
          organisationName = Some(organisationName),
          individualName = Some(individualName),
          singleMarketIndicator = singleMarketIndicator,
          deregistrationDecisionDate = Some(deregistrationDecisionDate)
        )
    }
  }

  implicit lazy val arbitraryEtmpTradingName: Arbitrary[EtmpTradingName] =
    Arbitrary {
      for {
        tradingName <- Gen.alphaStr
      } yield EtmpTradingName(tradingName)
    }

  implicit lazy val arbitraryEtmpCustomerIdentification: Arbitrary[EtmpCustomerIdentification] =
    Arbitrary {
      for {
        vrn <- arbitraryVrn.arbitrary
        etmpIdType <- Gen.oneOf(EtmpIdType.values)
      } yield EtmpCustomerIdentification(etmpIdType, vrn.vrn)
    }

  implicit lazy val arbitraryEtmpAdministration: Arbitrary[EtmpAdministration] =
    Arbitrary {
      for {
        messageType <- Gen.oneOf(EtmpMessageType.values)
      } yield EtmpAdministration(messageType, "IOSS")
    }

  implicit lazy val arbitrarySchemeType: Arbitrary[SchemeType] =
    Arbitrary {
      Gen.oneOf(SchemeType.values)
    }

  implicit lazy val arbitraryWebsite: Arbitrary[EtmpWebsite] =
    Arbitrary {
      for {
        websiteAddress <- Gen.alphaStr
      } yield EtmpWebsite(websiteAddress)
    }

  implicit lazy val arbitraryEtmpOtherIossIntermediaryRegistrations: Arbitrary[EtmpOtherIossIntermediaryRegistrations] =
    Arbitrary {
      for {
        countryCode <- Gen.listOfN(2, Gen.alphaChar).map(_.mkString)
        intermediaryNumber <- genIntermediaryNumber
      } yield EtmpOtherIossIntermediaryRegistrations(countryCode, intermediaryNumber)
    }

  implicit lazy val arbitraryEtmpIntermediaryDetails: Arbitrary[EtmpIntermediaryDetails] =
    Arbitrary {
      for {
        amountOfOtherRegistrations <- Gen.chooseNum(1, 5)
        otherRegistrationDetails <- Gen.listOfN(amountOfOtherRegistrations, arbitraryEtmpOtherIossIntermediaryRegistrations.arbitrary)
      } yield EtmpIntermediaryDetails(otherRegistrationDetails)
    }

  implicit lazy val arbitraryEtmpOtherAddress: Arbitrary[EtmpOtherAddress] =
    Arbitrary {
      for {
        issuedBy <- Gen.listOfN(2, Gen.alphaChar).map(_.mkString)
        tradingName <- Gen.listOfN(20, Gen.alphaChar).map(_.mkString)
        addressLine1 <- Gen.listOfN(35, Gen.alphaChar).map(_.mkString)
        addressLine2 <- Gen.listOfN(35, Gen.alphaChar).map(_.mkString)
        townOrCity <- Gen.listOfN(35, Gen.alphaChar).map(_.mkString)
        regionOrState <- Gen.listOfN(35, Gen.alphaChar).map(_.mkString)
        postcode <- Gen.listOfN(35, Gen.alphaChar).map(_.mkString)
      } yield EtmpOtherAddress(
        issuedBy,
        Some(tradingName),
        addressLine1,
        Some(addressLine2),
        townOrCity,
        Some(regionOrState),
        postcode
      )
    }

  implicit lazy val arbitraryBic: Arbitrary[Bic] = {
    val asciiCodeForA = 65
    val asciiCodeForN = 78
    val asciiCodeForP = 80
    val asciiCodeForZ = 90

    Arbitrary {
      for {
        firstChars <- Gen.listOfN(6, Gen.alphaUpperChar).map(_.mkString)
        char7 <- Gen.oneOf(Gen.alphaUpperChar, Gen.choose(2, 9))
        char8 <- Gen.oneOf(
          Gen.choose(asciiCodeForA, asciiCodeForN).map(_.toChar),
          Gen.choose(asciiCodeForP, asciiCodeForZ).map(_.toChar),
          Gen.choose(0, 9)
        )
        lastChars <- Gen.option(Gen.listOfN(3, Gen.oneOf(Gen.alphaUpperChar, Gen.numChar)).map(_.mkString))
      } yield Bic(s"$firstChars$char7$char8${lastChars.getOrElse("")}").get
    }
  }

  implicit lazy val arbitraryIban: Arbitrary[Iban] = {
    Arbitrary {
      Gen.oneOf(
        "GB94BARC10201530093459",
        "GB33BUKB20201555555555",
        "DE29100100100987654321",
        "GB24BKEN10000031510604",
        "GB27BOFI90212729823529",
        "GB17BOFS80055100813796",
        "GB92BARC20005275849855",
        "GB66CITI18500812098709",
        "GB15CLYD82663220400952",
        "GB26MIDL40051512345674",
        "GB76LOYD30949301273801",
        "GB25NWBK60080600724890",
        "GB60NAIA07011610909132",
        "GB29RBOS83040210126939",
        "GB79ABBY09012603367219",
        "GB21SCBL60910417068859",
        "GB42CPBK08005470328725"
      ).map(v => Iban(v).toOption.get)
    }
  }

  implicit lazy val arbitrarySavedUserAnswers: Arbitrary[SavedUserAnswers] = {
    Arbitrary {
      for {
        vrn <- arbitraryVrn.arbitrary
        data = JsObject(Seq("savedUserAnswers" -> Json.toJson("userAnswers")))
        now = Instant.now
      } yield {
        SavedUserAnswers(vrn = vrn, data = data, lastUpdated = now)
      }
    }
  }

  implicit lazy val arbitrarySaveForLaterRequest: Arbitrary[SaveForLaterRequest] = {
    Arbitrary {
      for {
        savedUserAnswers <- arbitrarySavedUserAnswers.arbitrary
      } yield {
        SaveForLaterRequest(
          vrn = savedUserAnswers.vrn,
          data = savedUserAnswers.data.as[JsObject]
        )
      }
    }
  }

  implicit lazy val arbitrarySaveForLaterResponse: Arbitrary[SaveForLaterResponse] = {
    Arbitrary {
      for {
        savedUserAnswers <- arbitrarySavedUserAnswers.arbitrary
        vatInfo <- arbitraryVatCustomerInfo.arbitrary
      } yield {
        SaveForLaterResponse(
          vrn = savedUserAnswers.vrn,
          data = savedUserAnswers.data,
          vatInfo = vatInfo,
          lastUpdated = savedUserAnswers.lastUpdated
        )
      }
    }
  }

  implicit lazy val genIntermediaryNumber: Gen[String] = {
    for {
      intermediaryNumber <- Gen.listOfN(12, Gen.alphaChar).map(_.mkString)
    } yield intermediaryNumber
  }

  implicit lazy val genVatNumber: Gen[String] = {
    for {
      vatNumber <- Gen.alphaNumStr
    } yield vatNumber
  }

  implicit lazy val genTaxReference: Gen[String] = {
    for {
      taxReferenceNumber <- Gen.alphaNumStr
    } yield taxReferenceNumber
  }

  implicit lazy val arbitraryEtmpAdminUse: Arbitrary[EtmpAdminUse] = {
    Arbitrary {
      for {
        changeDate <- Gen.option(arbitrary[LocalDateTime])
      } yield EtmpAdminUse(changeDate = changeDate)
    }
  }

  implicit lazy val arbitraryEtmpClientDetails: Arbitrary[EtmpClientDetails] = {
    Arbitrary {
      for {
        clientName <- Gen.alphaStr
        clientIossID <- Gen.alphaNumStr
        clientExcluded <- arbitrary[Boolean]
      } yield {
        EtmpClientDetails(
          clientName = clientName,
          clientIossID = clientIossID,
          clientExcluded = clientExcluded
        )
      }
    }
  }

  implicit lazy val arbitraryOtherIossIntermediaryRegistrations: Arbitrary[EtmpOtherIossIntermediaryRegistrations] = {
    Arbitrary {
      for {
        issuedBy <- arbitraryCountry.arbitrary.map(_.code)
        intermediaryNumber <- genIntermediaryNumber
      } yield {
        EtmpOtherIossIntermediaryRegistrations(
          issuedBy = issuedBy,
          intermediaryNumber = intermediaryNumber
        )
      }
    }
  }

  implicit lazy val arbitraryIntermediaryDetails: Arbitrary[EtmpIntermediaryDetails] = {
    Arbitrary {
      for {
        otherIossIntermediaryRegistrations <- Gen.listOfN(2, arbitraryOtherIossIntermediaryRegistrations.arbitrary)
      } yield {
        EtmpIntermediaryDetails(
          otherIossIntermediaryRegistrations = otherIossIntermediaryRegistrations
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpExclusion: Arbitrary[EtmpExclusion] = {
    Arbitrary {
      for {
        exclusionReason <- Gen.oneOf(EtmpExclusionReason.values)
        effectiveDate <- arbitrary[LocalDate]
        decisionDate <- arbitrary[LocalDate]
        quarantine <- arbitrary[Boolean]
      } yield {
        EtmpExclusion(
          exclusionReason = exclusionReason,
          effectiveDate = effectiveDate,
          decisionDate = decisionDate,
          quarantine = quarantine
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpDisplayEuRegistrationDetails: Arbitrary[EtmpDisplayEuRegistrationDetails] = {
    Arbitrary {
      for {
        issuedBy <- arbitraryCountry.arbitrary.map(_.code)
        vatNumber <- genVatNumber
        taxIdentificationNumber <- genTaxReference
        fixedEstablishmentTradingName <- arbitraryEtmpTradingName.arbitrary.map(_.tradingName)
        fixedEstablishmentAddressLine1 <- Gen.alphaStr
        fixedEstablishmentAddressLine2 <- Gen.alphaStr
        townOrCity <- Gen.alphaStr
        regionOrState <- Gen.alphaStr
        postcode <- Gen.alphaStr
      } yield {
        EtmpDisplayEuRegistrationDetails(
          issuedBy = issuedBy,
          vatNumber = Some(vatNumber),
          taxIdentificationNumber = Some(taxIdentificationNumber),
          fixedEstablishmentTradingName = fixedEstablishmentTradingName,
          fixedEstablishmentAddressLine1 = fixedEstablishmentAddressLine1,
          fixedEstablishmentAddressLine2 = Some(fixedEstablishmentAddressLine2),
          townOrCity = townOrCity,
          regionOrState = Some(regionOrState),
          postcode = Some(postcode)
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpPreviousEuRegistrationDetails: Arbitrary[EtmpPreviousEuRegistrationDetails] = {
    Arbitrary {
      for {
        issuedBy <- arbitraryCountry.arbitrary.map(_.code)
        registrationNumber <- arbitrary[String]
        schemeType <- Gen.oneOf(SchemeType.values)
        intermediaryNumber <- genIntermediaryNumber
      } yield {
        EtmpPreviousEuRegistrationDetails(
          issuedBy = issuedBy,
          registrationNumber = registrationNumber,
          schemeType = schemeType,
          intermediaryNumber = Some(intermediaryNumber)
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpDisplaySchemeDetails: Arbitrary[EtmpDisplaySchemeDetails] = {
    Arbitrary {
      for {
        commencementDate <- arbitrary[LocalDate].map(_.toString)
        euRegistrationDetails <- Gen.listOfN(3, arbitraryEtmpDisplayEuRegistrationDetails.arbitrary)
        contactName <- Gen.alphaStr
        businessTelephoneNumber <- Gen.alphaNumStr
        businessEmailId <- Gen.alphaStr
        unusableStatus <- arbitrary[Boolean]
        nonCompliant <- Gen.oneOf("1", "2")
      } yield {
        EtmpDisplaySchemeDetails(
          commencementDate = commencementDate,
          euRegistrationDetails = euRegistrationDetails,
          contactName = contactName,
          businessTelephoneNumber = businessTelephoneNumber,
          businessEmailId = businessEmailId,
          unusableStatus = unusableStatus,
          nonCompliantReturns = Some(nonCompliant),
          nonCompliantPayments = Some(nonCompliant)
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpBankDetails: Arbitrary[EtmpBankDetails] = {
    Arbitrary {
      for {
        accountName <- arbitrary[String]
        bic <- arbitraryBic.arbitrary
        iban <- arbitraryIban.arbitrary
      } yield {
        EtmpBankDetails(
          accountName = accountName,
          bic = Some(bic),
          iban = iban
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpDisplayRegistration: Arbitrary[EtmpDisplayRegistration] = {
    Arbitrary {
      for {
        customerIdentification <- arbitraryEtmpCustomerIdentification.arbitrary
        tradingNames <- Gen.listOfN(3, arbitraryEtmpTradingName.arbitrary)
        clientDetails <- Gen.listOfN(3, arbitraryEtmpClientDetails.arbitrary)
        intermediaryDetails <- arbitraryIntermediaryDetails.arbitrary
        otherAddress <- arbitraryEtmpOtherAddress.arbitrary
        schemeDetails <- arbitraryEtmpDisplaySchemeDetails.arbitrary
        exclusions <- Gen.listOfN(1, arbitraryEtmpExclusion.arbitrary)
        bankDetails <- arbitraryEtmpBankDetails.arbitrary
        adminUse <- arbitraryEtmpAdminUse.arbitrary
      } yield {
        EtmpDisplayRegistration(
          customerIdentification = customerIdentification,
          tradingNames = tradingNames,
          clientDetails = clientDetails,
          intermediaryDetails = intermediaryDetails,
          otherAddress = otherAddress,
          schemeDetails = schemeDetails,
          exclusions = exclusions,
          bankDetails = bankDetails,
          adminUse = adminUse
        )
      }
    }
  }
  
  implicit lazy val arbitraryRegistrationWrapper: Arbitrary[RegistrationWrapper] = {
    Arbitrary {
      for {
        vatInfo <- arbitraryVatCustomerInfo.arbitrary
        etmpDisplayRegistration <- arbitraryEtmpDisplayRegistration.arbitrary
      } yield {
        RegistrationWrapper(
          vatInfo = vatInfo,
          etmpDisplayRegistration = etmpDisplayRegistration
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpEuRegistrationDetails: Arbitrary[EtmpEuRegistrationDetails] = {
    Arbitrary {
      for {
        countryOfRegistration <- arbitraryCountry.arbitrary.map(_.code)
        traderId <- arbitraryVatNumberTraderId.arbitrary
        tradingName <- arbitraryEtmpTradingName.arbitrary.map(_.tradingName)
        fixedEstablishmentAddressLine1 <- Gen.alphaStr
        fixedEstablishmentAddressLine2 <- Gen.alphaStr
        townOrCity <- Gen.alphaStr
        regionOrState <- Gen.alphaStr
        postcode <- Gen.alphaStr
      } yield {
        EtmpEuRegistrationDetails(
          countryOfRegistration = countryOfRegistration,
          traderId = traderId,
          tradingName = tradingName,
          fixedEstablishmentAddressLine1 = fixedEstablishmentAddressLine1,
          fixedEstablishmentAddressLine2 = Some(fixedEstablishmentAddressLine2),
          townOrCity = townOrCity,
          regionOrState = Some(regionOrState),
          postcode = Some(postcode)
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpWebsite: Arbitrary[EtmpWebsite] = {
    Arbitrary {
      for {
        websiteAddress <- Gen.alphaStr
      } yield EtmpWebsite(websiteAddress)
    }
  }

  implicit lazy val arbitraryEtmpSchemeDetails: Arbitrary[EtmpSchemeDetails] = {
    Arbitrary {
      for {
        commencementDate <- arbitrary[LocalDate].map(_.toString)
        euRegistrationDetails <- Gen.listOfN(3, arbitraryEtmpEuRegistrationDetails.arbitrary)
        previousEURegistrationDetails <- Gen.listOfN(3, arbitraryEtmpPreviousEuRegistrationDetails.arbitrary)
        websites <- Gen.listOfN(3, arbitraryEtmpWebsite.arbitrary)
        contactName <- Gen.alphaStr
        businessTelephoneNumber <- Gen.alphaNumStr
        businessEmailId <- Gen.alphaStr
        nonCompliant <- Gen.oneOf("1", "2")
      } yield {
        EtmpSchemeDetails(
          commencementDate = commencementDate,
          euRegistrationDetails = euRegistrationDetails,
          previousEURegistrationDetails = previousEURegistrationDetails,
          websites = Some(websites),
          contactName = contactName,
          businessTelephoneNumber = businessTelephoneNumber,
          businessEmailId = businessEmailId,
          nonCompliantReturns = Some(nonCompliant),
          nonCompliantPayments = Some(nonCompliant)
        )
      }
    }
  }

  implicit lazy val arbitraryEtmpRegistrationRequest: Arbitrary[EtmpRegistrationRequest] = {
    Arbitrary {
      for {
        administration <- arbitraryEtmpAdministration.arbitrary
        customerIdentification <- arbitraryEtmpCustomerIdentification.arbitrary
        tradingNames <- Gen.listOfN(3, arbitraryEtmpTradingName.arbitrary)
        intermediaryDetails <- arbitraryIntermediaryDetails.arbitrary
        otherAddress <- arbitraryEtmpOtherAddress.arbitrary
        schemeDetails <- arbitraryEtmpSchemeDetails.arbitrary
        bankDetails <- arbitraryEtmpBankDetails.arbitrary
      } yield {
        EtmpRegistrationRequest(
          administration = administration,
          customerIdentification = customerIdentification,
          tradingNames = tradingNames,
          intermediaryDetails = Some(intermediaryDetails),
          otherAddress = Some(otherAddress),
          schemeDetails = schemeDetails,
          bankDetails = bankDetails
        )
      }
    }
  }
}

