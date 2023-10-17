package com.example.patientsportal.adapters.pregnancyAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPregnancyNotesPhotosFeelsBinding
import com.example.patientsportal.entities.dbEntities.PregnancyNote
import java.text.SimpleDateFormat
import java.util.Locale

class PregnancyNotesAdapter (private var listPregnancyFeNotes: ArrayList<PregnancyNote>, private val onClick: (PregnancyNote, Int) -> Unit): RecyclerView.Adapter<PregnancyNotesAdapter.PregnancyNotesViewHolder>() {

    inner class PregnancyNotesViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPregnancyNotesPhotosFeelsBinding.bind(view)

        fun bind (pn: PregnancyNote, onClick: (PregnancyNote, Int) -> Unit) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd\nMMM\nEEEE", Locale.getDefault())
            val date = inputFormat.parse(pn.date)
            binding.tvDate.text = outputFormat.format(date!!).replace(".", "").substring(0, 10)
            binding.tvPFNPTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_event_24, 0, 0, 0)
            binding.tvPFNPTitle.text = pn.title
            binding.tvPFP.text = pn.note
            binding.cvMain.setOnClickListener { onClick(pn, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnancyNotesViewHolder {
        return PregnancyNotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pregnancy_notes_photos_feels, parent, false))
    }

    override fun getItemCount(): Int {
        return listPregnancyFeNotes.size
    }

    override fun onBindViewHolder(holder: PregnancyNotesViewHolder, position: Int) {
        holder.bind(listPregnancyFeNotes[position], onClick)
    }
}