package com.example.patientsportal.adapters.customAlertDialogAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemPlaceWithCheckboxBinding
import com.example.patientsportal.entities.dbEntities.Place

class CustomAlertDialogMultiPlacesAdapter(private val listPlaces: ArrayList<Place>, private var positionSelected: Int, private val onClickAdd: (Place, Int) -> Unit, private val onClickErase: (Place, Int) -> Unit): RecyclerView.Adapter<CustomAlertDialogMultiPlacesAdapter.PlaceWithCheckboxViewHolder>() {

    fun updateData(newPositionSelected: Int) {
        positionSelected = newPositionSelected
    }

    inner class PlaceWithCheckboxViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemPlaceWithCheckboxBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(place: Place, onClickAdd: (Place, Int) -> Unit, onClickErase: (Place, Int) -> Unit) {
            if (positionSelected == -1) { binding.rbDoctor.isChecked = false }
            binding.rbDoctor.isChecked = place.multiSelected
            binding.tvItem.text = place.name
            binding.rbDoctor.setOnCheckedChangeListener { _, isChecked -> if (isChecked) { onClickAdd(place, bindingAdapterPosition) } else { onClickErase(place, bindingAdapterPosition) } }
            binding.clItem.setOnClickListener { binding.rbDoctor.isChecked = !binding.rbDoctor.isChecked }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceWithCheckboxViewHolder {
        return PlaceWithCheckboxViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_place_with_checkbox, parent, false))
    }

    override fun getItemCount(): Int {
        return listPlaces.size
    }

    override fun onBindViewHolder(holder: PlaceWithCheckboxViewHolder, position: Int) {
        holder.bind(listPlaces[position], onClickAdd, onClickErase)
    }
}