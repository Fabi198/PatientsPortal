package com.example.patientsportal.entities.dbEntities

data class PregnancyContractions(
    var idPregnancyContraction: Int = 0,
    var patientPregnancy: PatientPregnancy = PatientPregnancy(),
    var date: String = "",
    var contractions: ArrayList<PregnancyContraction> = ArrayList()
)
