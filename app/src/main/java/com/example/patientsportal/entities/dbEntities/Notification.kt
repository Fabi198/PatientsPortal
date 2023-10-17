package com.example.patientsportal.entities.dbEntities

data class Notification(
    var idNotification: Int = 0,
    var patient: Patient = Patient(),
    var date: String = "",
    var title: String = "",
    var description: String = "",
    var readed: String = ""
)
