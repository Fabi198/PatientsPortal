package com.example.patientsportal.adapters.appointmentsAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemHistoryAppointmentBinding
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.objects.DateConverter.dateAppointmentConverter

class AppointmentsHistoryAdapter(private var listAppointments: ArrayList<Appointment>, private val context: Context, private val onClick: (Appointment) -> Unit): RecyclerView.Adapter<AppointmentsHistoryAdapter.HistoryAppointmentsViewHolder>() {

    fun updateData(newListAppointment: ArrayList<Appointment>) {
        listAppointments = newListAppointment
    }


    inner class HistoryAppointmentsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemHistoryAppointmentBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(appointment: Appointment, context: Context, onClick: (Appointment) -> Unit) {
            if (appointment.showUpPatient == context.getString(R.string.ausente_appointments)) { binding.btnReTakeAppointment.visibility = View.VISIBLE }
            binding.ivDoctor.setImageResource(appointment.doctorSpeciality.doctor.intImage)
            binding.tvDate.text = dateAppointmentConverter(appointment.date)
            binding.tvDoctor.text = "${appointment.doctorSpeciality.doctor.name} ${appointment.doctorSpeciality.doctor.lastName}"
            binding.tvPlace.text = appointment.place.address
            binding.tvSpeciality.text = appointment.doctorSpeciality.speciality.name
            binding.tvShowUpPatient.text = appointment.showUpPatient
            binding.btnReTakeAppointment.setOnClickListener { onClick(appointment) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAppointmentsViewHolder {
        return HistoryAppointmentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history_appointment, parent, false))
    }

    override fun getItemCount(): Int {
        return listAppointments.size
    }

    override fun onBindViewHolder(holder: HistoryAppointmentsViewHolder, position: Int) {
        holder.bind(listAppointments[position], context, onClick)
    }


}