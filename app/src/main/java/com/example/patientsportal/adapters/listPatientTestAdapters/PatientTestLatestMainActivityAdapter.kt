package com.example.patientsportal.adapters.listPatientTestAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemAppointmentsAheadAndLatestMedicalTestMainActivityBinding
import com.example.patientsportal.entities.dbEntities.PatientTest
import com.example.patientsportal.objects.DateConverter.dateSimplePatientTestConverter

class PatientTestLatestMainActivityAdapter(private val listPatientTest: ArrayList<PatientTest>, private val onClick: (PatientTest) -> Unit): RecyclerView.Adapter<PatientTestLatestMainActivityAdapter.PatientTestLatestMainActivityViewHolder>() {


    inner class PatientTestLatestMainActivityViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemAppointmentsAheadAndLatestMedicalTestMainActivityBinding.bind(view)

        fun bind(patientTest: PatientTest, onClick: (PatientTest) -> Unit) {
            binding.tvDate.text = dateSimplePatientTestConverter(patientTest.date)
            binding.tvSpeciality.text = patientTest.medicalTest.name
            binding.ivAppointment.visibility = View.GONE
            binding.clItem.setOnClickListener { onClick(patientTest) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientTestLatestMainActivityViewHolder {
        return PatientTestLatestMainActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_appointments_ahead_and_latest_medical_test_main_activity, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listPatientTest.size > 3) {
            3
        } else {
            listPatientTest.size
        }
    }

    override fun onBindViewHolder(holder: PatientTestLatestMainActivityViewHolder, position: Int) {
        holder.bind(listPatientTest[position], onClick)
    }
}