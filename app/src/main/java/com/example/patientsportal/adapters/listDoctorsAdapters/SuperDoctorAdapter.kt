package com.example.patientsportal.adapters.listDoctorsAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemsRecyclerBinding
import com.example.patientsportal.entities.dbEntities.Doctor

class SuperDoctorAdapter(private val listSuperItems: Map<String, ArrayList<Doctor>>, private val context: Context, private val onClick: (Doctor, Boolean) -> Unit) : RecyclerView.Adapter<SuperDoctorAdapter.SuperItemViewHolder>() {

    inner class SuperItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemsRecyclerBinding.bind(view)

        fun bind(pds: ArrayList<Doctor>, specialityName: String, context: Context, onClick: (Doctor, Boolean) -> Unit) {
            val adapter = DoctorAdapter(pds, context) { doc, b -> onClick(doc, b) }
            binding.rvItems.adapter = adapter
            binding.tvItemsRecyclerTitle.text = specialityName
            binding.tvItemsRecyclerTitle.visibility = View.VISIBLE
            binding.rvItems.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperItemViewHolder {
        return SuperItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_recycler, parent, false))
    }

    override fun getItemCount(): Int {
        return listSuperItems.size
    }

    override fun onBindViewHolder(holder: SuperItemViewHolder, position: Int) {
        val specialityName = listSuperItems.keys.toList()[position]
        val listDoctors = listSuperItems[specialityName]
        listDoctors?.let { holder.bind(it, specialityName, context, onClick) }
    }
}