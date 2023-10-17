package com.example.patientsportal.entities.dbEntities

data class HCArterialPressure (
    var idHC: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var lowArterialPressure: String = "",
    var highArterialPressure: String = "",
    var bodyPart: String = "",
    var bloodPressureMonitorType: String = "",
    var personWhoTookTheTest: String = "",
    var loadedBy: String = ""
)