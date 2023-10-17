package com.example.patientsportal.entities.dbEntities

data class DoctorPlaces(
    var idDoctorPlace: Int = 0,
    var doctorSpeciality: DoctorSpeciality = DoctorSpeciality(),
    var place: Place = Place()
)
