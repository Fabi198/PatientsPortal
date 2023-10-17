package com.example.patientsportal.adapters.listPatientTestAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemsRecyclerBinding
import com.example.patientsportal.entities.dbEntities.PatientTest

class SuperPatientTestAdapter(private val listSuperMedicalTest: Map<String, ArrayList<PatientTest>>, private val areWeLookingForDate: Boolean, private val onClick: (PatientTest) -> Unit): RecyclerView.Adapter<SuperPatientTestAdapter.SuperMedicalTestViewHolder>() {

    inner class SuperMedicalTestViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemsRecyclerBinding.bind(v)

        fun bind(patientTests: ArrayList<PatientTest>, superText: String, areWeLookingForDate: Boolean, onClick: (PatientTest) -> Unit) {
            val adapter = PatientTestAdapter(patientTests, areWeLookingForDate) { onClick(it) }
            binding.rvItems.adapter = adapter
            binding.tvItemsRecyclerTitle.text = superText
            binding.tvItemsRecyclerTitle.visibility = View.VISIBLE
            binding.rvItems.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperMedicalTestViewHolder {
        return SuperMedicalTestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_recycler, parent, false))
    }

    override fun getItemCount(): Int {
        return listSuperMedicalTest.size
    }

    override fun onBindViewHolder(holder: SuperMedicalTestViewHolder, position: Int) {
        val superText = listSuperMedicalTest.keys.toList()[position]
        val listMedicalTest = listSuperMedicalTest[superText]
        listMedicalTest?.let { holder.bind(it, superText, areWeLookingForDate, onClick) }
    }
}