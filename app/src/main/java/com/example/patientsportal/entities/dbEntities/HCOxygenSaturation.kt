package com.example.patientsportal.entities.dbEntities

data class HCOxygenSaturation (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var oxygenSaturation: String = "",
    var hasSupplementaryOxygen: String = "",
    var loadedBy: String = ""
)