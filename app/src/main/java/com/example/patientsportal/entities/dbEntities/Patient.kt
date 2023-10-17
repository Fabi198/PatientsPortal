package com.example.patientsportal.entities.dbEntities

data class Patient(
    var idPatient: Int = 0,
    var name: String = "",
    var lastName: String = "",
    var mothersLastName: String = "",
    var biologicalSex: String = "",
    var gender: String = "",
    var email: String = "",
    var alternativeEmail: String = "",
    var password: String = "",
    var birthday: String = "",
    var documentType: String = "",
    var documentNumber: String = "",
    var idClinicalHistory: String = "",
    var observations: String = "",
    var intImage: Int = 0,
    var uriImage: String = "",
    var language: String = "",
    var countryBirth: String = "",
    var educationLevelReached: String = "",
    var homeType: String = "",
    var whoLiveWith: String = "",
    var religion: String = "",
    var disability: String = ""
)
