package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStepsHolderBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce


@Suppress("DEPRECATION")
class BuyGoodDrugsStepsHolder : Fragment(R.layout.fragment_buy_good_drugs_steps_holder), OnBuyGoodDrugsStepsClicks {

    private lateinit var binding: FragmentBuyGoodDrugsStepsHolderBinding
    private lateinit var listAlterOrDrugShopItemParce: ArrayList<AlterOrDrugShopItemParce>
    private var counter = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private var secondsRemaining = 10 * 60 // 10 minutos en segundos

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStepsHolderBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        listAlterOrDrugShopItemParce = arguments?.getParcelableArrayList<AlterOrDrugShopItemParce>(getString(R.string.alterordrug_tag)) as ArrayList<AlterOrDrugShopItemParce>
        binding.btnClose.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }


        if (idContainer != null) {
            showFirstStep()
        }
    }


    private fun showFirstStep() {
        val buyGoodDrugsStep1 = BuyGoodDrugsStep1()
        buyGoodDrugsStep1.onBuyGoodDrugsStepsClicks = this
        val bundle = Bundle()
        bundle.putParcelableArrayList(getString(R.string.alterordrug_tag), listAlterOrDrugShopItemParce)
        buyGoodDrugsStep1.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().add(binding.containerSteps.id, buyGoodDrugsStep1, getString(R.string.buygooddrugsstep1_tag)).commit()
    }

    private fun showSecondStep() {
        val buyGoodDrugsStep2 = BuyGoodDrugsStep2()
        buyGoodDrugsStep2.onBuyGoodDrugsStepsClicks = this
        val bundle = Bundle()
        bundle.putParcelableArrayList(getString(R.string.alterordrug_tag), listAlterOrDrugShopItemParce)
        buyGoodDrugsStep2.arguments = bundle
        showNewFragmentStep(buyGoodDrugsStep2, getString(R.string.buygooddrugsstep2_tag), getString(R.string.paso_2_5))
    }

    private fun showThirdStep(sot: Boolean, place: String, cellphone: String, hour: String, date: String) {
        var newPlace = ""
        if (!sot)  {
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            newPlace = dbPatientsPortal.readPlaceByLocality(place).address
        }
        val buyGoodDrugsStep3 = BuyGoodDrugsStep3()
        buyGoodDrugsStep3.onBuyGoodDrugsStepsClicks = this
        val bundle = Bundle()
        bundle.putParcelableArrayList(getString(R.string.alterordrug_tag), listAlterOrDrugShopItemParce)
        bundle.putBoolean(getString(R.string.shippingortakeaway_tag), sot)
        if (sot) { bundle.putString(getString(R.string.place_tag), place) } else { bundle.putString(getString(R.string.place_tag), newPlace) }
        bundle.putString(getString(R.string.cellphone_tag), cellphone)
        bundle.putString(getString(R.string.hour_tag), hour)
        bundle.putString(getString(R.string.date_tag), date)
        buyGoodDrugsStep3.arguments = bundle
        showNewFragmentStep(buyGoodDrugsStep3, getString(R.string.buygooddrugsstep3_tag), getString(R.string.paso_3_5))
    }

    private fun showFourthStep() {
        val buyGoodDrugsStep4 = BuyGoodDrugsStep4()
        buyGoodDrugsStep4.onBuyGoodDrugsStepsClicks = this
        val bundle = Bundle()
        //bundle.putParcelableArrayList(getString(R.string.alterordrug_tag), listAlterOrDrugShopItemParce)
        buyGoodDrugsStep4.arguments = bundle
        showNewFragmentStep(buyGoodDrugsStep4, getString(R.string.buygooddrugsstep4_tag), getString(R.string.paso_4_5))
    }

    private fun showFifthStep() {
        Handler().postDelayed({
            showNewFragmentStep(BuyGoodDrugsStep5(), getString(R.string.buygooddrugsstep5_tag), getString(R.string.paso_5_5))
        }, 500)
    }

    private fun showNewFragmentStep(fragment: Fragment, tag: String, stepNumber: String) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.right_in,
                R.anim.left_out,
                R.anim.right_in,
                R.anim.left_out)
            .replace(binding.containerSteps.id, fragment, tag)
            .commit()
        binding.tvStepNumber.text = stepNumber
        when (stepNumber) {
            getString(R.string.paso_2_5) -> {
                binding.tvTitle.text = getString(R.string.lugar_de_entrega)
                binding.cvShoppingCart.visibility = View.INVISIBLE
            }
            getString(R.string.paso_3_5) -> {
                binding.tvTitle.text = getString(R.string.resumen_de_compra)
            }
            getString(R.string.paso_4_5) -> {
                binding.tvTitle.text = getString(R.string.c_mo_quer_s_pagar)
            }
            getString(R.string.paso_5_5) -> {
                binding.tvTitle.text = getString(R.string.compra_confirmada)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private val timerRunnable = object : Runnable {
        @SuppressLint("UseCompatLoadingForDrawables")
        override fun run() {
            if (isTimerRunning) {
                if (secondsRemaining > 0) {
                    if (secondsRemaining < 120 && counter == 0) {
                        binding.cvTimer.setCardBackgroundColor(resources.getColor(R.color.redSuperLight, null))
                        val drawable = resources.getDrawable(R.drawable.baseline_error_outline_24)
                        binding.tvTimer.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                        counter++
                    }
                    // Actualiza la UI con el tiempo restante en el formato deseado
                    val minutes = secondsRemaining / 60
                    val seconds = secondsRemaining % 60
                    val timeRemaining = String.format("%02d:%02d min", minutes, seconds)
                    binding.tvTimer.text = setBoldPartText(getString(R.string.tiempo_restante), " $timeRemaining")

                    // Resta 1 segundo del tiempo restante
                    secondsRemaining--

                    // Programa la próxima ejecución de esta función después de 1 segundo
                    handler.postDelayed(this, 1000)
                } else {
                    // El temporizador ha terminado
                    isTimerRunning = false
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            handler.post(timerRunnable)
        }
    }

    private fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    override fun onClickNextStep2To3(sot: Boolean, place: String, cellphone: String, hour: String, date: String) {
        showThirdStep(sot, place, cellphone, hour, date)
    }

    override fun onQueryForShoppingCartItemsQuantity(size: Int) {
        binding.tvCantShoppingCart.text = size.toString()
    }


    override fun onDeleteDrugShopItem(newSize: Int) {
        binding.tvCantShoppingCart.text = newSize.toString()
    }

    override fun onClickSelectPlace() {
        showSecondStep()
    }

    override fun onClickAddMoreProducts() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onClickStartPayment() {
        showFourthStep()
    }

    override fun onClickCancelOrder() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onClickConfirmPayment() {
        showFifthStep()
    }

    private fun setBoldPartText(normalText: String, boldText: String): SpannableStringBuilder {
        val spannableString = SpannableString(boldText)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, boldText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(normalText).append(spannableString)
        return spannableStringBuilder
    }

}