package com.example.patientsportal.entities.dbEntities

data class PregnancyContraction(
    var idContraction: Int = 0,
    var idPregnancyContraction: Int = 0,
    var duration: String = "",
    var interval: String = "",
    var startAndFinish: String = ""
)
