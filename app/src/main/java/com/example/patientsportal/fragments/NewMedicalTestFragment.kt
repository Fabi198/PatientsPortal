package com.example.patientsportal.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.patientsportal.R
import com.example.patientsportal.adapters.customAlertDialogAdapters.CustomAlertDialogMedicalTypesAdapter
import com.example.patientsportal.databinding.FragmentNewMedicalTestBinding
import com.example.patientsportal.databinding.CustomAlertDialogCoveragesFromPatientBinding
import com.example.patientsportal.db.arrays.ArrayMedicalTests.arrayMedicalTest
import com.google.android.material.card.MaterialCardView
import java.util.ArrayList
import java.util.Calendar


class NewMedicalTestFragment : Fragment(R.layout.fragment_new_medical_test) {

    private lateinit var binding: FragmentNewMedicalTestBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewMedicalTestBinding.bind(view)

        binding.cvNewMedicalTestDate.setOnClickListener { openCalendar() }

        binding.cvNewMedicalTestType.setOnClickListener { openTypeDialog(binding.spinnerNewMedicalTestType, binding.cvNewMedicalTestOtherType) }


    }

    private fun openTypeDialog(spinnerNewMedicalTestType: EditText, cvNewMedicalTestOtherType: MaterialCardView) {
        var medicalTestType = ""
        val binding = CustomAlertDialogCoveragesFromPatientBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .setOnDismissListener { spinnerNewMedicalTestType.setText(medicalTestType) }
            .create()
        val adapter = CustomAlertDialogMedicalTypesAdapter(arrayListOf("Biopsia", "Electrocardiograma", "Ecografía", "Radiografía", "Mamografía", "Tomografía Computada", "Resonancia Magnética", "Análisis de laboratorio", "Cultivo", "Pap", "Telemedicina", "Otros")) {
            medicalTestType = it
            if (it == "Otros") { showAndSetOtherTypeEditText() } else { if (cvNewMedicalTestOtherType.visibility == View.VISIBLE) { cvNewMedicalTestOtherType.visibility = View.GONE } }
            alertDialog.dismiss()
        }
        binding.rvSpinnerItems.adapter = adapter
        binding.cvCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun showAndSetOtherTypeEditText() {
        val listAllMedicalTest = ArrayList<String>()
        arrayMedicalTest.forEach { listAllMedicalTest.add(it.name) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listAllMedicalTest)
        binding.tvNewMedicalTestOtherType.threshold = 0
        binding.tvNewMedicalTestOtherType.setAdapter(adapter)
        binding.cvNewMedicalTestOtherType.visibility = View.VISIBLE
    }


    private fun openCalendar() {
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
                binding.tvNewMedicalTestDatePicker.text = fecha
            }, yearGetter, monthGetter, dayGetter)
        dpd.datePicker.maxDate = cal.timeInMillis
        dpd.show()
    }

}