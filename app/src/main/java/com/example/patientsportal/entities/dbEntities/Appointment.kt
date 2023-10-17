package com.example.patientsportal.entities.dbEntities

data class Appointment(
    var idAppointment: Int = 0,
    var patientCoveragePlan: PatientCoveragePlan = PatientCoveragePlan(),
    var patient: Patient = Patient(),
    var doctorSpeciality: DoctorSpeciality = DoctorSpeciality(),
    var place: Place = Place(),
    var date: String = "",
    var showUpPatient: String = "A presentarse",
    var isExpanded: Boolean = false
)
