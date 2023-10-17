package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import com.example.patientsportal.entities.dbEntities.PatientCoveragePlan

interface OnCoverageSelected {

    fun onCoverageSelected(pcp: PatientCoveragePlan)
}