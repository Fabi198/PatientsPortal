package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.adapters.drugsAdapters.DrugsShopStep1Adapter
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStep1Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce


@Suppress("DEPRECATION")
class BuyGoodDrugsStep1 : Fragment(R.layout.fragment_buy_good_drugs_step1) {

    private lateinit var binding: FragmentBuyGoodDrugsStep1Binding
    lateinit var onBuyGoodDrugsStepsClicks: OnBuyGoodDrugsStepsClicks
    private lateinit var adapter: DrugsShopStep1Adapter
    private lateinit var listAlterOrDrugShopItemParce: ArrayList<AlterOrDrugShopItemParce>
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private val dollar = "$"
    private var total = 0.0

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStep1Binding.bind(view)
        listAlterOrDrugShopItemParce = arguments?.getParcelableArrayList<AlterOrDrugShopItemParce>(getString(R.string.alterordrug_tag)) as ArrayList<AlterOrDrugShopItemParce>

        if (listAlterOrDrugShopItemParce.size > 0) {
            dbPatientsPortal = DbPatientsPortal(requireContext())
            onBuyGoodDrugsStepsClicks.onQueryForShoppingCartItemsQuantity(listAlterOrDrugShopItemParce.size)
            adapter = DrugsShopStep1Adapter(listAlterOrDrugShopItemParce, requireContext()) {
                updateData(it)
            }
            binding.rvBuyGoodDrugsStep1.adapter = adapter
            Handler().postDelayed({
                binding.pbBuyGoodDrugsStep1.visibility = View.GONE
                binding.rvBuyGoodDrugsStep1.visibility = View.VISIBLE
            }, 650)


            binding.tvTotal.text = "$dollar ${String.format("%.2f", checkTotal())}"
            binding.btnSelectPlace.alpha = if (listAlterOrDrugShopItemParce.size > 0) { 1F } else { 0.5F }
            binding.btnSelectPlace.setOnClickListener { if (listAlterOrDrugShopItemParce.size > 0) { onBuyGoodDrugsStepsClicks.onClickSelectPlace() } }
            binding.btnAddMoreProducts.setOnClickListener { onBuyGoodDrugsStepsClicks.onClickAddMoreProducts() }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun updateData(position: Int) {
        if (!binding.rvBuyGoodDrugsStep1.isComputingLayout) {
            listAlterOrDrugShopItemParce.removeAt(position)
            adapter.updateData(listAlterOrDrugShopItemParce)
            adapter.notifyItemRemoved(position)
            adapter.notifyDataSetChanged()
            binding.tvTotal.text = "$dollar ${String.format("%.2f", checkTotal())}"
            onBuyGoodDrugsStepsClicks.onDeleteDrugShopItem(listAlterOrDrugShopItemParce.size)
            binding.btnSelectPlace.alpha = if (listAlterOrDrugShopItemParce.size > 0) { 1F } else { 0.5F }
        }
    }

    private fun checkTotal(): Double {
        total = 0.0
        listAlterOrDrugShopItemParce.forEach { total += if (it.alterOrDrug == getString(R.string.drug)) {
            (dbPatientsPortal.readDrug(it.id).price*it.quantity)
        } else {
            (dbPatientsPortal.readAlternativeDrug(it.id).price*it.quantity)
        } }
        return total
    }

}