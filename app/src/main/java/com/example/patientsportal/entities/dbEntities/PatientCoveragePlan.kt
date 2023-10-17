package com.example.patientsportal.entities.dbEntities

data class PatientCoveragePlan(
    var idPCP: Int = 0,
    var patient: Patient = Patient(),
    var coverage: Coverage = Coverage(),
    var planType: PlanCoverage = PlanCoverage(),
    var affiliateNumber: String = ""
)
