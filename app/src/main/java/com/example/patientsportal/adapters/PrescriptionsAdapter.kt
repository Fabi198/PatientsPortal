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
import com.example.patientsportal.databinding.ItemPrescriptionBinding
import com.example.patientsportal.entities.dbEntities.Prescription
import com.example.patientsportal.objects.DateConverter.setDifferenceBetweenDates

class PrescriptionsAdapter (private val listPrescriptions: ArrayList<Prescription>, private val context: Context): RecyclerView.Adapter<PrescriptionsAdapter.PrescriptionsViewHolder>() {

    inner class PrescriptionsViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private val binding = ItemPrescriptionBinding.bind(v)

        @SuppressLint("SetTextI18n")
        fun bind(p: Prescription, context: Context) {
            var showed = false

            binding.tvDrugSubstance.text = p.drug.nameDrugSubstance
            binding.tvDrugProduct.text = setBoldPartText(context.getString(R.string.recetado), boldText = p.drug.nameDrugProduct)
            binding.tvCredits.text = context.getString(R.string._0_cr_ditos)
            binding.tvDosage.text = setBoldPartText(context.getString(R.string.dosis), boldText = p.drug.dosage)
            binding.tvExpiredDate.text = if (p.drug.expiredDate == "-") { setBoldPartText(context.getString(R.string.vigencia), boldText = p.drug.expiredDate) } else { setBoldPartText(context.getString(R.string.vigencia), date = setDifferenceBetweenDates(p.drug.expiredDate, context)) }
            binding.tvDoctorName.text = "${p.doctor.name} ${p.doctor.lastName}"
            binding.ivDoctor.setImageResource(p.doctor.intImage)
            binding.cvMiniView.setOnClickListener {
                if (!showed) {
                    binding.btnShowFullView.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                    binding.cvFullView.visibility = View.VISIBLE
                } else {
                    binding.btnShowFullView.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
                    binding.cvFullView.visibility = View.GONE
                }
                showed = !showed
            }
        }

        private fun setBoldPartText(normalText: String, date: SpannableString ?= null, boldText: String ?= null): SpannableStringBuilder {
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(normalText)

            if (date != null) {
                spannableStringBuilder.append(date)
                val boldStart = spannableStringBuilder.length - date.length
                val boldEnd = spannableStringBuilder.length
                spannableStringBuilder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    boldStart,
                    boldEnd,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableStringBuilder.append("-")
            }

            if (boldText != null) {
                val spannableString = SpannableString(boldText)
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                val spannableStringBuilder2 = SpannableStringBuilder()
                spannableStringBuilder2.append(normalText).append(spannableString)
                return spannableStringBuilder2
            }

            return spannableStringBuilder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionsViewHolder {
        return PrescriptionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_prescription, parent, false))
    }

    override fun getItemCount(): Int {
        return listPrescriptions.size
    }

    override fun onBindViewHolder(holder: PrescriptionsViewHolder, position: Int) {
        holder.bind(listPrescriptions[position], context)
    }
}