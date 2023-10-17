package com.example.patientsportal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemExpiredPrescriptionsBinding
import com.example.patientsportal.entities.dbEntities.PrescriptionExpired

class PrescriptionExpiredAdapter (private var listPrescriptions: ArrayList<PrescriptionExpired>, private val context: Context, private val currentOrExpired: String, private val onClick: (PrescriptionExpired, Boolean, Int) -> Unit): RecyclerView.Adapter<PrescriptionExpiredAdapter.PrescriptionExpiredViewHolder>() {

    fun updateData(newList: ArrayList<PrescriptionExpired>) {
        listPrescriptions = newList
    }


    inner class PrescriptionExpiredViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private val binding = ItemExpiredPrescriptionsBinding.bind(v)

        @SuppressLint("SetTextI18n")
        fun bind(p: PrescriptionExpired, context: Context, currentOrExpired: String, onClick: (PrescriptionExpired, Boolean, Int) -> Unit) {
            binding.cbExpiredPrescription.isChecked = p.isChecked
            binding.tvDrugSubstance.text = p.prescription.drug.nameDrugSubstance
            binding.tvDrugProduct.text = setBoldPartText(context.getString(R.string.recetado), p.prescription.drug.nameDrugProduct)
            binding.tvDoctorName.text = "${p.prescription.doctor.name} ${p.prescription.doctor.lastName}"
            binding.ivDoctor.setImageResource(p.prescription.doctor.intImage)
            binding.tvExpired.text = when (currentOrExpired) {
                context.getString(R.string.current) -> { context.getString(R.string.receta_proxima_a_vencer) }
                context.getString(R.string.expired) -> { context.getString(R.string.receta_vencida) }
                else -> { "" }
            }
            binding.cbExpiredPrescription.setOnClickListener {
                if (binding.cbExpiredPrescription.isChecked) {
                    binding.cbExpiredPrescription.isChecked = true
                    onClick(p, true, bindingAdapterPosition)
                    p.isChecked = true
                } else {
                    binding.cbExpiredPrescription.isChecked = false
                    onClick(p, false, bindingAdapterPosition)
                    p.isChecked = false
                }
            }
        }

        private fun setBoldPartText(normalText: String, boldText: String): SpannableStringBuilder {
            val spannableString = SpannableString(boldText)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(normalText).append(spannableString)
            return spannableStringBuilder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionExpiredViewHolder {
        return PrescriptionExpiredViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_expired_prescriptions, parent, false))
    }

    override fun getItemCount(): Int {
        return listPrescriptions.size
    }

    override fun onBindViewHolder(holder: PrescriptionExpiredViewHolder, position: Int) {
        holder.bind(listPrescriptions[position], context, currentOrExpired, onClick)
    }
}