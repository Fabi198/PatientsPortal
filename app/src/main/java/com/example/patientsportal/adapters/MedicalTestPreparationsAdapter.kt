package com.example.patientsportal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemMedicalTestPreparationsBinding
import com.example.patientsportal.entities.dbEntities.MedicalTest

class MedicalTestPreparationsAdapter (private val listMedicalTestPreparations: ArrayList<MedicalTest>, private val context: Context): RecyclerView.Adapter<MedicalTestPreparationsAdapter.MedicalTestPreparationsViewHolder>() {

    inner class MedicalTestPreparationsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemMedicalTestPreparationsBinding.bind(view)

        fun bind(mT: MedicalTest, context: Context) {
            val animUp: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_up)
            val animDown: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_down)
            binding.tvItem.text = mT.name
            binding.tvPreparation.text = mT.preparation
            binding.cvMedicalTest.setOnClickListener {
                if (!mT.isPreparationVisible) {
                    binding.btnShowPreparation.startAnimation(animUp)
                    binding.cvPreparation.visibility = View.VISIBLE
                } else {
                    binding.btnShowPreparation.startAnimation(animDown)
                    binding.cvPreparation.visibility = View.GONE
                }
                mT.isPreparationVisible = !mT.isPreparationVisible
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalTestPreparationsViewHolder {
        return MedicalTestPreparationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_medical_test_preparations, parent, false))
    }

    override fun getItemCount(): Int {
        return listMedicalTestPreparations.size
    }

    override fun onBindViewHolder(holder: MedicalTestPreparationsViewHolder, position: Int) {
        holder.bind(listMedicalTestPreparations[position], context)
    }

}