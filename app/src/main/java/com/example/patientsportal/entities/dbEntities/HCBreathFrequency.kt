package com.example.patientsportal.entities.dbEntities

data class HCBreathFrequency (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var breathFrequency: String = "",
    var loadedBy: String = ""
)