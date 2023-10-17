package com.example.patientsportal.adapters.drugsAdapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemDrupShopBinding
import com.example.patientsportal.entities.dbEntities.AlternativeDrugShopItem

class AlternativeDrugsShopAdapter (private var alterDrugsList: ArrayList<AlternativeDrugShopItem>, private val context: Context, private val onClick: (AlternativeDrugShopItem, Boolean, Int) -> Unit, private val onQuantityClick: (AlternativeDrugShopItem) -> Unit): RecyclerView.Adapter<AlternativeDrugsShopAdapter.AlternativeDrugsShopViewHolder>() {

    fun updateData(newListDrugs: ArrayList<AlternativeDrugShopItem>) {
        alterDrugsList = newListDrugs
    }


    inner class AlternativeDrugsShopViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemDrupShopBinding.bind(view)
        private var counter = 0

        fun bind(
            aD: AlternativeDrugShopItem,
            context: Context,
            onClick: (AlternativeDrugShopItem, Boolean, Int) -> Unit
        ) {

            binding.cbMainDrug.isChecked = aD.isChecked
            binding.tvName.text = aD.alternativeDrug.name
            binding.tvDrugProduct.visibility = View.GONE


            if (aD.alternativeDrug.stockAvailable > 0) {
                binding.tvPrice.visibility = View.VISIBLE
                binding.tvQuantity.visibility = View.VISIBLE
                binding.tvQuantity.isEnabled = false
                binding.spinnerQuantity.visibility = View.VISIBLE
                binding.spinnerQuantity.isEnabled = false
                binding.tvPrice.text = setBoldPartText(context.getString(R.string.precio_con_descuento), aD.alternativeDrug.price.toString())
            } else {
                binding.tvPrice.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
                binding.spinnerQuantity.visibility = View.GONE
                binding.tvNoStock.visibility = View.VISIBLE
            }

            binding.cbMainDrug.setOnClickListener {
                if (binding.cbMainDrug.isChecked) {
                    binding.cbMainDrug.isChecked = true
                    aD.isChecked = true
                    onClick(aD, true, bindingAdapterPosition)
                    binding.tvQuantity.isEnabled = true
                    binding.spinnerQuantity.isEnabled = true
                    setSpinner(aD)
                } else {
                    binding.cbMainDrug.isChecked = false
                    binding.tvQuantity.isEnabled = false
                    binding.spinnerQuantity.isEnabled = false
                    aD.isChecked = false
                    onClick(aD, false, bindingAdapterPosition)
                    counter = 0
                }
            }
        }

        private fun setSpinner(aD: AlternativeDrugShopItem) {
            val arrayOptions = Array(aD.alternativeDrug.maximumUnitsToBuy + 1) { it.toString() }
            val adapterQuantitySpinner = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOptions)
            adapterQuantitySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(binding.spinnerQuantity) {
                adapter = adapterQuantitySpinner
                setSelection(1)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(context, "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedItem.toString()
                        aD.quantity = Integer.parseInt(selectedItem.toString())
                        onQuantityClick(aD)
                    }
                }
            }
        }

        private fun setBoldPartText(normalText: String, boldText: String): SpannableStringBuilder {
            val spannableString = SpannableString(boldText)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(normalText).append(spannableString)
            return spannableStringBuilder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlternativeDrugsShopViewHolder {
        return AlternativeDrugsShopViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_drup_shop, parent, false))
    }

    override fun getItemCount(): Int {
        return alterDrugsList.size
    }

    override fun onBindViewHolder(holder: AlternativeDrugsShopViewHolder, position: Int) {
        holder.bind(alterDrugsList[position], context, onClick)
    }


}