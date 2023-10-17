package com.example.patientsportal.entities.dbEntities

data class PatientDoctorsBySpeciality (
    var speciality: Speciality = Speciality(0, ""),
    var doctors: ArrayList<Doctor> = ArrayList()
)
