package com.example.patientsportal.entities.dbEntities

data class HCWeightAndHeight (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var weight: String = "",
    var height: String = "",
    var imc: String = "",
    var loadedBy: String = ""
)