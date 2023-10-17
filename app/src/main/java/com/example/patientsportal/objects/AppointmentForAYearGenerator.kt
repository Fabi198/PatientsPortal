package com.example.patientsportal.objects

import android.content.Context
import com.example.patientsportal.db.arrays.ArrayDoctorRelations.arrayDoctorPlaces
import com.example.patientsportal.db.DbPatientsPortal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

object AppointmentForAYearGenerator {


    fun generateAYearAppointmentsForEachDoctorPlace(context: Context) {

        val dbPatientsPortal = DbPatientsPortal(context)
        val actualTime = Calendar.getInstance()
        val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

        arrayDoctorPlaces.forEach {
            for (i in 0 until 365) {
                val dates = ArrayList<String>()
                val r = Random()
                val appointmentsForToday = r.nextInt(6)
                val date = actualTime.clone() as Calendar
                date.add(Calendar.DAY_OF_YEAR, i)
                if (date.get(Calendar.DAY_OF_WEEK) in 2..6 && appointmentsForToday > 0) {
                    for (a in 0..appointmentsForToday) {
                        date.set(Calendar.HOUR_OF_DAY, r.nextInt(14) + 7)
                        date.set(Calendar.MINUTE, listOf(0, 15, 30, 45).random())
                        date.set(Calendar.SECOND, 0)
                        dates.add(inputFormat.format(date.time))
                    }
                }

                dates.sortBy { inputFormat.parse(it) }

                dates.forEach { dateResult ->
                    dbPatientsPortal.createAppointment(it.doctorSpeciality.idDoctorSpeciality, it.place.idPlace, dateResult)
                }
            }
        }
    }
}