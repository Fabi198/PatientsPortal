package com.example.patientsportal.adapters.customAlertDialogAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemSpinnerCoverageBinding
import com.example.patientsportal.entities.dbEntities.PlanCoverage

class CustomAlertDialogCoveragesAdapter(private val listCoveragePlans: ArrayList<PlanCoverage>, private val onClick: (PlanCoverage) -> Unit): RecyclerView.Adapter<CustomAlertDialogCoveragesAdapter.SpinnerPlanCoverageViewHolder>() {

    inner class SpinnerPlanCoverageViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemSpinnerCoverageBinding.bind(v)

        fun bind(pC: PlanCoverage, onClick: (PlanCoverage) -> Unit) {
            binding.tvTitlePlanCoverage.text = pC.planType
            binding.clMainItemSpinnerCoveragePlan.setOnClickListener { onClick(pC) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinnerPlanCoverageViewHolder {
        return SpinnerPlanCoverageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_coverage, parent, false))
    }

    override fun getItemCount(): Int {
        return listCoveragePlans.size
    }

    override fun onBindViewHolder(holder: SpinnerPlanCoverageViewHolder, position: Int) {
        holder.bind(listCoveragePlans[position], onClick)
    }


}