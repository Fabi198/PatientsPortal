package com.example.patientsportal.entities.dbEntities

data class HCBreathlessness (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var breathlessness: String = "",
    var loadedBy: String = ""
)