package com.example.patientsportal.entities.dbEntities

data class PregnancyFeel(
    var idPregnancyFeel: Int = 0,
    var patientPregnancy: PatientPregnancy = PatientPregnancy(),
    var feel: String = "",
    var date: String = ""
)
