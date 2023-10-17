package com.example.patientsportal.entities.dbEntities

data class MedicalTest(
    var idMedicalTest: Int = 0,
    var name: String = "",
    var preparation: String = "",
    var isPreparationVisible: Boolean = false
)
