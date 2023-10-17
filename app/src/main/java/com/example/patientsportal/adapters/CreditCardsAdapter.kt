package com.example.patientsportal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemCreditCardBinding
import com.example.patientsportal.entities.dbEntities.CreditCard

class CreditCardsAdapter (private var listCreditCards: ArrayList<CreditCard>, private val context: Context, private var positionSelected: Int, private val onClick: (CreditCard, Int) -> Unit): RecyclerView.Adapter<CreditCardsAdapter.CreditCardsViewHolder>() {

    fun updateData(newPositionSelected: Int) {
        positionSelected = newPositionSelected
    }

    inner class CreditCardsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemCreditCardBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(cd: CreditCard, context: Context, onClick: (CreditCard, Int) -> Unit) {
            binding.btnCreditCard.isChecked = positionSelected == bindingAdapterPosition
            when (cd.cardBrand) {
                context.getString(R.string.visa) -> { binding.btnCreditCard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_credit_card_visa, 0, 0, 0) }
                context.getString(R.string.master) -> { binding.btnCreditCard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_credit_card_master, 0, 0, 0) }
                context.getString(R.string.amex) -> { binding.btnCreditCard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_credit_card_amex, 0, 0, 0) }
            }
            binding.btnCreditCard.text = "**** **** **** ${cd.cardNumber.substring(cd.cardNumber.length - 4)}"
            binding.btnCreditCard.setOnClickListener { onClick(cd, bindingAdapterPosition) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardsViewHolder {
        return CreditCardsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_credit_card, parent, false))
    }

    override fun getItemCount(): Int {
        return listCreditCards.size
    }

    override fun onBindViewHolder(holder: CreditCardsViewHolder, position: Int) {
        holder.bind(listCreditCards[position], context, onClick)
    }
}