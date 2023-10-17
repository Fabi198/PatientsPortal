package com.example.patientsportal.entities.dbEntities

data class PatientTest(
    var idPatientTest: Int = 0,
    var patient: Patient = Patient(),
    var medicalTest: MedicalTest = MedicalTest(),
    var date: String = "",
    var urlResult: String = ""
)
