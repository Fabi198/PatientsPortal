package com.example.patientsportal.adapters.autoCompleteAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemAutocompleteSpecialitiesBinding
import com.example.patientsportal.entities.dbEntities.DoctorSpeciality

class AutoCompleteTextSpecialitiesAdapter(context: Context, specialityList: ArrayList<DoctorSpeciality>): ArrayAdapter<DoctorSpeciality>(context, 0, specialityList) {

    private val specialityListFull = ArrayList<DoctorSpeciality>(specialityList)

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_autocomplete_specialities, parent, false)
        val binding = ItemAutocompleteSpecialitiesBinding.bind(view)

        val doctorSpeciality = getItem(position)

        if (doctorSpeciality != null) {
            binding.tvPlace.text = doctorSpeciality.speciality.name
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = ArrayList<DoctorSpeciality>()
                val addedItems = HashSet<String>()

                if (constraint.isNullOrEmpty()) {
                    suggestions.addAll(specialityListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()

                    for (doctorSpeciality in specialityListFull) {
                        val doctorSpecialityName = doctorSpeciality.speciality.name.lowercase()

                        if (doctorSpecialityName.contains(filterPattern) && !addedItems.contains(doctorSpecialityName)) {
                            suggestions.add(doctorSpeciality)
                            addedItems.add(doctorSpecialityName)
                        }
                    }
                }

                results.values = suggestions
                results.count = suggestions.size

                return results
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results?.values as ArrayList<DoctorSpeciality>)
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return ((resultValue as DoctorSpeciality).speciality.name)
            }
        }
    }

}