package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.CreditCardsAdapter
import com.example.patientsportal.databinding.CustomAlertDialogNewCreditCardBinding
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStep4Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BuyGoodDrugsStep4 : Fragment(R.layout.fragment_buy_good_drugs_step4) {

    private lateinit var binding: FragmentBuyGoodDrugsStep4Binding
    lateinit var onBuyGoodDrugsStepsClicks: OnBuyGoodDrugsStepsClicks
    private lateinit var adapter: CreditCardsAdapter
    private var selectedCard = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStep4Binding.bind(view)
        selectedCard = false

        lifecycleScope.launch(Dispatchers.IO) {
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            val listCreditCards = dbPatientsPortal.readAllCreditCardsFromPatient(getPatient(requireActivity(), requireContext()).idPatient)
            withContext(Dispatchers.Main) {
                if (listCreditCards.size > 0) {
                    adapter = CreditCardsAdapter(listCreditCards, requireContext(), -1) { _, i ->
                        updateData(i)
                        selectedCard = true
                    }
                    binding.rvCreditCards.adapter = adapter
                    binding.rvCreditCards.visibility = View.VISIBLE
                }
            }
        }



        binding.btnNextStep.setOnClickListener { if (selectedCard) { onBuyGoodDrugsStepsClicks.onClickConfirmPayment() } }
        binding.btnCancel.setOnClickListener { onBuyGoodDrugsStepsClicks.onClickCancelOrder() }
        binding.btnAddNewCreditCard.setOnCheckedChangeListener { _, isChecked -> if (isChecked) { showCreditCardDialog(binding.btnAddNewCreditCard) } }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData(position: Int) {
        if (!binding.rvCreditCards.isComputingLayout) {
            adapter.updateData(position)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showCreditCardDialog(rb: RadioButton) {
        var cardBrand = ""
        val binding = CustomAlertDialogNewCreditCardBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .setOnDismissListener { rb.isChecked = false }
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        binding.btnVISA.setOnClickListener {
            cardBrand = getString(R.string.visa)
            binding.btnVISA.setImageResource(R.drawable.card_credit_visa)
            binding.btnMaster.setImageResource(R.drawable.card_credit_master_bw)
            binding.btnAmex.setImageResource(R.drawable.card_credit_american_bw)
            val editTextFiltersVISA = arrayOf(InputFilter.LengthFilter(3))
            binding.etCardCVV.filters = editTextFiltersVISA
            binding.cvCardInfoView.visibility = View.VISIBLE
        }
        binding.btnMaster.setOnClickListener {
            cardBrand = getString(R.string.master)
            binding.btnMaster.setImageResource(R.drawable.card_credit_master)
            binding.btnVISA.setImageResource(R.drawable.card_credit_visa_bw)
            binding.btnAmex.setImageResource(R.drawable.card_credit_american_bw)
            val editTextFiltersMASTER = arrayOf(InputFilter.LengthFilter(3))
            binding.etCardCVV.filters = editTextFiltersMASTER
            binding.cvCardInfoView.visibility = View.VISIBLE
        }
        binding.btnAmex.setOnClickListener {
            cardBrand = getString(R.string.amex)
            binding.btnAmex.setImageResource(R.drawable.card_credit_american)
            binding.btnMaster.setImageResource(R.drawable.card_credit_master_bw)
            binding.btnVISA.setImageResource(R.drawable.card_credit_visa_bw)
            val editTextFiltersAMEX = arrayOf(InputFilter.LengthFilter(4))
            binding.etCardCVV.filters = editTextFiltersAMEX
            binding.cvCardInfoView.visibility = View.VISIBLE
        }
        binding.etCardExpiration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verifica si se han ingresado los dos dígitos.
                if (count == 1 && start == 1) {
                    val monthDigits = s?.subSequence(0, 2)?.toString()?.toIntOrNull()
                    if (!(monthDigits != null && monthDigits >= 1 && monthDigits <= 12)) {
                        binding.etCardExpiration.error = "Mes inválido"
                    }
                }
                // Verifica si el tercer dígito ha sido ingresado y agrega "/" automáticamente.
                if (count == 1 && start == 2) {
                    val newText = s?.let { StringBuilder(it) }
                    newText?.insert(2, "/")
                    binding.etCardExpiration.setText(newText)
                    binding.etCardExpiration.setSelection(binding.etCardExpiration.text.length)
                }

                // Verifica si se ha borrado el primer dígito del año y elimina la "/" automáticamente.
                if (before == 1 && start == 3) {
                    val newText = s?.let { StringBuilder(it) }
                    newText?.deleteCharAt(2) // Elimina la barra "/"
                    binding.etCardExpiration.setText(newText)
                    binding.etCardExpiration.setSelection(binding.etCardExpiration.text.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.btnNextStep.setOnClickListener { 
            if (binding.etCardOwner.text.toString().isNotEmpty() &&
                binding.etCardNumber.text.toString().length == 16 &&
                binding.etCardExpiration.text.toString().length == 5 &&
                binding.etCardCVV.text.toString().length >= 3 &&
                binding.etDocNumber.text.toString().length > 7) {
                if (binding.btnSaveCard.isChecked) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val dbPatientsPortal = DbPatientsPortal(requireContext())
                        dbPatientsPortal.createPatientCreditCard(
                            getPatient(requireActivity(), requireContext()).idPatient,
                            cardBrand,
                            binding.etCardOwner.text.toString(),
                            binding.etCardNumber.text.toString(),
                            binding.etCardExpiration.text.toString(),
                            binding.etCardCVV.text.toString(),
                            binding.etDocNumber.text.toString()
                        )
                        withContext(Dispatchers.Main) {
                            onBuyGoodDrugsStepsClicks.onClickConfirmPayment()
                            alertDialog.dismiss()
                        }
                    }
                } else {
                    onBuyGoodDrugsStepsClicks.onClickConfirmPayment()
                    alertDialog.dismiss()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.campos_incompletos), Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.show()
    }

}