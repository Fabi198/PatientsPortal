package com.example.patientsportal.adapters.listRequestedPatientTestAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemMedicalTestBinding
import com.example.patientsportal.entities.dbEntities.RequestedPatientTest

class RequestedPatientTestAdapter(private val listRequestedPatientTest: ArrayList<RequestedPatientTest>, private val areWeLookingForDate: Boolean, private val onClick: (RequestedPatientTest) -> Unit): RecyclerView.Adapter<RequestedPatientTestAdapter.RequestedPatientTestViewHolder>() {

    inner class RequestedPatientTestViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemMedicalTestBinding.bind(v)

        fun bind(rpt: RequestedPatientTest, areWeLookingForDate: Boolean, onClick: (RequestedPatientTest) -> Unit) {
            binding.tvItem.text = if (areWeLookingForDate) { rpt.speciality.name } else { rpt.date }
            binding.cvItem.setOnClickListener { onClick(rpt) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestedPatientTestViewHolder {
        return RequestedPatientTestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_medical_test, parent, false))
    }

    override fun getItemCount(): Int {
        return listRequestedPatientTest.size
    }

    override fun onBindViewHolder(holder: RequestedPatientTestViewHolder, position: Int) {
        holder.bind(listRequestedPatientTest[position], areWeLookingForDate, onClick)
    }
}