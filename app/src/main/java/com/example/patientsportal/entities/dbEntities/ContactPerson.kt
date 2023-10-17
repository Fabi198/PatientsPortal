package com.example.patientsportal.entities.dbEntities

data class ContactPerson(
    var patient: Patient = Patient(),
    var names: String = "",
    var lastNames: String = "",
    var relationship: String = "",
    var address: String = "",
    var phone: String = ""
)
