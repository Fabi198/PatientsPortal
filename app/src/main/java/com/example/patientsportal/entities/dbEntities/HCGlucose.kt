package com.example.patientsportal.entities.dbEntities

data class HCGlucose (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var glucose: String = "",
    var eatLastTwoHours: String = "",
    var loadedBy: String = ""
)