package com.example.patientsportal.entities.dbEntities

data class PregnancyPhoto(
    var idPregnancyPhoto: Int = 0,
    var patientPregnancy: PatientPregnancy = PatientPregnancy(),
    var title: String = "",
    var imagePath: String = "",
    var date: String = ""
)
