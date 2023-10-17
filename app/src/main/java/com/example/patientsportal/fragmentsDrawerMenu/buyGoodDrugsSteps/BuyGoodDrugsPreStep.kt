package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.drugsAdapters.DrugsShopAdapter
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsPreStepBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce
import com.example.patientsportal.entities.dbEntities.AlternativeDrugShopItem
import com.example.patientsportal.entities.dbEntities.DrugShopItem
import com.example.patientsportal.objects.GetPatient
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class BuyGoodDrugsPreStep : Fragment(R.layout.fragment_buy_good_drugs_pre_step) {

    private lateinit var binding: FragmentBuyGoodDrugsPreStepBinding
    private var listDrugShopItems = ArrayList<DrugShopItem>()
    private var listDrugShopItemsSelected = ArrayList<DrugShopItem>()
    private var listAlterDrugShopItemsSelected = ArrayList<AlternativeDrugShopItem>()
    private var listAlterOrDrugShopItemParce = ArrayList<AlterOrDrugShopItemParce>()
    private var enabledReNewPrescription = false
    private lateinit var adapter: DrugsShopAdapter
    private var shopItemCounter = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsPreStepBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        shopItemCounter = 0
        binding.tvCantShoppingCart.text = shopItemCounter.toString()

        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        if (idContainer != null) {
            binding.btnNextStep.setOnClickListener {
                if (checkEnableBtn()) {
                    showFragmentFromFragment(requireActivity(), BuyGoodDrugsStepsHolder(), getString(R.string.buygooddrugsstepsholder_tag), requireContext(), idContainer, listAlterOrDrugShopItemParce = listAlterOrDrugShopItemParce)
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                listDrugShopItems.clear()
                listDrugShopItemsSelected.clear()
                listAlterDrugShopItemsSelected.clear()
                listAlterOrDrugShopItemParce.clear()
                val dbPatientsPortal = DbPatientsPortal(requireContext())
                val list = dbPatientsPortal.readAllPrescriptionsByPatient(GetPatient.getPatient(requireActivity(), requireContext()).idPatient, getString(R.string.current), "")
                withContext(Dispatchers.Main) {
                    if (list.size > 0) {
                        list.forEach { listDrugShopItems.add(DrugShopItem(it.drug, false, 0)) }
                        adapter = DrugsShopAdapter(listDrugShopItems, requireContext(), { dSI, b, i ->
                            updateData(b, i, dSI)
                            if (b) { shopItemCounter++ } else { shopItemCounter-- }
                            binding.tvCantShoppingCart.text = shopItemCounter.toString()
                        },  { aD, b, i ->
                            updateData(b, i, aDSI = aD)
                            if (b) { shopItemCounter++ } else { shopItemCounter-- }
                            binding.tvCantShoppingCart.text = shopItemCounter.toString()
                        }, { dSI ->
                            updateQuantity(dSI)
                        }, { aDSI ->
                            updateQuantity(aDSI = aDSI)
                        })
                        binding.rvBuyGoodDrugsPreStep.adapter = adapter
                        Handler().postDelayed({
                            binding.pbBuyGoodDrugsPreStep.visibility = View.GONE
                            binding.rvBuyGoodDrugsPreStep.visibility = View.VISIBLE
                        }, 650)
                    }
                }
            }
        }
    }

    private fun checkEnableBtn(): Boolean {
        val b: Boolean
        var count = 0
        listDrugShopItemsSelected.forEach { if (it.drug.idDrug != 0) { count++ } }
        listAlterDrugShopItemsSelected.forEach { if (it.alternativeDrug.idAlternativeDrug != 0) { count++ } }
        b = count > 0
        enabledReNewPrescription = count > 0
        binding.btnNextStep.alpha = if (count > 0) { 1F } else { 0.5F }
        listAlterOrDrugShopItemParce.clear()
        listDrugShopItemsSelected.forEach { listAlterOrDrugShopItemParce.add(AlterOrDrugShopItemParce("Drug", it.drug.idDrug, it.quantity)) }
        listAlterDrugShopItemsSelected.forEach { listAlterOrDrugShopItemParce.add(AlterOrDrugShopItemParce("Alter", it.alternativeDrug.idAlternativeDrug, it.quantity)) }
        return b
    }

    private fun updateQuantity(dSI: DrugShopItem ?= null, aDSI: AlternativeDrugShopItem ?= null) {
        if (dSI != null) {
            for (dsi in listDrugShopItemsSelected) {
                if (dsi.drug.idDrug == dSI.drug.idDrug) {
                    dsi.quantity = dSI.quantity
                    break
                }
            }
        }
        if (aDSI != null) {
            for (adsi in listAlterDrugShopItemsSelected) {
                if (adsi.alternativeDrug.idAlternativeDrug == aDSI.alternativeDrug.idAlternativeDrug) {
                    adsi.quantity = aDSI.quantity
                    break
                }
            }
        }
        checkEnableBtn()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData(isChecked: Boolean, position: Int, dSI: DrugShopItem ?= null, aDSI: AlternativeDrugShopItem ?= null) {
        if (!binding.rvBuyGoodDrugsPreStep.isComputingLayout) {
            listDrugShopItems[position].isChecked = isChecked
            //adapter.updateData(listDrugShopItems)
            //adapter.notifyItemChanged(position)
            //adapter.notifyDataSetChanged()
            if (dSI != null) {
                if (isChecked) {
                    listDrugShopItemsSelected.add(dSI)
                } else {
                    val newListDrugShopItemsSelected = listDrugShopItemsSelected.filter { it.drug.idDrug != dSI.drug.idDrug }
                    listDrugShopItemsSelected.clear()
                    listDrugShopItemsSelected.addAll(newListDrugShopItemsSelected)
                }
            }
            if (aDSI != null) {
                if (isChecked) {
                    listAlterDrugShopItemsSelected.add(aDSI)
                } else {
                    val newListAlterDrugShopItemsSelected = listAlterDrugShopItemsSelected.filter { it.alternativeDrug.idAlternativeDrug != aDSI.alternativeDrug.idAlternativeDrug }
                    listAlterDrugShopItemsSelected.clear()
                    listAlterDrugShopItemsSelected.addAll(newListAlterDrugShopItemsSelected)

                }
            }
            checkEnableBtn()
        }
    }
}