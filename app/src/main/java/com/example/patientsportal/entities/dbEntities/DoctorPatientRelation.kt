package com.example.patientsportal.entities.dbEntities

data class DoctorPatientRelation(
    var doctorSpeciality: DoctorSpeciality = DoctorSpeciality(),
    var patient: Patient = Patient()
)
