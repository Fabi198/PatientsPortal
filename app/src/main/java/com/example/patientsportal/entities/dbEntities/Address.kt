package com.example.patientsportal.entities.dbEntities

data class Address(
    var patient: Patient = Patient(),
    var street: String = "",
    var number: String = "",
    var floor: String = "",
    var department: String = "",
    var observations: String = "",
    var postalCode: String = "",
    var country: String = "",
    var province: String = "",
    var locality: String = ""
)
