package com.example.patientsportal.entities.dbEntities

data class PrescriptionExpired(
    var prescription: Prescription = Prescription(),
    var isChecked: Boolean = false
)
