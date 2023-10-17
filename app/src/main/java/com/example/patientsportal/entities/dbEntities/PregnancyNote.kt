package com.example.patientsportal.entities.dbEntities

data class PregnancyNote(
    var idPregnancyNote: Int = 0,
    var patientPregnancy: PatientPregnancy = PatientPregnancy(),
    var title: String = "",
    var note: String = "",
    var date: String = ""
)
