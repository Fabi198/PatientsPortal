package com.example.patientsportal.adapters.pregnancyAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPregnantWeekBinding
import com.example.patientsportal.entities.PregnancyWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PregnancyWeekAdapter(private val listPregnancyWeek: ArrayList<PregnancyWeek>, private val context: Context, private val onClick: (pregnancyWeek: PregnancyWeek, cvSelected: String, position: Int) -> Unit): RecyclerView.Adapter<PregnancyWeekAdapter.PregnancyWeekViewHolder>() {


    inner class PregnancyWeekViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPregnantWeekBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind (pw: PregnancyWeek, onClick: (pregnancyWeek: PregnancyWeek, cvSelected: String, position: Int) -> Unit) {
            val calendar = Calendar.getInstance()
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val datePersonalizedFormat = SimpleDateFormat("dd\nMMM\nEEE", Locale.getDefault())
            var pregnancyTestDate = datePersonalizedFormat.format(inputFormat.parse(pw.pregnancyTestDate)!!)
            var motherChangesDate = datePersonalizedFormat.format(inputFormat.parse(pw.motherChangesDate)!!)
            var babyDevelopmentDate = datePersonalizedFormat.format(inputFormat.parse(pw.babyDevelopmentDate)!!)

            // Reemplazar el nombre del día de la semana con la abreviatura
            val weekDayShortNames = arrayOf("", "dom", "lun", "mar", "mié", "jue", "vie", "sáb")
            pregnancyTestDate = pregnancyTestDate.replace(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: "", weekDayShortNames[calendar.get(
                Calendar.DAY_OF_WEEK)])
            motherChangesDate = motherChangesDate.replace(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: "", weekDayShortNames[calendar.get(
                Calendar.DAY_OF_WEEK)])
            babyDevelopmentDate = babyDevelopmentDate.replace(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: "", weekDayShortNames[calendar.get(
                Calendar.DAY_OF_WEEK)])

            // Eliminar puntos adicionales
            pregnancyTestDate = pregnancyTestDate.replace(".", "")
            motherChangesDate = motherChangesDate.replace(".", "")
            babyDevelopmentDate = babyDevelopmentDate.replace(".", "")
            when (pw.numberWeek) {
                in 5..10 -> { binding.cvPregnancyTest.visibility = View.GONE }
                in 13..19 -> { binding.cvPregnancyTest.visibility = View.GONE }
                21 -> { binding.cvPregnancyTest.visibility = View.GONE }
                in 23..24 -> { binding.cvPregnancyTest.visibility = View.GONE }
                in 26..27 -> { binding.cvPregnancyTest.visibility = View.GONE }
                33 -> { binding.cvPregnancyTest.visibility = View.GONE }
                36 -> { binding.cvPregnancyTest.visibility = View.GONE; binding.cvMotherChanges.visibility = View.GONE }
                else -> { binding.cvPregnancyTest.visibility = View.VISIBLE; binding.cvMotherChanges.visibility = View.VISIBLE; binding.cvBabyDevelopment.visibility = View.VISIBLE }
            }
            binding.tvWeek.text = "Semana ${pw.numberWeek}"
            binding.tvWeekDate.text = pregnancyTestDate.replace("\n", " ").substring(0, 6)
            binding.tvPregnancyTestDate.text = pregnancyTestDate
            binding.tvMotherChangesDate.text = motherChangesDate
            binding.tvBabyDevelopmentDate.text = babyDevelopmentDate
            binding.cvPregnancyTest.setOnClickListener { onClick(pw, "cvPregnancyTest", bindingAdapterPosition) }
            binding.cvMotherChanges.setOnClickListener { onClick(pw, "cvMotherChanges", bindingAdapterPosition) }
            binding.cvBabyDevelopment.setOnClickListener { onClick(pw, "cvBabyDevelopment", bindingAdapterPosition) }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnancyWeekViewHolder {
        return PregnancyWeekViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pregnant_week, parent, false))
    }

    override fun getItemCount(): Int {
        return listPregnancyWeek.size
    }

    override fun onBindViewHolder(holder: PregnancyWeekViewHolder, position: Int) {
        holder.bind(listPregnancyWeek[position], onClick)

        // Obtén la posición de la semana actual
        val currentWeekPosition = goToPresentWeek(listPregnancyWeek)

        // Cambia el color de fondo si la posición actual coincide con la semana actual
        if (position == currentWeekPosition) {
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.blueLight, null))
        } else {
            // Restaura el color de fondo predeterminado para otros elementos
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.white, null))
        }
    }

    private fun goToPresentWeek(listPregnancyWeek: ArrayList<PregnancyWeek>): Int {
        var i = 0
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()

        // Establecer la hora, minuto, segundo y milisegundo a 0
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        for (pw in listPregnancyWeek) {
            val testDate = inputFormat.parse(pw.pregnancyTestDate)

            if (testDate != null && testDate > currentDate.time) {
                i = pw.numberWeek - 1
                break
            }
        }
        return i
    }

}