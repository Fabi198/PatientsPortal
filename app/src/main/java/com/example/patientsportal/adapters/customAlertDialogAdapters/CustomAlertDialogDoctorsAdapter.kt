package com.example.patientsportal.adapters.customAlertDialogAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemDoctorWithCheckboxBinding
import com.example.patientsportal.entities.dbEntities.DoctorSpeciality

class CustomAlertDialogDoctorsAdapter(private val listItems: ArrayList<DoctorSpeciality>, private var positionSelected: Int, private val onClick: (DoctorSpeciality, Int) -> Unit): RecyclerView.Adapter<CustomAlertDialogDoctorsAdapter.DoctorWithCheckboxViewHolder>() {

    fun updateData(newPositionSelected: Int) {
        positionSelected = newPositionSelected
    }

    inner class DoctorWithCheckboxViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemDoctorWithCheckboxBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(doctorSpeciality: DoctorSpeciality, positionSelected: Int, onClick: (DoctorSpeciality, Int) -> Unit) {
            binding.rbDoctor.isChecked = positionSelected == bindingAdapterPosition
            binding.ivItem.setImageResource(doctorSpeciality.doctor.intImage)
            binding.tvItem.text = "${doctorSpeciality.doctor.name} ${doctorSpeciality.doctor.lastName}"
            binding.tvSpeciality.text = doctorSpeciality.speciality.name
            binding.rbDoctor.setOnClickListener { onClick(doctorSpeciality, bindingAdapterPosition) }
            binding.clItem.setOnClickListener { binding.rbDoctor.isActivated = true; onClick(doctorSpeciality, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorWithCheckboxViewHolder {
        return DoctorWithCheckboxViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_with_checkbox, parent, false))
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: DoctorWithCheckboxViewHolder, position: Int) {
        holder.bind(listItems[position], positionSelected, onClick)
    }
}