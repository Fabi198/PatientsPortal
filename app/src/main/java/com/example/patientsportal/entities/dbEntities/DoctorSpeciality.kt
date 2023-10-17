package com.example.patientsportal.entities.dbEntities

data class DoctorSpeciality(
    var idDoctorSpeciality: Int = 0,
    var doctor: Doctor = Doctor(),
    var speciality: Speciality = Speciality()
)
