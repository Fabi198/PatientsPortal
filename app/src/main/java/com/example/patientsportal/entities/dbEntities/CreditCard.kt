package com.example.patientsportal.entities.dbEntities

data class CreditCard(
    var idCreditCard: Int = 0,
    var patient: Patient = Patient(),
    var cardBrand: String = "",
    var cardOwner: String = "",
    var cardNumber: String = "",
    var cardExpiration: String = "",
    var cardCVV: String = "",
    var cardDocNumber: String = ""
)
