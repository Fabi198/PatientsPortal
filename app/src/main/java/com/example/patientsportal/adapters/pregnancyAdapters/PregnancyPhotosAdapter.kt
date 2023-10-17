package com.example.patientsportal.adapters.pregnancyAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPregnancyNotesPhotosFeelsBinding
import com.example.patientsportal.entities.dbEntities.PregnancyPhoto
import java.text.SimpleDateFormat
import java.util.Locale

class PregnancyPhotosAdapter (private var listPregnancyPhotos: ArrayList<PregnancyPhoto>, private val context: Context, private val onClick: (PregnancyPhoto, Int) -> Unit): RecyclerView.Adapter<PregnancyPhotosAdapter.PregnancyPhotosViewHolder>() {

    inner class PregnancyPhotosViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPregnancyNotesPhotosFeelsBinding.bind(view)

        @SuppressLint("UseCompatTextViewDrawableApis")
        fun bind (pp: PregnancyPhoto, context: Context, onClick: (PregnancyPhoto, Int) -> Unit) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd\nMMM\nEEEE", Locale.getDefault())
            val date = inputFormat.parse(pp.date)
            binding.tvDate.text = outputFormat.format(date!!).replace(".", "").substring(0, 10)
            binding.tvPFNPTitle.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.blue, null))
            binding.tvPFNPTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_camera_alt_24, 0, 0, 0)
            binding.tvPFNPTitle.text = pp.title
            binding.tvPFP.text = context.getString(R.string.foto)
            binding.cvMain.setOnClickListener { onClick(pp, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PregnancyPhotosViewHolder {
        return PregnancyPhotosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pregnancy_notes_photos_feels, parent, false))
    }

    override fun getItemCount(): Int {
        return listPregnancyPhotos.size
    }

    override fun onBindViewHolder(holder: PregnancyPhotosViewHolder, position: Int) {
        holder.bind(listPregnancyPhotos[position], context, onClick)
    }
}