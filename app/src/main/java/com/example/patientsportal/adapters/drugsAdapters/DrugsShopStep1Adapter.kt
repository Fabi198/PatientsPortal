package com.example.patientsportal.adapters.drugsAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ItemDrupShopStep1Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce
import com.example.patientsportal.entities.dbEntities.AlternativeDrug
import com.example.patientsportal.entities.dbEntities.Drug

class DrugsShopStep1Adapter (private var listDrugs: ArrayList<AlterOrDrugShopItemParce>, private val context: Context, private val onDeleteClick: (Int) -> Unit): RecyclerView.Adapter<DrugsShopStep1Adapter.DrugsShopItem1ViewHolder>() {

    fun updateData(newList: ArrayList<AlterOrDrugShopItemParce>) {
        listDrugs = newList
    }

    inner class DrugsShopItem1ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemDrupShopStep1Binding.bind(view)
        private lateinit var dbPatientsPortal: DbPatientsPortal
        private lateinit var drug: Drug
        private lateinit var alterDrug: AlternativeDrug

        @SuppressLint("SetTextI18n")
        fun bind(aod: AlterOrDrugShopItemParce, context: Context, onDeleteClick: (Int) -> Unit) {
            dbPatientsPortal = DbPatientsPortal(context)
            if (aod.alterOrDrug == context.getString(R.string.drug)) { drug = dbPatientsPortal.readDrug(aod.id) }
            if (aod.alterOrDrug == context.getString(R.string.alter)) { alterDrug = dbPatientsPortal.readAlternativeDrug(aod.id) }
            binding.tvName.text = if (alterOrDrug(aod.alterOrDrug)) { drug.nameDrugSubstance } else { alterDrug.drug.nameDrugSubstance }
            binding.tvDrugProduct.text = if (alterOrDrug(aod.alterOrDrug)) { drug.nameDrugProduct } else { alterDrug.name }
            binding.tvQuantity.text = "${context.getString(R.string.cantidad)} ${aod.quantity}"
            binding.tvPrice.text = if (alterOrDrug(aod.alterOrDrug)) { setBoldPartText(context.getString(R.string.subtotal_nprecio_con_dto), String.format("%.2f", (drug.price*aod.quantity.toDouble()))) } else { setBoldPartText(context.getString(R.string.subtotal_nprecio_con_dto), String.format("%.2f", (alterDrug.price*aod.quantity.toDouble()))) }
            binding.btnDelete.setOnClickListener { onDeleteClick(bindingAdapterPosition) }
        }

        private fun alterOrDrug(alterOrDrug: String): Boolean {
            return alterOrDrug == context.getString(R.string.drug)
        }

        private fun setBoldPartText(normalText: String, boldText: String): SpannableStringBuilder {
            val spannableString = SpannableString(boldText)
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(normalText).append(spannableString)
            return spannableStringBuilder
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugsShopItem1ViewHolder {
        return DrugsShopItem1ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_drup_shop_step_1, parent, false))
    }

    override fun getItemCount(): Int {
        return listDrugs.size
    }

    override fun onBindViewHolder(holder: DrugsShopItem1ViewHolder, position: Int) {
        holder.bind(listDrugs[position], context, onDeleteClick)
    }
}