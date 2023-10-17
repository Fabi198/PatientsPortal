package com.example.patientsportal.adapters.listRequestedPatientTestAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemsRecyclerBinding
import com.example.patientsportal.entities.dbEntities.RequestedPatientTest

class SuperRequestedPatientTestAdapter (private val listSuperRequestedPatientTest: Map<String, ArrayList<RequestedPatientTest>>, private val areWeLookingForDate: Boolean, private val onClick: (RequestedPatientTest) -> Unit): RecyclerView.Adapter<SuperRequestedPatientTestAdapter.SuperRequestedMedicalTestViewHolder>() {

    inner class SuperRequestedMedicalTestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val binding = ItemsRecyclerBinding.bind(v)

        fun bind(rpt: ArrayList<RequestedPatientTest>, superText: String, areWeLookingForDate: Boolean, onClick: (RequestedPatientTest) -> Unit) {
            val adapter = RequestedPatientTestAdapter(rpt, areWeLookingForDate) { onClick(it) }
            binding.rvItems.adapter = adapter
            binding.tvItemsRecyclerTitle.text = superText
            binding.tvItemsRecyclerTitle.visibility = View.VISIBLE
            binding.rvItems.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperRequestedMedicalTestViewHolder {
        return SuperRequestedMedicalTestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_recycler, parent, false))
    }

    override fun getItemCount(): Int {
        return listSuperRequestedPatientTest.size
    }

    override fun onBindViewHolder(holder: SuperRequestedMedicalTestViewHolder, position: Int) {
        val superText = listSuperRequestedPatientTest.keys.toList()[position]
        val listRequestedPatientTest = listSuperRequestedPatientTest[superText]
        listRequestedPatientTest?.let { holder.bind(it, superText, areWeLookingForDate, onClick) }
    }
}