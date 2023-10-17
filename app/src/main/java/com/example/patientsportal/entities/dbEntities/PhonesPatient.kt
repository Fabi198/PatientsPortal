package com.example.patientsportal.entities.dbEntities

data class PhonesPatient(
    var patient: Patient = Patient(),
    var phoneHome: String = "",
    var alternativePhoneHome: String = "",
    var particularPhone: String = "",
    var cellphone: String = "",
    var company: String = "",
    var fax: String = "",
    var phoneWork: String = ""
)
