package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStep3Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce
import com.example.patientsportal.entities.dbEntities.AlternativeDrug
import com.example.patientsportal.entities.dbEntities.Drug
import com.example.patientsportal.objects.DateConverter.dateForBuyGoodDrugs


@Suppress("DEPRECATION")
class BuyGoodDrugsStep3 : Fragment(R.layout.fragment_buy_good_drugs_step3) {

    private lateinit var binding: FragmentBuyGoodDrugsStep3Binding
    lateinit var onBuyGoodDrugsStepsClicks: OnBuyGoodDrugsStepsClicks
    private lateinit var listAlterOrDrugShopItemParce: ArrayList<AlterOrDrugShopItemParce>
    private var total = 0.0
    private lateinit var drug: Drug
    private lateinit var alterDrug: AlternativeDrug
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private var serviceCost = 600.00
    private var counter = 1
    private var sot: Boolean ?= null
    private var place: String ?= null
    private var cellphone: String ?= null
    private var hour: String ?= null
    private var date: String ? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStep3Binding.bind(view)
        listAlterOrDrugShopItemParce = arguments?.getParcelableArrayList<AlterOrDrugShopItemParce>(getString(R.string.alterordrug_tag)) as ArrayList<AlterOrDrugShopItemParce>
        total = 0.0
        counter = 1
        dbPatientsPortal = DbPatientsPortal(requireContext())
        sot = arguments?.getBoolean(getString(R.string.shippingortakeaway_tag))
        place = arguments?.getString(getString(R.string.place_tag))
        cellphone = arguments?.getString(getString(R.string.cellphone_tag))
        hour = arguments?.getString(getString(R.string.hour_tag))
        date = arguments?.getString(getString(R.string.date_tag))

        setTable(listAlterOrDrugShopItemParce)
        setInfoCard(sot, place, cellphone, hour, date)

        binding.btnPayment.setOnClickListener { onBuyGoodDrugsStepsClicks.onClickStartPayment() }
        binding.btnCancel.setOnClickListener { onBuyGoodDrugsStepsClicks.onClickCancelOrder() }
    }

    private fun setInfoCard(sot: Boolean?, place: String?, cellphone: String?, hour: String?, date: String?) {
        if (sot != null && place != null && cellphone != null && hour != null && date != null) {
            binding.tvPlace.text = setBoldPartText(getString(R.string.lugar_de_entrega_), place)
            binding.tvCellphone.text = setBoldPartText(getString(R.string.tel_fono_de_contacto), cellphone)
            binding.tvHour.text = setBoldPartText(getString(R.string.horario_de_entrega_), hour)
            binding.tvDate.text = setBoldPartText(getString(R.string.fecha_de_entrega_), dateForBuyGoodDrugs(date))

            binding.tvHour.visibility = if (sot) { View.VISIBLE } else { View.GONE }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTable(listAlterOrDrugShopItemParce: java.util.ArrayList<AlterOrDrugShopItemParce>) {

        // Agregar filas de productos dinámicamente
        for (aod in listAlterOrDrugShopItemParce) {
            if (aod.alterOrDrug == getString(R.string.drug)) { drug = dbPatientsPortal.readDrug(aod.id) }
            if (aod.alterOrDrug == getString(R.string.alter)) { alterDrug = dbPatientsPortal.readAlternativeDrug(aod.id) }

            val row = TableRow(requireContext())
            val margin = resources.getDimensionPixelSize(R.dimen.table_text_margin)

            val tvQuantity = TextView(requireContext())
            val quantityLayoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f)
            quantityLayoutParams.setMargins(margin)
            tvQuantity.layoutParams = quantityLayoutParams
            tvQuantity.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            tvQuantity.setTextColor(resources.getColor(R.color.black, null))
            tvQuantity.text = aod.quantity.toString()

            val tvName = TextView(requireContext())
            val nameLayoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f)
            nameLayoutParams.setMargins(margin)
            tvName.layoutParams = nameLayoutParams
            tvName.setTypeface(null, Typeface.BOLD)
            tvName.setTextColor(resources.getColor(R.color.black, null))
            tvName.isSingleLine = true
            tvName.text = if (alterOrDrug(aod.alterOrDrug)) { drug.nameDrugProduct } else { alterDrug.drug.nameDrugProduct }

            val tvPrice = TextView(requireContext())
            val priceLayoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f)
            priceLayoutParams.setMargins(margin)
            tvPrice.layoutParams = priceLayoutParams
            tvPrice.setTextColor(resources.getColor(R.color.black, null))
            tvPrice.text = if (alterOrDrug(aod.alterOrDrug)) { "$ ${String.format("%.2f", (drug.price*aod.quantity.toDouble()))}" } else { "$ ${String.format("%.2f", (alterDrug.price*aod.quantity.toDouble()))}" }

            row.addView(tvQuantity)
            row.addView(tvName)
            row.addView(tvPrice)

            binding.tableLayout.addView(row, counter)
            counter++
            total += if (alterOrDrug(aod.alterOrDrug)) { drug.price*aod.quantity.toDouble() } else { alterDrug.price*aod.quantity.toDouble() }
        }


        total += serviceCost
        binding.tvServiceCost.text = "$ ${String.format("%.2f", serviceCost)}"
        binding.tvTotal.text = "$ ${String.format("%.2f", total)}"


        for (i in 0 until binding.tableLayout.childCount) {
            val row = binding.tableLayout.getChildAt(i) as TableRow

            // Alternar colores de fondo
            if (i % 2 == 0) {
                row.setBackgroundColor(resources.getColor(R.color.blueLight, null))
            } else {
                row.setBackgroundColor(resources.getColor(R.color.white, null))
            }
        }

    }

    private fun alterOrDrug(alterOrDrug: String): Boolean {
        return alterOrDrug == getString(R.string.drug)
    }

    private fun setBoldPartText(normalText: String, boldText: String): SpannableStringBuilder {
        val spannableString = SpannableString(boldText)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(normalText).append(spannableString)
        return spannableStringBuilder
    }

}