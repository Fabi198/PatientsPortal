package com.example.patientsportal.entities.dbEntities

data class PatientPregnancy(
    var idPatientPregnancy: Int = 0,
    var patient: Patient = Patient(),
    var startPregnancyDate: String = "",
    var positivePregnancyDate: String = "",
    var finishPregnancyDate: String = ""
)
