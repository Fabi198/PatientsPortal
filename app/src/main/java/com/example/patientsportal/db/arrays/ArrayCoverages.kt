package com.example.patientsportal.db.arrays

import com.example.patientsportal.entities.dbEntities.Coverage
import com.example.patientsportal.entities.dbEntities.PlanCoverage

object ArrayCoverages {

    val arrayCoverages = arrayListOf(
        Coverage(1, "SWISS MEDICAL S.A"),
        Coverage(2, "OSECAC"),
        Coverage(3, "OSDEPYM"),
        Coverage(4, "PUPPIS MEDICAL")
    )

    val arrayPlanCoverages = arrayListOf(
        PlanCoverage(1, arrayCoverages[0], "ITL62"),
        PlanCoverage(2, arrayCoverages[0], "SMG02"),
        PlanCoverage(3, arrayCoverages[0], "SMG20"),
        PlanCoverage(4, arrayCoverages[0], "SMG50"),
        PlanCoverage(5, arrayCoverages[1], "PLAN UNICO"),
        PlanCoverage(6, arrayCoverages[1], "PLAN AZUL"),
        PlanCoverage(7, arrayCoverages[1], "PLAN M.O."),
        PlanCoverage(8, arrayCoverages[2], "PLAN 800"),
        PlanCoverage(9, arrayCoverages[2], "PLAN 1000"),
        PlanCoverage(10, arrayCoverages[2], "PLAN 2000"),
        PlanCoverage(11, arrayCoverages[2], "PLAN 2500"),
        PlanCoverage(12, arrayCoverages[2], "PLAN 3000"),
        PlanCoverage(13, arrayCoverages[2], "PLAN 4000"),
        PlanCoverage(14, arrayCoverages[2], "PLAN STAFF"),
        PlanCoverage(15, arrayCoverages[2], "PLAN COMBINADO"),
        PlanCoverage(16, arrayCoverages[2], "PLAN PERSONAL"),
        PlanCoverage(17, arrayCoverages[3], "PUP20"),
        PlanCoverage(18, arrayCoverages[3], "PUP200"),
        PlanCoverage(19, arrayCoverages[3], "PUP500"),
        PlanCoverage(20, arrayCoverages[3], "PUP1000")

    )
}