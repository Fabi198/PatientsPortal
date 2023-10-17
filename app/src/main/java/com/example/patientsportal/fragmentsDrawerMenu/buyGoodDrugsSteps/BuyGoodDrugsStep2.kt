package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.databinding.CustomAlertDialogNewAddressBinding
import com.example.patientsportal.databinding.FragmentBuyGoodDrugsStep2Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Address
import com.example.patientsportal.objects.DateConverter.dateForBuyGoodDrugs
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.HideKeyboard.hideKeyboardOnFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class BuyGoodDrugsStep2 : Fragment(R.layout.fragment_buy_good_drugs_step2) {

    private lateinit var binding: FragmentBuyGoodDrugsStep2Binding
    private lateinit var address: Address
    private lateinit var cellphone: String
    private var b = false
    private var datePicked = ""
    private lateinit var dbPatientsPortal: DbPatientsPortal
    lateinit var onBuyGoodDrugsStepsClicks: OnBuyGoodDrugsStepsClicks

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyGoodDrugsStep2Binding.bind(view)

        lifecycleScope.launch(Dispatchers.IO) {
            setSpinnerPlaces()
            dbPatientsPortal = DbPatientsPortal(requireContext())
            address = dbPatientsPortal.readAddressFromPatient(getPatient(requireActivity(), requireContext()).idPatient)
            cellphone = dbPatientsPortal.readPhonesFromPatient(getPatient(requireActivity(), requireContext()).idPatient).cellphone
            withContext(Dispatchers.Main) {
                if (address.patient.idPatient != 0) {
                    changeAddress(address)
                }
                if (cellphone != "") {
                    binding.etCellPhoneShipping.setText(cellphone)
                    checkNextStepAvailable(1)
                    binding.etCellPhoneTakeAway.setText(cellphone)
                    checkNextStepAvailable(2)
                }
            }
        }


        binding.etCellPhoneShipping.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboardOnFragment(requireActivity(), binding.rlMain)
                checkNextStepAvailable(1)
                return@setOnKeyListener true }
            false
        }

        binding.etCellPhoneTakeAway.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboardOnFragment(requireActivity(), binding.rlMain)
                checkNextStepAvailable(2)
                return@setOnKeyListener true }
            false
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.btnShipping.id -> { binding.llTakeAway.visibility = View.GONE; binding.llShipping.visibility = View.VISIBLE; checkNextStepAvailable(1) }
                binding.btnTakeAway.id -> { binding.llShipping.visibility = View.GONE; binding.llTakeAway.visibility = View.VISIBLE; checkNextStepAvailable(2) }
            }
        }

        binding.radioGroupHour.setOnCheckedChangeListener { _, _ -> checkNextStepAvailable(1) }

        binding.btnAddNewAddress.setOnClickListener { showNewAddressDialog() }


        binding.etDeliverDateShipping.setOnClickListener { openCalendar(binding.etDeliverDateShipping, 1) }
        binding.etDeliverDateTakeAway.setOnClickListener { openCalendar(binding.etDeliverDateTakeAway, 2) }

        binding.btnNextStep.setOnClickListener {
            if (b) {
                val sot = binding.llShipping.visibility == View.VISIBLE
                val place = if (sot) { binding.tvAddress.text.toString() } else { binding.spinnerPlaces.selectedItem.toString() }
                val cellphone = if (sot) { binding.etCellPhoneShipping.text.toString() } else  { binding.etCellPhoneTakeAway.text.toString() }
                val hour = if (binding.btnMorning.isChecked) { binding.btnMorning.text.toString() } else { binding.btnAfternoon.text.toString() }
                onBuyGoodDrugsStepsClicks.onClickNextStep2To3(sot, place, cellphone, hour, datePicked)
            }
        }

    }

    private fun checkNextStepAvailable(sot: Int): Boolean {
        when (sot) {
            1 -> {
                b = binding.tvAddress.text != "" && (binding.etCellPhoneShipping.text.toString() != "" && binding.etCellPhoneShipping.text.length > 8) && (binding.btnMorning.isChecked || binding.btnAfternoon.isChecked) && binding.etDeliverDateShipping.text.toString() != ""
            }
            2 -> {
                b = binding.spinnerPlaces.selectedItemPosition > 0 && (binding.etCellPhoneTakeAway.text.toString() != "" && binding.etCellPhoneTakeAway.text.length > 8) && binding.etDeliverDateTakeAway.text.toString() != ""
            }
        }
        binding.btnNextStep.alpha = if (b) { 1F } else { 0.5F }
        return b
    }

    private fun setSpinnerPlaces() {
        with(binding.spinnerPlaces) {
            val arrayOptions = arrayOf("Seleccionar centro médico", "San Isidro", "Avellaneda", "Caseros", "Olivos", "Ramos Mejia", "San Martin", "Villa Urquiza", "Lomas De Zamora", "Morón", "Belgrano Norte 2", "Belgrano (Virrey Del Pino)")
            val adapterFirstSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOptions)
            adapterFirstSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = adapterFirstSpinner
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { selectedItem.toString(); checkNextStepAvailable(2) }
            }
        }
    }

    private fun showNewAddressDialog() {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val binding = CustomAlertDialogNewAddressBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .setOnDismissListener { checkNextStepAvailable(1) }
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        binding.tvEditStreet.setText(address.street)
        binding.tvEditNumber.setText(address.number)
        binding.tvEditFloor.setText(address.floor)
        binding.tvEditDepartment.setText(address.department)
        binding.tvEditObservations.setText(address.observations)
        binding.postalCodeShippingAddress.setText(address.postalCode)
        binding.tvDisplayedCountryW.text = address.country
        binding.postalCodeShippingAddress.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboardOnFragment(requireActivity(), binding.clWritingView)
                setSpinnerProvince(binding, binding.postalCodeShippingAddress.text.toString(), binding.spinnerProvinceShippingAddress)
                return@setOnKeyListener true }
            false
        }
        binding.postalCodeShippingAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty()) {
                        if (text.length == 4) {
                            hideKeyboardOnFragment(requireActivity(), binding.clWritingView)
                            setSpinnerProvince(binding, binding.postalCodeShippingAddress.text.toString(), binding.spinnerProvinceShippingAddress)
                        }
                    }
                }
            }
        })
        setKnownProvinceSpinner(binding.spinnerProvinceShippingAddress, address.province)
        setKnownLocalitySpinner(binding.spinnerLocalityShippingAddress, address.locality)
        binding.cbLocalityShippingNotFound.setOnClickListener { notFoundShippingLocality(binding) }

        binding.btnSaveForUpcomingOrders.setOnCheckedChangeListener { _, isChecked ->
            binding.tvReferenceNameW.visibility = if (isChecked) { View.VISIBLE } else { View.GONE }
            binding.cvEditReferenceNameW.visibility = if (isChecked) { View.VISIBLE } else { View.GONE }
            binding.separatorReferenceNameW.visibility = if (isChecked) { View.VISIBLE } else { View.GONE }
        }
        binding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val originalStreet = address.street
                val originalNumber = address.number
                val originalFloor = address.floor
                val originalDepartment = address.department
                val originalObservations = address.observations
                val originalPostalCode = address.postalCode
                val originalProvince = address.province
                val originalLocality = address.locality
                address.street = binding.tvEditStreet.text.toString()
                address.number = binding.tvEditNumber.text.toString()
                address.floor = binding.tvEditFloor.text.toString()
                address.department = binding.tvEditDepartment.text.toString()
                address.observations = binding.tvEditObservations.text.toString()
                address.postalCode = binding.postalCodeShippingAddress.text.toString()
                address.province = binding.spinnerProvinceShippingAddress.selectedItem.toString()
                address.locality = if (binding.localityShippingAddress.text.toString() != "") {
                    binding.localityShippingAddress.text.toString()
                } else {
                    binding.spinnerLocalityShippingAddress.selectedItem.toString()
                }

                val editAddressPatient = dbPatientsPortal.editAddress(address)
                withContext(Dispatchers.Main) {
                    if (editAddressPatient) {
                        changeAddress(address)
                        alertDialog.dismiss()
                    } else {
                        address.street = originalStreet
                        address.number = originalNumber
                        address.floor = originalFloor
                        address.department = originalDepartment
                        address.observations = originalObservations
                        address.postalCode = originalPostalCode
                        address.province = originalProvince
                        address.locality = originalLocality
                        Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun changeAddress(address: Address) {
        binding.tvAddress.text = "${address.street} ${address.number}\n${address.province}, ${address.country}"
        binding.tvAddress.visibility = View.VISIBLE
        binding.radioGroupHour.visibility = View.VISIBLE
    }

    private fun openCalendar(et: EditText, sot: Int) {
        hideKeyboardOnFragment(requireActivity(), binding.rlMain)
        val cal: Calendar = Calendar.getInstance()
        val yearGetter = cal.get(Calendar.YEAR)
        val monthGetter = cal.get(Calendar.MONTH)
        val dayGetter = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), 0,
            { _, year, month, dayOfMonth ->
                lateinit var fecha: String
                if ((month+1) in 0..9 && dayOfMonth in 10..31) {
                    fecha = "$year/0${month+1}/$dayOfMonth"
                } else if ((month+1) in 0..9 && dayOfMonth in 0..9) {
                    fecha = "$year/0${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 0..9) {
                    fecha = "$year/${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 10..31) {
                    fecha = "$year/${month+1}/$dayOfMonth"
                }
                et.setText(dateForBuyGoodDrugs(fecha))
                datePicked = fecha
                checkNextStepAvailable(sot)
            }, yearGetter, monthGetter, dayGetter)
        val minDate = cal.clone() as Calendar
        minDate.add(Calendar.DAY_OF_MONTH, 3)
        val maxDate = cal.clone() as Calendar
        maxDate.add(Calendar.DAY_OF_MONTH, 9)
        dpd.datePicker.minDate = minDate.timeInMillis
        dpd.datePicker.maxDate = maxDate.timeInMillis
        dpd.show()
    }


    private fun notFoundShippingLocality(customAlertDialogNewAddressBinding: CustomAlertDialogNewAddressBinding) {
        if (customAlertDialogNewAddressBinding.cbLocalityShippingNotFound.isChecked) {
            setSpinnerAllProvinces(customAlertDialogNewAddressBinding.spinnerProvinceShippingAddress)
            customAlertDialogNewAddressBinding.spinnerLocalityShippingAddress.visibility = View.GONE
            customAlertDialogNewAddressBinding.localityShippingAddress.setTextColor(resources.getColor(R.color.blue, null))
            customAlertDialogNewAddressBinding.localityShippingAddress.visibility = View.VISIBLE
        } else {
            customAlertDialogNewAddressBinding.spinnerLocalityShippingAddress.visibility = View.VISIBLE
            customAlertDialogNewAddressBinding.localityShippingAddress.visibility = View.GONE
        }
    }
    private fun setKnownProvinceSpinner(spinner: Spinner, provinceName: String) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf(provinceName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }
    private fun setKnownLocalitySpinner(spinner: Spinner, cityName: String) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf(cityName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }
    private fun setSpinnerAllProvinces(spinner: Spinner) {
        val list = ArrayList<String>()
        dbPatientsPortal.readAllProvinces().forEach { list.add(it.name) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { spinner.selectedItem.toString() }
        }
    }
    private fun getArrayForAdapterProvinceSpinner(postalCode: String): ArrayList<String> {
        val list: ArrayList<String> = dbPatientsPortal.readProvinceForSpinner(postalCode)
        if (list.size == 0) { Toast.makeText(requireContext(), "No existe el codigo postal", Toast.LENGTH_SHORT).show() }
        return list
    }
    private fun getAdapterForProvinceSpinner(postalCode: String): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, getArrayForAdapterProvinceSpinner(postalCode))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    private fun setSpinnerProvince(customAlertDialogNewAddressBinding: CustomAlertDialogNewAddressBinding, postalCode: String, spinner: Spinner) {
        spinner.adapter = getAdapterForProvinceSpinner(postalCode)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setLocalitySpinner(customAlertDialogNewAddressBinding.spinnerLocalityShippingAddress, postalCode, spinner.selectedItem.toString())
            }
        }
    }
    private fun getArrayForAdapterLocalitySpinner(postalCode: String, provinceName: String): ArrayList<String> {
        return dbPatientsPortal.readCitysForSpinner(postalCode, provinceName)
    }
    private fun setAdapterForLocalitySpinner(postalCode: String, provinceName: String): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, getArrayForAdapterLocalitySpinner(postalCode, provinceName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    private fun setLocalitySpinner(spinner: Spinner, postalCode: String, provinceName: String) {
        spinner.adapter = setAdapterForLocalitySpinner(postalCode, provinceName)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { spinner.selectedItem.toString() }
        }
    }

}