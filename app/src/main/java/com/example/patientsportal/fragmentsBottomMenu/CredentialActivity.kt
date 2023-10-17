package com.example.patientsportal.fragmentsBottomMenu

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ActivityCredentialBinding
import com.example.patientsportal.db.arrays.ArrayPatients
import com.example.patientsportal.entities.dbEntities.Patient

@Suppress("DEPRECATION")
class CredentialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCredentialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCredentialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCredential()
        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    @SuppressLint("SetTextI18n")
    private fun setupCredential() {
        with(getPatient()) {
            binding.ivCredential.setImageResource(intImage)
            binding.tvName.text = "$name $lastName $mothersLastName"
            binding.tvIdNumber.text = getString(R.string.id_n, idClinicalHistory)
            binding.tvDni.text = "$documentType: $documentNumber"
        }
    }


    private fun getPatient(): Patient {
        lateinit var patient: Patient
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.my_pref_tag), MODE_PRIVATE)
        val dni = prefs.getString(getString(R.string.dni_tag), null)
        if (dni != null) { ArrayPatients.arrayPatients.forEach { if (dni == it.documentNumber) { patient = it } } }
        return patient
    }
}