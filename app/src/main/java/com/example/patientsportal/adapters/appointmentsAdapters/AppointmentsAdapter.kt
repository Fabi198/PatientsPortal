package com.example.patientsportal.adapters.appointmentsAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemAppointmentBinding
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.objects.DateConverter.dateAppointmentConverter

class AppointmentsAdapter(
    private var listAppointments: List<Appointment>,
    private val context: Context,
    private var positionSelected: Int,
    private val onClick: (Appointment, Int) -> Unit
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder>() {


    fun updateData(newPositionSelected: Int) {
        positionSelected = newPositionSelected
    }

    class AppointmentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemAppointmentBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(appointment: Appointment, context: Context, positionSelected: Int, onClick: (Appointment, Int) -> Unit) {
            binding.rbAppointment.isChecked = positionSelected == bindingAdapterPosition
            binding.tvDate.text = dateAppointmentConverter(appointment.date)
            binding.tvDoctor.text = "${appointment.doctorSpeciality.doctor.lastName}, ${appointment.doctorSpeciality.doctor.name}"
            binding.tvSpeciality.text = appointment.doctorSpeciality.speciality.name
            binding.tvPlace.text = "${context.getString(R.string.turnos)} - ${appointment.place.name}"
            binding.rbAppointment.setOnClickListener { onClick(appointment, bindingAdapterPosition) }
            binding.clItem.setOnClickListener { onClick(appointment, bindingAdapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
        return AppointmentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false))
    }

    override fun getItemCount(): Int {
        return listAppointments.size
    }

    override fun onBindViewHolder(holder: AppointmentsViewHolder, position: Int) {
        holder.bind(listAppointments[position], context, positionSelected, onClick)
    }
}

