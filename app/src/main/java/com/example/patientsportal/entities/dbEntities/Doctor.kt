package com.example.patientsportal.entities.dbEntities

data class Doctor(
    var idDoctor: Int = 0,
    var name: String = "",
    var lastName: String = "",
    var dni: String = "",
    var gender: String = "",
    var email: String = "",
    var birthday: String = "",
    var codArea: String = "",
    var numCelular: String = "",
    var intImage: Int = 0,
    var isExpanded: Boolean = false
)
