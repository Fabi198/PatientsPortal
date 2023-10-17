package com.example.patientsportal.entities.dbEntities

data class HCHeartFrequency (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var heartFrequency: String = "",
    var method: String = "",
    var loadedBy: String = ""
)