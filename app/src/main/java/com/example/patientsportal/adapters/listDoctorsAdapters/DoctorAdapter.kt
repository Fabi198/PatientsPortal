package com.example.patientsportal.adapters.listDoctorsAdapters

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
import com.example.patientsportal.databinding.ItemDoctorBinding
import com.example.patientsportal.entities.dbEntities.Doctor

@Suppress("DEPRECATION")
class DoctorAdapter(private val listItems: ArrayList<Doctor>, private val context: Context, private val onClick: (Doctor, Boolean) -> Unit) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDoctorBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(doctor: Doctor, context: Context, onClick: (Doctor, Boolean) -> Unit) {
            val b = true
            val animUp: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_up)
            val animDown: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_down)
            binding.ivItem.setImageResource(doctor.intImage)
            binding.tvItem.text = "${doctor.name} ${doctor.lastName}"
            binding.btnShowCards.setOnClickListener {
                if (doctor.isExpanded) { collapseMiniCards(animDown, context) } else { expandMiniCards(animUp, context) }
                doctor.isExpanded = !doctor.isExpanded
            }
            binding.sendEmail.setOnClickListener { onClick(doctor, !b) }
            binding.takeAppointment.setOnClickListener { onClick(doctor, b) }

            // Actualizar la visibilidad de las miniCards
            binding.miniCards.visibility = if (doctor.isExpanded) View.VISIBLE else View.GONE
            val anim = if (doctor.isExpanded) animUp else animDown
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false))
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(listItems[position], context, onClick)
    }
}