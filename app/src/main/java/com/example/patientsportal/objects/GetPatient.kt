package com.example.patientsportal.objects

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Patient

object GetPatient {

    fun getPatient(fragmentActivity: FragmentActivity, context: Context): Patient {
        lateinit var patient: Patient
        val prefs: SharedPreferences = fragmentActivity.getSharedPreferences("MY PREF", AppCompatActivity.MODE_PRIVATE)
        val dni = prefs.getString("dni", null)
        if (dni != null) {
            val dbPatientPortal = DbPatientsPortal(context)
            patient = dbPatientPortal.readPatientByDocumentNumber(dni)
        }
        return patient
    }
}