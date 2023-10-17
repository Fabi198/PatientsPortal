package com.example.patientsportal.entities.dbEntities

data class AlternativeDrug(
    var idAlternativeDrug: Int = 0,
    var drug: Drug = Drug(),
    var name: String = "",
    var price: Double = 0.0,
    var stockAvailable: Int = 0,
    var maximumUnitsToBuy: Int = 0
)
