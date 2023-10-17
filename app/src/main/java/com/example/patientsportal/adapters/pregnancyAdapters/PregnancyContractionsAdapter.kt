package com.example.patientsportal.adapters.pregnancyAdapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPregnancyContractionBinding
import com.example.patientsportal.entities.dbEntities.PregnancyContractions
import java.text.SimpleDateFormat
import java.util.Locale

class PregnancyContractionsAdapter (private val listContractions: ArrayList<PregnancyContractions>, private val context: Context): RecyclerView.Adapter<PregnancyContractionsAdapter.PregnancyContractionsViewHolder>() {


    inner class PregnancyContractionsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPregnancyContractionBinding.bind(view)

        fun bind(pc: PregnancyContractions, context: Context) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("'Día' d MMM yyyy 'a las' HH:mm 'hs'", Locale.getDefault())
            binding.tvContractionsTitle.text = outputFormat.format(inputFormat.parse(pc.date)!!)
            pc.contractions.forEach { addNewRow(context, it.duration, it.interval, it.startAndFinish) }
        }

        private fun addNewRow(context: Context, duration: String, interval: String, startAndFinish: String) {
            // Crea una nueva TableRow
            val tableRow = TableRow(context)

            // Columna 1 - Duración
            val tvDuration = TextView(context)
            with(tvDuration) {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
                text = duration
                setTextColor(resources.getColor(R.color.black, null))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 8, 8, 8)
                tableRow.addView(tvDuration)
            }

            // Columna 2 - Intervalo
            val tvInterval = TextView(context)
            with(tvInterval) {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
                text = interval
                setTextColor(resources.getColor(R.color.black, null))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 8, 8, 8)
                tableRow.addView(tvInterval)
            }

            // Columna 3 - Inicio y Fin
            val tvStartAndFinish = TextView(context)
            with(tvStartAndFinish) {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
                text = startAndFinish
                setTextColor(resources.getColor(R.color.black, null))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 8, 8, 8)
                tableRow.addView(tvStartAndFinish)
            }

            // Agrega la TableRow al TableLayout
            binding.tlMain.addView(tableRow)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnancyContractionsViewHolder {
        return PregnancyContractionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pregnancy_contraction, parent, false))
    }

    override fun getItemCount(): Int {
        return listContractions.size
    }

    override fun onBindViewHolder(holder: PregnancyContractionsViewHolder, position: Int) {
        holder.bind(listContractions[position], context)
    }
}