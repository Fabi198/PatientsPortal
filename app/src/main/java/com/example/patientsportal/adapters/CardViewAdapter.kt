package com.example.patientsportal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.CardViewBinding
import com.example.patientsportal.entities.CustomCardView

class CardViewAdapter (private val listCard: ArrayList<CustomCardView>, private val onClick: (String) -> Unit): RecyclerView.Adapter<CardViewAdapter.CardViewViewHolder>() {

    inner class CardViewViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private val binding = CardViewBinding.bind(v)

        fun bind(c: CustomCardView, onClick: (String) -> Unit) {
            binding.ivCard.setImageResource(c.image)
            binding.tvCard.text = c.title
            binding.cvMain.setOnClickListener { onClick(c.title) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewViewHolder {
        return CardViewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun getItemCount(): Int {
        return listCard.size
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(listCard[position], onClick)
    }
}