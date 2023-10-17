package com.example.patientsportal.adapters.pregnancyAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPregnancyNotesPhotosFeelsBinding
import com.example.patientsportal.entities.dbEntities.PregnancyFeel
import java.text.SimpleDateFormat
import java.util.Locale

class PregnancyFeelAdapter (private var listPregnancyFeels: ArrayList<PregnancyFeel>, private val onClick: (PregnancyFeel, Int) -> Unit): RecyclerView.Adapter<PregnancyFeelAdapter.PregnancyFeelViewHolder>() {

    inner class PregnancyFeelViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPregnancyNotesPhotosFeelsBinding.bind(view)

        fun bind (pf: PregnancyFeel, onClick: (PregnancyFeel, Int) -> Unit) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd\nMMM\nEEEE", Locale.getDefault())
            val date = inputFormat.parse(pf.date)
            binding.tvDate.text = outputFormat.format(date!!).replace(".", "").substring(0, 10)
            binding.tvPFP.text = pf.feel
            binding.cvMain.setOnClickListener { onClick(pf, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnancyFeelViewHolder {
        return PregnancyFeelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pregnancy_notes_photos_feels, parent, false))
    }

    override fun getItemCount(): Int {
        return listPregnancyFeels.size
    }

    override fun onBindViewHolder(holder: PregnancyFeelViewHolder, position: Int) {
        holder.bind(listPregnancyFeels[position], onClick)
    }
}