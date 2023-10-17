package com.example.patientsportal.entities.dbEntities

data class Drug (
    var idDrug: Int = 0,
    var nameDrugSubstance: String = "",
    var expiredDate: String = "",
    var nameDrugProduct: String = "",
    var dosage: String = "",
    var price: Double = 0.0,
    var stockAvailable: Int = 0,
    var maximumUnitsToBuy: Int = 0
)
