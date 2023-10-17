package com.example.patientsportal.adapters.autoCompleteAdapters



import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemDoctorWithCheckboxBinding
import com.example.patientsportal.entities.dbEntities.DoctorSpeciality

class AutoCompleteTextDoctorsAdapter(context: Context, doctorList: ArrayList<DoctorSpeciality>): ArrayAdapter<DoctorSpeciality>(context, 0, doctorList) {

    private val doctorListFull = ArrayList<DoctorSpeciality>(doctorList)

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_doctor_with_checkbox, parent, false)
        val binding = ItemDoctorWithCheckboxBinding.bind(view)

        val doctorSpeciality = getItem(position)

        if (doctorSpeciality != null) {
            binding.rbDoctor.visibility = View.GONE
            binding.tvItem.text = "${doctorSpeciality.doctor.name} ${doctorSpeciality.doctor.lastName}"
            binding.tvSpeciality.text = doctorSpeciality.speciality.name
            binding.ivItem.setImageResource(doctorSpeciality.doctor.intImage)
        }



        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = ArrayList<DoctorSpeciality>()
                if (constraint.isNullOrEmpty()) {
                    suggestions.addAll(doctorListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()

                    for (doctorSpeciality in doctorListFull) {
                        if (doctorSpeciality.doctor.name.lowercase().contains(filterPattern) || doctorSpeciality.doctor.lastName.lowercase().contains(filterPattern)) {
                            suggestions.add(doctorSpeciality)
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
                return ("${(resultValue as DoctorSpeciality).doctor.name} ${resultValue.doctor.lastName}")
            }
        }
    }

}
