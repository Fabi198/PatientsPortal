package com.example.patientsportal.objects

import android.content.Context
import com.example.patientsportal.R
import com.example.patientsportal.entities.CustomCardView

object CardLists {

    fun getAppointmentList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_book_new_appointment, context.getString(R.string.reservar_turnos)),
            CustomCardView(R.drawable.card_icon_booked_appointment, context.getString(R.string.mis_turnos_agendados)),
            CustomCardView(R.drawable.card_icon_hystory, context.getString(R.string.historial)),
            CustomCardView(R.drawable.card_icon_videocall, context.getString(R.string.guardia_virtual))
        )
    }

    fun getMedicalTestList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_see_results, context.getString(R.string.ver_resultados)),
            CustomCardView(R.drawable.card_icon_requested_test, context.getString(R.string.estudios_solicitados)),
            CustomCardView(R.drawable.card_icon_preparated_test, context.getString(R.string.preparacion_de_estudios))
        )
    }

    fun getHealthCoverageList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_my_plan, context.getString(R.string.my_plan)),
            CustomCardView(R.drawable.card_icon_my_bills, context.getString(R.string.my_bills)),
            CustomCardView(R.drawable.card_icon_travel_assisntace, context.getString(R.string.travel_assistance))
        )
    }

    fun getMyDoctorsList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_doctors, context.getString(R.string.mis_medicos)),
        )
    }

    fun getCommunityList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_my_pregnancy, context.getString(R.string.my_pregnancy))
        )
    }

    fun getProviderDirectoryList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_doctors, context.getString(R.string.especialties_and_proffessionals)),
        )
    }

    fun getHealthControlList(context: Context): ArrayList<CustomCardView> {
        return arrayListOf(
            CustomCardView(R.drawable.card_icon_height_and_weight, context.getString(R.string.peso_y_altura)),
            CustomCardView(R.drawable.card_icon_temperature, context.getString(R.string.temp)),
            CustomCardView(R.drawable.card_icon_heart_frequency, context.getString(R.string.frecuencia_cardiaca)),
            CustomCardView(R.drawable.card_icon_blood_pressure, context.getString(R.string.presion_arterial)),
            CustomCardView(R.drawable.card_icon_glucemy, context.getString(R.string.glucemia)),
            CustomCardView(R.drawable.card_icon_breath_frequency, context.getString(R.string.frecuencia_respiratoria)),
            CustomCardView(R.drawable.card_icon_oxygen_saturation, context.getString(R.string.saturacion_de_oxigeno)),
            CustomCardView(R.drawable.card_icon_dyspnea, context.getString(R.string.disnea))
        )
    }
}