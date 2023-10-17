package com.example.patientsportal.entities.dbEntities

data class Prescription (
    var idPrescription: Int = 0,
    var patient: Patient = Patient(),
    var doctor: Doctor = Doctor(),
    var drug: Drug = Drug(),
    var currentOrExpired: String = ""
)
