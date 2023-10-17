package com.example.patientsportal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemNotificationBinding
import com.example.patientsportal.entities.dbEntities.Notification
import com.example.patientsportal.objects.DateConverter.calculateDateDifference

class NotificationAdapter(private var listNotis: ArrayList<Notification>, private val onClick: (Notification, Int) -> Unit): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    fun updateData(newList: ArrayList<Notification>) {
        listNotis = newList
    }

    inner class NotificationViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemNotificationBinding.bind(view)

        fun bind(noti: Notification, onClick: (Notification, Int) -> Unit) {
            val image = when (noti.title) {
                "Estudios" -> { R.drawable.baseline_description_24 }
                "Portal de Salud" -> { R.drawable.icon_main }
                "Medicamentos" -> { R.drawable.icon_drugs }
                "Derivaciones" -> { R.drawable.icon_doctors }
                else -> { R.drawable.baseline_help_24 }
            }
            binding.ivNoti.setImageResource(image)
            binding.tvTitle.text = noti.title
            binding.tvDateUntilToday.text = calculateDateDifference(noti.date)
            binding.tvDescription.text = noti.description
            binding.cvMain.setOnClickListener { onClick(noti, bindingAdapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))
    }

    override fun getItemCount(): Int {
        return listNotis.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listNotis[position], onClick)
    }
}