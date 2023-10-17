package com.example.patientsportal.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemHealthControlsBinding
import com.example.patientsportal.entities.dbEntities.HCArterialPressure
import com.example.patientsportal.entities.dbEntities.HCBreathFrequency
import com.example.patientsportal.entities.dbEntities.HCBreathlessness
import com.example.patientsportal.entities.dbEntities.HCGlucose
import com.example.patientsportal.entities.dbEntities.HCHeartFrequency
import com.example.patientsportal.entities.dbEntities.HCOxygenSaturation
import com.example.patientsportal.entities.dbEntities.HCTemperature
import com.example.patientsportal.entities.dbEntities.HCWeightAndHeight

class HealthControlAdapter(private val listHealthControls: ArrayList<out Any>, private val title: String): RecyclerView.Adapter<HealthControlAdapter.HealthControlViewHolder>() {



    inner class HealthControlViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemHealthControlsBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(hc: Any, title: String) {
            when (title) {
                "WeightAndHeight" -> {
                    if (hc is HCWeightAndHeight) {
                        binding.headerFiveColumns.visibility = View.VISIBLE
                        binding.fiveColumnFirst.text = hc.date
                        binding.fiveColumnSecond.text = hc.weight
                        binding.fiveColumnThird.text = hc.height
                        binding.fiveColumnFourth.text = hc.imc
                        binding.fiveColumnFifth.text = hc.loadedBy
                    }
                }
                "Temperature" -> {
                    if (hc is HCTemperature) {
                        binding.headerThreeColumn.visibility = View.VISIBLE
                        binding.threeColumnFirst.text = hc.date
                        binding.threeColumnSecond.text = hc.temperature
                        binding.threeColumnThird.text = hc.loadedBy
                    }
                }
                "HeartFrequency" -> {
                    if (hc is HCHeartFrequency) {
                        binding.headerThreeColumn.visibility = View.VISIBLE
                        binding.threeColumnFirst.text = hc.date
                        binding.threeColumnSecond.text = hc.heartFrequency
                        binding.threeColumnThird.text = hc.loadedBy
                    }
                }
                "ArterialPressure" -> {
                    if (hc is HCArterialPressure) {
                        binding.headerThreeColumn.visibility = View.VISIBLE
                        binding.threeColumnFirst.text = hc.date
                        binding.threeColumnSecond.text = "${hc.lowArterialPressure}/${hc.highArterialPressure}"
                        binding.threeColumnThird.text = hc.loadedBy
                    }
                }
                "Glucose" -> {
                    if (hc is HCGlucose) {
                        binding.headerFourColumns.visibility = View.VISIBLE
                        binding.fourColumnFirst.text = hc.date
                        binding.fourColumnSecond.text = hc.glucose
                        binding.fourColumnThird.text = hc.eatLastTwoHours
                        binding.fourColumnFourth.text = hc.loadedBy
                    }
                }
                "BreathFrequency" -> {
                    if (hc is HCBreathFrequency) {
                        binding.headerThreeColumn.visibility = View.VISIBLE
                        binding.threeColumnFirst.text = hc.date
                        binding.threeColumnSecond.text = hc.breathFrequency
                        binding.threeColumnThird.text = hc.loadedBy
                    }
                }
                "OxygenSaturation" -> {
                    if (hc is HCOxygenSaturation) {
                        binding.headerFourColumns.visibility = View.VISIBLE
                        binding.fourColumnFirst.text = hc.date
                        binding.fourColumnSecond.text = hc.oxygenSaturation
                        binding.fourColumnThird.text = hc.hasSupplementaryOxygen
                        binding.fourColumnFourth.text = hc.loadedBy
                    }
                }
                "Breathlessness" -> {
                    if (hc is HCBreathlessness) {
                        binding.headerThreeColumn.visibility = View.VISIBLE
                        binding.threeColumnFirst.text = hc.date
                        binding.threeColumnSecond.text = hc.breathlessness
                        binding.threeColumnThird.text = hc.loadedBy
                    }
                }
                else -> {}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthControlViewHolder {
        return HealthControlViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_health_controls, parent, false))
    }

    override fun getItemCount(): Int {
        return listHealthControls.size
    }

    override fun onBindViewHolder(holder: HealthControlViewHolder, position: Int) {
        holder.bind(listHealthControls[position], title)
    }
}