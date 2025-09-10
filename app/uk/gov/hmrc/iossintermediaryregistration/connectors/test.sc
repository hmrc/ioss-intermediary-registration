import play.api.libs.json.Json
import uk.gov.hmrc.iossintermediaryregistration.models.etmp.display.EtmpDisplayRegistration

val a = """{
  "customerIdentification": {
    "idType": "VRN",
    "idValue": "21312312"
  }
  ,
  "tradingNames": [
  {
    "tradingName": "John Lewis"
  }
  ],
  "clientDetails": [
  {
    "clientName": "Eloitt James"
    ,
    "clientIossID": "IM9000005187"
    ,
    "clientExcluded": true
  }
  ],
  "intermediaryDetails": {
    "otherIossIntermediaryRegistrations": [ {
    "issuedBy": "PT",
    "intermediaryNumber": "IN9000005187"
  }
    ]
  }
  ,
  "otherAddress": {
    "issuedBy": "PT",
    "tradingName": "Name 1",
    "addressLine1": "London Street",
    "addressLine2": "London Road",
    "postcode": "BN10 8HG",
    "regionOrState": "PT",
    "townOrCity": "London"
  }
  ,
  "schemeDetails": {
    "commencementDate": "1900-03-14",
    "requestDate": "1900-03-14",
    "registrationDate": "1900-03-14",
    "nonCompliantReturns": "1",
    "nonCompliantPayments": "1",
    "registeredForTaxInEUCountries": true,
    "euRegistrationDetails": [ {
    "issuedBy": "PT",
    "vatRegistered": true,
    "vatNumber": "21312312",
    "taxIdentificationNumber": "21312312",
    "fixedEstablishmentTradingName": "Name 3",
    "fixedEstablishmentAddressLine1": "London Street",
    "fixedEstablishmentAddressLine2": "London Road",
    "townOrCity": "London",
    "regionOrState": "GB",
    "postcode": "BN10 8HG"
  }
    ],
    "previouslyRegisteredfromOSSInEUCountries": true,
    "previousEURegistrationDetails": [ {
    "issuedBy": "PT",
    "registrationNumber": "1234567889",
    "schemeType": "OSS Union",
    "intermediaryNumber": "IN9000005187"
  }
    ],
    "websites": [ {
    "websiteAddress": "www.sdasidjo.com"
  }
    ],
    "contactDetails": {
    "contactNameOrBusinessAddress": "Johnedwards",
    "businessTelephoneNumber": "07564532178",
    "businessEmailAddress": "jdoe@identifier.com",
    "unusableStatus": true
  }
  }
  ,
  "exclusions": [
  {
    "exclusionReason": "-1"
    ,
    "effectiveDate": "1900-03-14"
    ,
    "decisionDate": "1900-03-14"
    ,
    "quarantine": true
  }
  ],
  "bankDetails": {
    "accountName": "John Lewis",
    "bic": "ABCDEF2A",
    "iban": "GB33BUKB20201555555555"
  }
  ,
  "adminUse": {
    "dataOrigin": "Portal",
    "completedOn": "2025-07-08T01:02:03Z",
    "completedBy": "Mr Lewis",
    "dateOfApplication": "2025-08-08",
    "changeDate": "2025-07-08T01:02:03Z"
  }
}"""

Json.parse(a).validate[EtmpDisplayRegistration] match {
  case a => a
}