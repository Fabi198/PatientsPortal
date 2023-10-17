package com.example.patientsportal.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentNewHealthControlBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.HCArterialPressure
import com.example.patientsportal.entities.dbEntities.HCBreathFrequency
import com.example.patientsportal.entities.dbEntities.HCBreathlessness
import com.example.patientsportal.entities.dbEntities.HCGlucose
import com.example.patientsportal.entities.dbEntities.HCHeartFrequency
import com.example.patientsportal.entities.dbEntities.HCOxygenSaturation
import com.example.patientsportal.entities.dbEntities.HCTemperature
import com.example.patientsportal.entities.dbEntities.HCWeightAndHeight
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.HideKeyboard.hideKeyboardOnFragment
import com.example.patientsportal.objects.HideKeyboard.showKeyboardOnFragment
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar


class NewHealthControlFragment : Fragment(R.layout.fragment_new_health_control) {

    private lateinit var binding: FragmentNewHealthControlBinding
    private var phrasePlayer1Error = false
    private var enabledClean = false
    private var enabledSave = false
    private var completedFirstEdit = false
    private var completedSecondEdit = false
    private var completedSpinnerOne = false
    private var completedSpinnerTwo = false
    private var completedSpinnerThree = false
    private var completedDateText = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewHealthControlBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val title = arguments?.getString(getString(R.string.title_tag))

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        if (idContainer != null && title != null) {
            setHeader(title)
            setView(title)
            setDateInputConfiguration()
            binding.btnCleanSelection.setOnClickListener { setCleanActions() }
            binding.btnSaveChange.setOnClickListener { setSaveActions(title) }
        }

    }

    private fun setView(title: String) {
        when (title) {
            getString(R.string.weightandheight_tag) -> { setFirstInputConfiguration(title); setSecondInputConfiguration(title) }
            getString(R.string.temperature_tag) -> { setFirstInputConfiguration(title); setFirstSpinner(title) }
            getString(R.string.heartfrequency_tag) -> { setFirstInputConfiguration(title); setFirstSpinner(title) }
            getString(R.string.arterialpressure_tag) -> { setFirstInputConfiguration(title); setSecondInputConfiguration(title); setFirstSpinner(title); setSecondSpinner(title); setThirdSpinner(title) }
            getString(R.string.glucose_tag) -> { setFirstInputConfiguration(title); setFirstSpinner(title) }
            getString(R.string.breathfrequency_tag) -> { setFirstInputConfiguration(title) }
            getString(R.string.oxygensaturation_tag) -> { setFirstInputConfiguration(title); setFirstSpinner(title) }
            getString(R.string.breathlessness_tag) -> { setFirstInputConfiguration(title) }
            else -> {  }
        }

    }

    private fun setHeader(title: String) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val query = dbPatientsPortal.readAllHealthControlsRegistersByHC(getPatient(requireActivity(), requireContext()), title)
        if (query.size > 0) {
            val lastHealthControl = query[query.size-1]
            binding.tvLastResult.text = when (title) {
                getString(R.string.weightandheight_tag) -> { if (lastHealthControl is HCWeightAndHeight) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.weight} kg | ${lastHealthControl.height} mts" } else { "" } }
                getString(R.string.temperature_tag) -> { if (lastHealthControl is HCTemperature) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.temperature} °C" } else { "" } }
                getString(R.string.heartfrequency_tag) -> { if (lastHealthControl is HCHeartFrequency) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.heartFrequency} Lat/Min" } else { "" } }
                getString(R.string.arterialpressure_tag) -> { if (lastHealthControl is HCArterialPressure) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.lowArterialPressure}/${lastHealthControl.highArterialPressure} mmHg" } else { "" } }
                getString(R.string.glucose_tag) -> { if (lastHealthControl is HCGlucose) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.glucose} mg/dl" } else { "" } }
                getString(R.string.breathfrequency_tag) -> { if (lastHealthControl is HCBreathFrequency) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.breathFrequency} Res/Min" } else { "" } }
                getString(R.string.oxygensaturation_tag) -> { if (lastHealthControl is HCOxygenSaturation) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.oxygenSaturation} %" } else { "" } }
                getString(R.string.breathlessness_tag) -> { if (lastHealthControl is HCBreathlessness) { "${getString(R.string.ltimo_valor_cargado)} ${lastHealthControl.breathlessness} (X/10)" } else { "" } }
                else -> { "" }
            }
        } else {
            binding.tvLastResult.text = when (title) {
                getString(R.string.weightandheight_tag) -> { "${getString(R.string.ltimo_valor_cargado)} kg | mts" }
                getString(R.string.temperature_tag) -> { "${getString(R.string.ltimo_valor_cargado)} °C" }
                getString(R.string.heartfrequency_tag) -> { "${getString(R.string.ltimo_valor_cargado)} Lat/Min" }
                getString(R.string.arterialpressure_tag) -> { "${getString(R.string.ltimo_valor_cargado)} mmHg" }
                getString(R.string.glucose_tag) -> { "${getString(R.string.ltimo_valor_cargado)} mg/dl" }
                getString(R.string.breathfrequency_tag) -> { "${getString(R.string.ltimo_valor_cargado)} Res/Min" }
                getString(R.string.oxygensaturation_tag) -> { "${getString(R.string.ltimo_valor_cargado)} %" }
                getString(R.string.breathlessness_tag) -> { "${getString(R.string.ltimo_valor_cargado)} (X/10)" }
                else -> { "" }
            }
        }

        binding.tvTitle.text = when (title) {
            getString(R.string.weightandheight_tag) -> { getString(R.string.peso_y_altura) }
            getString(R.string.temperature_tag) -> { getString(R.string.temp) }
            getString(R.string.heartfrequency_tag) -> { getString(R.string.frecuencia_cardiaca) }
            getString(R.string.arterialpressure_tag) -> { getString(R.string.presion_arterial) }
            getString(R.string.glucose_tag) -> { getString(R.string.glucemia) }
            getString(R.string.breathfrequency_tag) -> { getString(R.string.frecuencia_respiratoria) }
            getString(R.string.oxygensaturation_tag) -> { getString(R.string.saturacion_de_oxigeno) }
            getString(R.string.breathlessness_tag) -> { getString(R.string.disnea) }
            else -> { "" }
        }
    }

    private fun setFirstInputConfiguration(title: String) {
        binding.tvFirstInput.visibility = View.VISIBLE
        binding.separatorFirstUnit.visibility = View.VISIBLE
        binding.tvFirstInput.hint = when (title) {
            getString(R.string.weightandheight_tag) -> { "Peso (kg)" }
            getString(R.string.temperature_tag) -> { "Temperatura (°C)" }
            getString(R.string.heartfrequency_tag) -> { "Valor (Lat/Min)" }
            getString(R.string.arterialpressure_tag) -> { "Alta (mmHg)" }
            getString(R.string.glucose_tag) -> { "Valor (mg/dl)" }
            getString(R.string.breathfrequency_tag) -> { "N° de respiraciones por minuto" }
            getString(R.string.oxygensaturation_tag) -> { "Valor (%)" }
            getString(R.string.breathlessness_tag) -> { "Disnea (x/10)" }
            else -> { "" }
        }
        binding.tvFirstEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tvFirstEdit.hint = when (title) {
                    getString(R.string.weightandheight_tag) -> { "Mayor a 0.5 y menor a 300 kgs" }
                    getString(R.string.temperature_tag) -> { "Valor entre 34 y 45 °C" }
                    getString(R.string.heartfrequency_tag) -> { "El valor debe estar entre 30 y 250 Lat/Min" }
                    getString(R.string.arterialpressure_tag) -> { "El valor debe estar entre 50 y 300 mmHg" }
                    getString(R.string.glucose_tag) -> { "El valor debe estar entre 0 y 900 mg/dl" }
                    getString(R.string.breathfrequency_tag) -> { "El valor debe estar entre 10 y 50 Res/Min" }
                    getString(R.string.oxygensaturation_tag) -> { "El valor debe estar entre 0 y 100 %" }
                    getString(R.string.breathlessness_tag) -> { "El valor debe estar entre 0 y 10" }
                    else -> { "" }
                }
                showKeyboardOnFragment(requireActivity(), binding.tvFirstEdit)
            } else {
                binding.tvFirstEdit.hint = null
            }
        }

        binding.tvFirstEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty() && text.length > 1) {
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        if (areTheDataCorrect(binding.tvFirstInput, title, text)) {
                            completedFirstEdit = true
                            areWeDoneYet()
                            hidePhraseError(binding.tvFirstInput, binding.separatorFirstUnit)
                        } else {
                            completedFirstEdit = false
                            showPhraseError(binding.tvFirstInput, getString(R.string.error_en_el_dato_revise_e_ingreselo_nuevamente), binding.separatorFirstUnit)
                        }
                    } else {
                        completedFirstEdit = false
                        hidePhraseError(binding.tvFirstInput, binding.separatorFirstUnit)
                    }
                }
            }
        })
    }

    private fun setSecondInputConfiguration(title: String) {
        binding.tvSecondInput.visibility = View.VISIBLE
        binding.separatorSecondUnit.visibility = View.VISIBLE
        binding.tvSecondInput.hint = when (title) {
            getString(R.string.weightandheight_tag) -> { "Altura (Mts)" }
            getString(R.string.arterialpressure_tag) -> { "Baja (mmHg)" }
            else -> { "" }
        }
        binding.tvSecondEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tvSecondEdit.hint = when (title) {
                    getString(R.string.weightandheight_tag) -> { "Mayor a 0.4 y menor a 2.5 mts" }
                    getString(R.string.arterialpressure_tag) -> { "El valor debe estar entre 50 y 300 mmHg" }
                    else -> { "" }
                }
                showKeyboardOnFragment(requireActivity(), binding.tvFirstEdit)
            } else {
                binding.tvSecondEdit.hint = null
            }
        }

        binding.tvSecondEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty() && text.length > 1) {
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        if (areTheDataCorrect(binding.tvSecondInput, title, text)) {
                            completedSecondEdit = true
                            areWeDoneYet()
                            hidePhraseError(binding.tvSecondInput, binding.separatorSecondUnit)
                        } else {
                            completedSecondEdit = false
                            showPhraseError(binding.tvSecondInput, getString(R.string.error_en_el_dato_revise_e_ingreselo_nuevamente), binding.separatorSecondUnit)
                        }
                    } else {
                        completedSecondEdit = false
                        hidePhraseError(binding.tvSecondInput, binding.separatorSecondUnit)
                    }
                }
            }
        })
    }

    private fun setFirstSpinner(title: String) {
        binding.spinner1.visibility = View.VISIBLE
        binding.separatorFirstSpinner.visibility = View.VISIBLE
        val arrayOptions = when (title) {
            getString(R.string.temperature_tag) -> { arrayOf(getString(R.string.parte_del_cuerpo), getString(R.string.axilar), getString(R.string.bucal), getString(R.string.rectal_anal), getString(R.string.oido), getString(R.string.otro)) }
            getString(R.string.heartfrequency_tag) -> { arrayOf(getString(R.string.m_todo), getString(R.string.manual), getString(R.string.automatico)) }
            getString(R.string.arterialpressure_tag) -> { arrayOf(getString(R.string.parte_del_cuerpo), getString(R.string.brazo), getString(R.string.mu_eca)) }
            getString(R.string.glucose_tag) -> { arrayOf(getString(R.string.comi_en_las_ultimas_2_horas), getString(R.string.si), getString(R.string.no)) }
            getString(R.string.oxygensaturation_tag) -> { arrayOf(getString(R.string.ten_as_ox_geno_suplementario), getString(R.string.si), getString(R.string.no)) }
            else -> { arrayOf("") }
        }
        val adapterFirstSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOptions)
        adapterFirstSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner1) {
            adapter = adapterFirstSpinner
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        binding.spinner1.selectedItem.toString()
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        completedSpinnerOne = true
                        areWeDoneYet()
                        binding.separatorFirstSpinner.setBackgroundColor(resources.getColor(R.color.purple_200, null))
                    } else {
                        completedSpinnerOne = false
                        areWeDoneYet()
                        binding.separatorFirstSpinner.setBackgroundColor(resources.getColor(R.color.gray, null))
                    }

                }
            }
        }
    }

    private fun setSecondSpinner(title: String) {
        binding.spinner2.visibility = View.VISIBLE
        binding.separatorSecondSpinner.visibility = View.VISIBLE
        val arrayOptions = when (title) {
            getString(R.string.arterialpressure_tag) -> { arrayOf(getString(R.string.tipo_de_tensi_metro), getString(R.string.manual), getString(R.string.automatico)) }
            else -> { arrayOf("") }
        }
        val adapterSecondSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOptions)
        adapterSecondSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner2) {
            adapter = adapterSecondSpinner
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        binding.spinner2.selectedItem.toString()
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        completedSpinnerTwo = true
                        areWeDoneYet()
                        binding.separatorSecondSpinner.setBackgroundColor(resources.getColor(R.color.purple_200, null))
                    } else {
                        completedSpinnerTwo = false
                        areWeDoneYet()
                        binding.separatorSecondSpinner.setBackgroundColor(resources.getColor(R.color.gray, null))
                    }

                }
            }
        }
    }

    private fun setThirdSpinner(title: String) {
        binding.spinner3.visibility = View.VISIBLE
        binding.separatorThirdSpinner.visibility = View.VISIBLE
        val arrayOptions = when (title) {
            getString(R.string.arterialpressure_tag) -> { arrayOf(getString(R.string.persona_que_midi), getString(R.string.yo_familiar_amigo), getString(R.string.m_dico_enfermero), getString(R.string.otro)) }
            else -> { arrayOf("") }
        }
        val adapterThirdSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOptions)
        adapterThirdSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner3) {
            adapter = adapterThirdSpinner
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        binding.spinner3.selectedItem.toString()
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        completedSpinnerThree = true
                        areWeDoneYet()
                        binding.separatorThirdSpinner.setBackgroundColor(resources.getColor(R.color.purple_200, null))
                    } else {
                        completedSpinnerThree = false
                        areWeDoneYet()
                        binding.separatorThirdSpinner.setBackgroundColor(resources.getColor(R.color.gray, null))
                    }
                }
            }
        }
    }

    private fun setDateInputConfiguration() {
        binding.tvEditDate.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { openCalendar() } }
        binding.tvEditDate.setOnClickListener { openCalendar() }
        binding.tvEditDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty()) {
                        enabledClean = true
                        binding.btnCleanSelection.alpha = 1F
                        completedDateText = true
                        areWeDoneYet()
                        binding.separatorDate.setBackgroundColor(resources.getColor(R.color.purple_200, null))
                    } else {
                        completedDateText = false
                        hidePhraseError(binding.tvFirstInput, binding.separatorDate)
                    }
                }
            }
        })
    }

    private fun areTheDataCorrect(tvInput: TextInputLayout, title: String, text: String): Boolean {
        return when (title) {
            getString(R.string.weightandheight_tag) -> { when (tvInput) { binding.tvFirstInput -> { text.toDouble() in 0.5..300.0 } binding.tvSecondInput -> { text.toDouble() in 0.4..2.5 } else -> { false } } }
            getString(R.string.temperature_tag) -> { text.toDouble() in 34.0..45.0 }
            getString(R.string.heartfrequency_tag) -> { text.toDouble() in 30.0..250.0 }
            getString(R.string.arterialpressure_tag) -> { text.toDouble() in 50.0..300.0 }
            getString(R.string.glucose_tag) -> { text.toDouble() in 0.0..900.0 }
            getString(R.string.breathfrequency_tag) -> { text.toDouble() in 10.0..50.0 }
            getString(R.string.oxygensaturation_tag) -> { text.toDouble() in 0.0..100.0 }
            getString(R.string.breathlessness_tag) -> { text.toDouble() in 0.0..10.0 }
            else -> { false }
        }
    }

    private fun showPhraseError(tvInput: TextInputLayout, errorMsg: String, separator: ImageView) {
        tvInput.error = errorMsg
        phrasePlayer1Error = true
        separator.setBackgroundColor(resources.getColor(R.color.red, null))
    }

    private fun hidePhraseError(tvInput: TextInputLayout, separator: ImageView) {
        tvInput.error = null
        phrasePlayer1Error = false
        separator.setBackgroundColor(resources.getColor(R.color.purple_200, null))
    }

    private fun areWeDoneYet() {
        enabledSave = (((binding.tvFirstInput.visibility == View.VISIBLE && completedFirstEdit) || binding.tvFirstInput.visibility == View.GONE)
            && ((binding.tvSecondInput.visibility == View.VISIBLE && completedSecondEdit) || binding.tvSecondInput.visibility == View.GONE)
            && ((binding.spinner1.visibility == View.VISIBLE && completedSpinnerOne) || binding.spinner1.visibility == View.GONE)
            && ((binding.spinner2.visibility == View.VISIBLE && completedSpinnerTwo) || binding.spinner2.visibility == View.GONE)
            && ((binding.spinner3.visibility == View.VISIBLE && completedSpinnerThree) || binding.spinner3.visibility == View.GONE)
            && ((binding.tvInputDate.visibility == View.VISIBLE && completedDateText) || binding.tvInputDate.visibility == View.GONE))
        binding.btnSaveChange.alpha = if (enabledSave) { 1F } else { 0.5F }
        //Toast.makeText(requireContext(), "$completedFirstEdit\n$completedSecondEdit\n$completedSpinnerOne\n$completedSpinnerTwo\n$completedSpinnerThree\n$completedDateText", Toast.LENGTH_SHORT).show()
    }

    private fun setSaveActions(title: String) {
        if (enabledSave) {
            val patient = getPatient(requireActivity(), requireContext())
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            val hc = when (title) {
                getString(R.string.weightandheight_tag) -> { HCWeightAndHeight(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.tvSecondEdit.text.toString(), calculateIMC(binding.tvFirstEdit.text.toString().toDouble(), binding.tvSecondEdit.text.toString().toDouble()), getString(R.string.paciente)) }
                getString(R.string.temperature_tag) -> { HCTemperature(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.spinner1.selectedItem.toString(), getString(R.string.paciente)) }
                getString(R.string.heartfrequency_tag) -> { HCHeartFrequency(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.spinner1.selectedItem.toString(), getString(R.string.paciente)) }
                getString(R.string.arterialpressure_tag) -> { HCArterialPressure(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.tvSecondEdit.text.toString(), binding.spinner1.selectedItem.toString(), binding.spinner2.selectedItem.toString(), binding.spinner3.selectedItem.toString(), getString(R.string.paciente)) }
                getString(R.string.glucose_tag) -> { HCGlucose(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.spinner1.selectedItem.toString(), getString(R.string.paciente)) }
                getString(R.string.breathfrequency_tag) -> { HCBreathFrequency(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), getString(R.string.paciente)) }
                getString(R.string.oxygensaturation_tag) -> { HCOxygenSaturation(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), binding.spinner1.selectedItem.toString(), getString(R.string.paciente)) }
                getString(R.string.breathlessness_tag) -> { HCBreathlessness(0, patient, binding.tvEditDate.text.toString(), binding.tvFirstEdit.text.toString(), getString(R.string.paciente)) }
                else -> { Any() }
            }
            if (dbPatientsPortal.createHealthControlForPatient(hc, title) > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Hubo un error al guardar los datos, intente nuevamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateIMC(weight: Double, height: Double): String {
        val imc = weight / (height * height)
        val imcFormatted = String.format("%.2f", imc)

        val resultado: String = when {
            imc < 18.5 -> "Bajo peso"
            imc >= 18.5 && imc < 25.0 -> "Peso Normal"
            imc >= 25.0 && imc < 30.0 -> "Sobrepeso"
            else -> "Obesidad"
        }

        return "$imcFormatted\n$resultado"
    }

    private fun setCleanActions() {
        if (enabledClean) {
            binding.tvFirstEdit.text?.clear()
            binding.tvSecondEdit.text?.clear()
            binding.spinner1.setSelection(0)
            binding.spinner2.setSelection(0)
            binding.spinner3.setSelection(0)
            binding.tvEditDate.text?.clear()
            enabledClean = false
            !completedFirstEdit
            !completedSecondEdit
            !completedSpinnerOne
            !completedSpinnerTwo
            !completedSpinnerThree
            !completedDateText
            binding.btnCleanSelection.alpha = 0.5F
            binding.btnSaveChange.alpha = 0.5F
        }
    }

    private fun openCalendar() {
        hideKeyboardOnFragment(requireActivity(), binding.clMain)
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
                binding.tvEditDate.setText(fecha)
                completedDateText
                areWeDoneYet()
            }, yearGetter, monthGetter, dayGetter)
        dpd.datePicker.maxDate = cal.timeInMillis
        dpd.show()
    }

}