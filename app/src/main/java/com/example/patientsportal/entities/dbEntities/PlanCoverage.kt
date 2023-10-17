package com.example.patientsportal.entities.dbEntities

data class PlanCoverage(
    var idPlan: Int = 0,
    var coverage: Coverage = Coverage(),
    var planType: String = ""
)
