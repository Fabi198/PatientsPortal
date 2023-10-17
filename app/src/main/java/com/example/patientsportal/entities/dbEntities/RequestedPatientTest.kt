package com.example.patientsportal.entities.dbEntities

data class RequestedPatientTest (
    var idRequestedPatientTest: Int = 0,
    var patient: Patient = Patient(),
    var speciality: Speciality = Speciality(),
    var date: String = "",
    var urlResult: String = ""
)
