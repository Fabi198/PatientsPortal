package com.example.patientsportal.entities.dbEntities

data class Place(
    var idPlace: Int = 0,
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var openingHours: String = "",
    var multiSelected: Boolean = false
)
