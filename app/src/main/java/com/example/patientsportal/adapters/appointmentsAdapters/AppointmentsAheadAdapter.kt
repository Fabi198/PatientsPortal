package com.example.patientsportal.adapters.appointmentsAdapters

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemAheadAppointmentBinding
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.objects.DateConverter.dateAppointmentConverter

@Suppress("DEPRECATION")
class AppointmentsAheadAdapter(private var listAppointments: ArrayList<Appointment>, private val context: Context, private val onClick: (Appointment, Int, Boolean) -> Unit) : RecyclerView.Adapter<AppointmentsAheadAdapter.AppointmentsAheadViewHolder>() {

    fun updateData(newListAppointment: ArrayList<Appointment>) {
        listAppointments = newListAppointment
    }

    inner class AppointmentsAheadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAheadAppointmentBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(appointment: Appointment, context: Context, onClick: (Appointment, Int, Boolean) -> Unit) {
            val b = true
            val animUp: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_up)
            val animDown: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_down)
            binding.ivDoctor.setImageResource(appointment.doctorSpeciality.doctor.intImage)
            binding.tvDate.text = dateAppointmentConverter(appointment.date)
            binding.tvDoctor.text = "${appointment.doctorSpeciality.doctor.name} ${appointment.doctorSpeciality.doctor.lastName}"
            binding.tvSpeciality.text = appointment.doctorSpeciality.speciality.name
            binding.tvPlace.text = appointment.place.address
            binding.cvItem.setOnClickListener {
                if (appointment.isExpanded) { collapseMiniCards(animDown, context) } else { expandMiniCards(animUp, context) }
                appointment.isExpanded = !appointment.isExpanded
            }
            binding.delete.setOnClickListener { onClick(appointment, bindingAdapterPosition, !b) }
            binding.addToCalendar.setOnClickListener { onClick(appointment, bindingAdapterPosition, b) }

            // Actualizar la visibilidad de las miniCards
            binding.miniCards.visibility = if (appointment.isExpanded) View.VISIBLE else View.GONE
            val anim = if (appointment.isExpanded) animUp else animDown
            binding.btnShowCards.startAnimation(anim)
        }

        private fun expandMiniCards(animUp: Animation, context: Context) {
            binding.miniCards.visibility = View.VISIBLE
            val anim = ValueAnimator.ofInt(binding.cvItem.height, binding.cvItem.height + context.resources.getDimensionPixelSize(R.dimen.mini_cards_height))
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                binding.cvItem.layoutParams.height = value
                binding.cvItem.requestLayout()
            }
            anim.duration = 500
            anim.start()
            binding.btnShowCards.startAnimation(animUp)
        }

        private fun collapseMiniCards(animDown: Animation, context: Context) {
            binding.btnShowCards.startAnimation(animDown)
            val anim = ValueAnimator.ofInt(binding.cvItem.height, binding.cvItem.height - context.resources.getDimensionPixelSize(R.dimen.mini_cards_height))
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                binding.cvItem.layoutParams.height = value
                binding.cvItem.requestLayout()
            }
            anim.duration = 500
            anim.start()
            Handler().postDelayed({
                binding.miniCards.visibility = View.GONE
            }, 500)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsAheadViewHolder {
        return AppointmentsAheadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ahead_appointment, parent, false))
    }

    override fun getItemCount(): Int {
        return listAppointments.size
    }

    override fun onBindViewHolder(holder: AppointmentsAheadViewHolder, position: Int) {
        holder.bind(listAppointments[position], context, onClick)
    }
}