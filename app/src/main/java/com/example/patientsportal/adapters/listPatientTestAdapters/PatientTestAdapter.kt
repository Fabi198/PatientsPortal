package com.example.patientsportal.adapters.listPatientTestAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemMedicalTestBinding
import com.example.patientsportal.entities.dbEntities.PatientTest

class PatientTestAdapter(private val listMedicalTest: ArrayList<PatientTest>, private val areWeLookingForDate: Boolean, private val onClick: (PatientTest) -> Unit): RecyclerView.Adapter<PatientTestAdapter.MedicalTestViewHolder>() {

    inner class MedicalTestViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemMedicalTestBinding.bind(v)

        fun bind(patientTest: PatientTest, areWeLookingForDate: Boolean, onClick: (PatientTest) -> Unit) {
            binding.tvItem.text = if (areWeLookingForDate) { patientTest.medicalTest.name } else { patientTest.date }
            binding.cvItem.setOnClickListener { onClick(patientTest) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalTestViewHolder {
        return MedicalTestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_medical_test, parent, false))
    }

    override fun getItemCount(): Int {
        return listMedicalTest.size
    }

    override fun onBindViewHolder(holder: MedicalTestViewHolder, position: Int) {
        holder.bind(listMedicalTest[position], areWeLookingForDate, onClick)
    }

}