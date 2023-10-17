package com.example.patientsportal.adapters.appointmentsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemAppointmentsAheadAndLatestMedicalTestMainActivityBinding
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.objects.DateConverter.dateSimpleConverter

class AppointmentsAheadMainActivityAdapter(private val listAppointments: ArrayList<Appointment>, private val onClick: (Appointment, Int) -> Unit): RecyclerView.Adapter<AppointmentsAheadMainActivityAdapter.AppointmentsAheadMainActivityViewHolder>() {


    inner class AppointmentsAheadMainActivityViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private var binding = ItemAppointmentsAheadAndLatestMedicalTestMainActivityBinding.bind(view)

        fun bind(appointment: Appointment, onClick: (Appointment, Int) -> Unit) {
            binding.tvDate.text = dateSimpleConverter(appointment.date)
            binding.tvSpeciality.text = appointment.doctorSpeciality.speciality.name
            binding.clItem.setOnClickListener { onClick(appointment, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsAheadMainActivityViewHolder {
        return AppointmentsAheadMainActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_appointments_ahead_and_latest_medical_test_main_activity, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listAppointments.size > 3) {
            3
        } else {
            listAppointments.size
        }
    }

    override fun onBindViewHolder(holder: AppointmentsAheadMainActivityViewHolder, position: Int) {
        holder.bind(listAppointments[position], onClick)
    }
}