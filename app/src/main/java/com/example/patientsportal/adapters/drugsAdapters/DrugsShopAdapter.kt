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
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlternativeDrugShopItem
import com.example.patientsportal.entities.dbEntities.DrugShopItem

class DrugsShopAdapter (private var listDrugs: ArrayList<DrugShopItem>, private val context: Context, private val onClick: (DrugShopItem, Boolean, Int) -> Unit, private val onClickAlter: (AlternativeDrugShopItem, Boolean, Int) -> Unit, private val onQuantityClick: (DrugShopItem) -> Unit, private val onQuantityAlterClick: (AlternativeDrugShopItem) -> Unit): RecyclerView.Adapter<DrugsShopAdapter.DrugsShopViewHolder>() {

    fun updateData(newListDrugs: ArrayList<DrugShopItem>) {
        listDrugs = newListDrugs
    }
    inner class DrugsShopViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemDrupShopBinding.bind(view)
        private lateinit var adapterAlterDrugs: AlternativeDrugsShopAdapter
        private val alterDrugsShopList = ArrayList<AlternativeDrugShopItem>()
        private var counter = 0

        fun bind (
            d: DrugShopItem,
            context: Context,
            onClick: (DrugShopItem, Boolean, Int) -> Unit
        ) {
            val dbPatientsPortal = DbPatientsPortal(context)
            val alterDrugsList = dbPatientsPortal.readAlternativesDrugs(d.drug.idDrug)
            binding.cbMainDrug.isChecked = d.isChecked
            binding.tvName.text = d.drug.nameDrugSubstance
            binding.tvDrugProduct.text = setBoldPartText(context.getString(R.string.recetado), d.drug.nameDrugProduct)

            if (d.drug.stockAvailable > 0) {
                binding.tvPrice.visibility = View.VISIBLE
                binding.tvQuantity.visibility = View.VISIBLE
                binding.tvQuantity.isEnabled = false
                binding.spinnerQuantity.visibility = View.VISIBLE
                binding.tvPrice.text = setBoldPartText(context.getString(R.string.precio_con_descuento), d.drug.price.toString())
            } else {
                binding.tvDrugProduct.alpha = 0.2F
                binding.tvDrugProduct.isEnabled = false
                binding.tvNoStock.alpha = 0.2F
                binding.tvNoStock.isEnabled = false
                binding.cbMainDrug.alpha = 0.2F
                binding.cbMainDrug.isEnabled = false
                binding.tvPrice.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
                binding.spinnerQuantity.visibility = View.GONE
                binding.tvNoStock.visibility = View.VISIBLE
            }

            binding.cbMainDrug.setOnClickListener {
                if (binding.cbMainDrug.isChecked) {
                    binding.cbMainDrug.isChecked = true
                    onClick(d, true, bindingAdapterPosition)
                    d.isChecked = true
                    binding.tvQuantity.isEnabled = true
                    binding.spinnerQuantity.isEnabled = true
                    setSpinner(d)
                } else {
                    binding.cbMainDrug.isChecked = false
                    binding.tvQuantity.isEnabled = false
                    binding.spinnerQuantity.isEnabled = false
                    onClick(d, false, bindingAdapterPosition)
                    d.isChecked = false
                    counter = 0
                }
            }


            if (alterDrugsList.size > 0) {
                alterDrugsList.forEach { alterDrugsShopList.add(AlternativeDrugShopItem(it, false)) }
                adapterAlterDrugs = AlternativeDrugsShopAdapter(alterDrugsShopList, context, { aD, b, i ->
                    onClickAlter(aD, b, i)
                }, { aD ->
                    onQuantityAlterClick(aD)
                })
                binding.rvAlternativeDrugs.adapter = adapterAlterDrugs
                binding.tvAlternativeDrug.visibility = View.VISIBLE
                binding.btnShowHideAlterDrugs.visibility = View.VISIBLE

                binding.btnShowHideAlterDrugs.setOnClickListener {
                    if (binding.tvShowAlterDrugs.visibility == View.VISIBLE) {
                        binding.rvAlternativeDrugs.visibility = View.VISIBLE
                        binding.tvShowAlterDrugs.visibility = View.GONE
                        binding.tvHideAlternativeDrug.visibility = View.VISIBLE
                    } else if (binding.tvShowAlterDrugs.visibility == View.GONE) {
                        binding.rvAlternativeDrugs.visibility = View.GONE
                        binding.tvHideAlternativeDrug.visibility = View.GONE
                        binding.tvShowAlterDrugs.visibility = View.VISIBLE
                    }
                }
            }


        }

        private fun setSpinner(d: DrugShopItem) {
            val arrayOptions = Array(d.drug.maximumUnitsToBuy + 1) { it.toString() }
            val adapterQuantitySpinner = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOptions)
            adapterQuantitySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(binding.spinnerQuantity) {
                adapter = adapterQuantitySpinner
                setSelection(1)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(context, "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedItem.toString()
                        d.quantity = Integer.parseInt(selectedItem.toString())
                        onQuantityClick(d)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugsShopViewHolder {
        return DrugsShopViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_drup_shop, parent, false))
    }

    override fun getItemCount(): Int {
        return listDrugs.size
    }

    override fun onBindViewHolder(holder: DrugsShopViewHolder, position: Int) {
        holder.bind(listDrugs[position], context, onClick)
    }

}