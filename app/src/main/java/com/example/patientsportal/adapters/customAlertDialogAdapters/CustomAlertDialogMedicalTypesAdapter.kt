package com.example.patientsportal.adapters.customAlertDialogAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemSpinnerCoverageBinding

class CustomAlertDialogMedicalTypesAdapter(private val listMedicalTestType: ArrayList<String>, private val onClick: (String) -> Unit): RecyclerView.Adapter<CustomAlertDialogMedicalTypesAdapter.SpinnerMedicalTestViewHolder>() {

    inner class SpinnerMedicalTestViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemSpinnerCoverageBinding.bind(view)

        fun bind(s: String, onClick: (String) -> Unit) {
            binding.tvTitlePlanCoverage.text = s
            binding.clMainItemSpinnerCoveragePlan.setOnClickListener { onClick(s) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinnerMedicalTestViewHolder {
        return SpinnerMedicalTestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_coverage, parent, false))
    }

    override fun getItemCount(): Int {
        return listMedicalTestType.size
    }

    override fun onBindViewHolder(holder: SpinnerMedicalTestViewHolder, position: Int) {
        holder.bind(listMedicalTestType[position], onClick)
    }
}