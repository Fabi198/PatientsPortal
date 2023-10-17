package com.example.patientsportal.entities.dbEntities

data class HCTemperature (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var temperature: String = "",
    var bodyPart: String = "",
    var loadedBy: String = ""
)