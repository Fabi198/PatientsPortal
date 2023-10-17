package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import com.example.patientsportal.entities.dbEntities.Appointment

interface OnAppointmentSelected {

    fun onAppointmentSelected(appointment: Appointment)

    fun undoCheckOnOtherPages(numberPage: Int)

    fun onAppointmentDeselected()
}