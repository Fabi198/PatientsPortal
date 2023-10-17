package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.adapters.autoCompleteAdapters.AutoCompleteTextDoctorsAdapter
import com.example.patientsportal.adapters.autoCompleteAdapters.AutoCompleteTextSpecialitiesAdapter
import com.example.patientsportal.adapters.customAlertDialogAdapters.CustomAlertDialogDoctorsAdapter
import com.example.patientsportal.adapters.customAlertDialogAdapters.CustomAlertDialogMultiPlacesAdapter
import com.example.patientsportal.databinding.CustomAlertDialogDoctorsBinding
import com.example.patientsportal.databinding.CustomAlertDialogMultiPlacesBinding
import com.example.patientsportal.databinding.FragmentAppointmentsStep3Binding
import com.example.patientsportal.db.arrays.ArrayDoctorRelations.arrayDoctorSpecialities
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.DoctorSpeciality
import com.example.patientsportal.entities.dbEntities.Place
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.HideKeyboard.hideKeyboardOnFragment
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppointmentsStep3 : Fragment(R.layout.fragment_appointments_step3) {

    private lateinit var binding: FragmentAppointmentsStep3Binding
    private lateinit var adapterMyDoctors: CustomAlertDialogDoctorsAdapter
    private lateinit var adapterMultiPlaces: CustomAlertDialogMultiPlacesAdapter
    private var doctorSelected = ArrayList<DoctorSpeciality>()
    private var placeSelected = ArrayList<Place>()
    private var selectionablePlaces = ArrayList<Place>()
    private var listDoctorsForAutoComplete = ArrayList<DoctorSpeciality>(arrayDoctorSpecialities)
    private var enabledNextStep = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsStep3Binding.bind(view)
        doctorSelected.add(DoctorSpeciality())
        placeSelected.add(Place())
        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        val doctorOrSpeciality = arguments?.getBoolean(getString(R.string.doctororspeciality_tag))
        if (doctorOrSpeciality != null) {
            setView(doctorOrSpeciality)
            setSearchText(doctorOrSpeciality)
            putUnderLine(binding.btnMyDoctors.text.toString())
            binding.btnMyDoctors.setOnClickListener { showMyDoctorsDialog() }
        }
    }

    private fun setView(doctorOrSpeciality: Boolean?) {
        if (doctorOrSpeciality != null) {
            binding.btnBack.text = if (doctorOrSpeciality) { getString(R.string.buscar_por_profesional) } else { getString(R.string.buscar_por_especialidad) }
            binding.tvTitle.text = if (doctorOrSpeciality) { getString(R.string.seleccion_un_profesional_y_donde_quer_s_atenderte) } else { getString(R.string.seleccion_la_especialidad_y_donde_quer_s_atenderte) }
            binding.tvSearchFrom.text = if (doctorOrSpeciality) { getString(R.string.apellido_del_profesional) } else { getString(R.string.nombre_de_la_especialidad) }
            binding.btnMyDoctors.visibility = if (doctorOrSpeciality) { View.VISIBLE } else { View.GONE }
        }
    }

    private fun setSearchText(doctorOrSpeciality: Boolean?) {
        if (doctorOrSpeciality != null) {
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            with(binding.etSearch) {
                threshold = 0
                if (doctorOrSpeciality) {
                    val adapterDoctors = AutoCompleteTextDoctorsAdapter(requireContext(), listDoctorsForAutoComplete)
                    setAdapter(adapterDoctors)
                    setOnItemClickListener { _, _, position, _ ->
                        adapterDoctors.getItem(position)?.let {
                            doctorSelected[0] = it
                            selectionablePlaces.clear()
                            selectionablePlaces = dbPatientsPortal.readPlacesByDoctor(doctorSelected[0].idDoctorSpeciality)
                            setEnabledPlaces()
                            hideKeyboardOnFragment(requireActivity(), binding.rlMain)
                        }
                    }
                } else {
                    val adapterSpeciality = AutoCompleteTextSpecialitiesAdapter(requireContext(), listDoctorsForAutoComplete)
                    setAdapter(adapterSpeciality)
                    setOnItemClickListener { _, _, position, _ ->
                        adapterSpeciality.getItem(position)?.let {
                            doctorSelected[0] = it
                            selectionablePlaces.clear()
                            selectionablePlaces = dbPatientsPortal.readPlacesBySpeciality(doctorSelected[0].speciality.idSpeciality)
                            setEnabledPlaces()
                            hideKeyboardOnFragment(requireActivity(), binding.rlMain)
                        }
                    }
                }
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (s.toString().isNotEmpty()) {
                            setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_search_24, 0, R.drawable.baseline_clear_24, 0)
                        } else {
                            setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_search_24, 0, 0, 0)
                            doctorSelected.clear()
                            doctorSelected.add(DoctorSpeciality())
                            placeSelected.clear()
                            selectionablePlaces.clear()
                            binding.tvAttentionPlaces.alpha = 0.5F
                            binding.cvAttentionPlaces.alpha = 0.5F
                            setNextStep(false)
                        }
                    }
                })
                setOnTouchListener { v, event ->
                    if (compoundDrawables[2] != null) {
                        if (event.action == MotionEvent.ACTION_UP) {
                            val drawableEnd = compoundDrawables[2]
                            if (event.rawX >= (right - drawableEnd.bounds.width())) {
                                text = null
                                doctorSelected.clear()
                                doctorSelected.add(DoctorSpeciality())
                                placeSelected.clear()
                                selectionablePlaces.clear()
                                binding.tvAttentionPlaces.alpha = 0.5F
                                binding.cvAttentionPlaces.alpha = 0.5F
                                setNextStep(false)
                                v.performClick()
                                return@setOnTouchListener true
                            }
                        }
                    }
                    false
                }
            }
        }
    }

    private fun showMyDoctorsDialog() {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val binding = CustomAlertDialogDoctorsBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setOnDismissListener { if (doctorSelected[0].idDoctorSpeciality != 0) { setDoctorForAutoCompleteText(doctorSelected); setEnabledPlaces() } }
            .create()
        lifecycleScope.launch(Dispatchers.IO) {
            val doctorsList = dbPatientsPortal.readAllDoctorsPatient(getPatient(requireActivity(), requireContext()).idPatient)
            withContext(Dispatchers.Main) {
                if (doctorsList.size > 0) {
                    adapterMyDoctors = CustomAlertDialogDoctorsAdapter(doctorsList, -1) { dS, position ->
                        doctorSelected[0] = dS
                        selectionablePlaces.clear()
                        selectionablePlaces = dbPatientsPortal.readPlacesByDoctor(doctorSelected[0].idDoctorSpeciality)
                        updateAdapterDoctors(binding.rvSpinnerItems, position)
                    }
                    binding.rvSpinnerItems.adapter = adapterMyDoctors
                    binding.pbMyDoctorsListWithCheckbox.visibility = View.GONE
                    binding.rvSpinnerItems.visibility = View.VISIBLE
                    binding.btnCleanSelection.setOnClickListener { updateAdapterDoctors(binding.rvSpinnerItems, -1); doctorSelected[0] = DoctorSpeciality() }
                    binding.btnSetDoctor.setOnClickListener { if (doctorSelected[0].idDoctorSpeciality != 0) { setDoctorForAutoCompleteText(doctorSelected); setEnabledPlaces(); alertDialog.dismiss() } }
                } else {
                    binding.pbMyDoctorsListWithCheckbox.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
            }
        }
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setDoctorForAutoCompleteText(doctorSpeciality: ArrayList<DoctorSpeciality>) {
        binding.etSearch.setText("${doctorSpeciality[0].doctor.name} ${doctorSpeciality[0].doctor.lastName}")
    }

    private fun setEnabledPlaces() {
        binding.tvAttentionPlaces.alpha = 1F
        binding.cvAttentionPlaces.alpha = 1F
        binding.cvAttentionPlaces.setCardBackgroundColor(requireContext().getColor(R.color.blueLight))
        binding.tvMultiPlacesSelected.setOnClickListener { if (binding.cvAttentionPlaces.alpha == 1F) { showMultiPlacesDialog() } }
    }

    private fun showMultiPlacesDialog() {
        val binding = CustomAlertDialogMultiPlacesBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setOnDismissListener {  }
            .create()
        if (selectionablePlaces.size > 0) {
            adapterMultiPlaces = CustomAlertDialogMultiPlacesAdapter(selectionablePlaces, -1, { place, position ->
                selectionablePlaces[position].multiSelected = true
                placeSelected.add(place)
                updateAdapterPlaces(binding.rvSpinnerItems, position)
                setNextStep(true)
            }, { place, position ->
                selectionablePlaces[position].multiSelected = false
                placeSelected = placeSelected.filter { pS -> pS.idPlace != place.idPlace }.toMutableList() as ArrayList<Place>
                if (placeSelected.size < 1) { setNextStep(false) }
            })
            binding.rvSpinnerItems.adapter = adapterMultiPlaces
            binding.pbMultiPlacesListWithCheckbox.visibility = View.GONE
            binding.rvSpinnerItems.visibility = View.VISIBLE
            binding.btnCleanSelection.setOnClickListener { updateAdapterPlaces(binding.rvSpinnerItems, -1); placeSelected.clear(); setNextStep(false) }
            binding.btnSetPlaces.setOnClickListener { showTextMultiPlaces(); alertDialog.dismiss() }
        } else {
            binding.pbMultiPlacesListWithCheckbox.visibility = View.GONE
            binding.tvNoRegisters.visibility = View.VISIBLE
        }
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun showTextMultiPlaces() {
        binding.spinnerAttentionPlaces.visibility = View.GONE
        binding.cvAttentionPlaces.setCardBackgroundColor(requireContext().getColor(R.color.blueLight))
        binding.tvMultiPlacesSelected.text = placeSelected.joinToString("\n") { it.name }
        binding.tvMultiPlacesSelected.visibility = View.VISIBLE
        binding.tvMultiPlacesSelected.setOnClickListener { showMultiPlacesDialog() }
    }

    private fun setNextStep(isEnabled: Boolean) {
        enabledNextStep = isEnabled
        if (!enabledNextStep) { disableDoctorPlacesSpinner() }
        binding.btnNextStep.alpha = if (isEnabled) { 1F } else { 0.5F }
        binding.btnNextStep.setOnClickListener {
            if (enabledNextStep) {
                val dbPatientsPortal = DbPatientsPortal(requireContext())
                val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
                val idPCPSelected = arguments?.getInt(getString(R.string.idpcpselected_tag))
                if (idContainer != null && idPCPSelected != null) {
                    val idPlaceArray = ArrayList<Int>()
                    placeSelected.forEach { idPlaceArray.add(it.idPlace) }
                    val doctorOrSpeciality = arguments?.getBoolean(getString(R.string.doctororspeciality_tag))
                    if (doctorOrSpeciality != null) {
                        val idDoctorSpecialityArray = if (doctorOrSpeciality) {
                            intArrayOf(doctorSelected[0].idDoctorSpeciality)
                        } else {
                            dbPatientsPortal.readAllIDDoctorSpecialityBySpecialityAndPlace(doctorSelected[0].speciality.idSpeciality, idPlaceArray.toIntArray())
                        }
                        showFragmentFromFragment(requireActivity(), AppointmentsStep4(), getString(R.string.appointmentsstep4_tag), requireContext(), idContainer, idPCPSelected = idPCPSelected, idDoctorSpecialitySelected = idDoctorSpecialityArray, idPlaceSelected = idPlaceArray.toIntArray())
                    }

                }
            }
        }
    }




    private fun disableDoctorPlacesSpinner() {
        val emptyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf<String>())
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAttentionPlaces.adapter = emptyAdapter
        binding.cvAttentionPlaces.setCardBackgroundColor(requireContext().getColor(R.color.white))
    }

    private fun putUnderLine(text: String) {
        val spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.btnMyDoctors.text = spannableString
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterPlaces(rv: RecyclerView, position: Int) {
        if (!rv.isComputingLayout) {
            adapterMultiPlaces.updateData(position)
            adapterMultiPlaces.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterDoctors(rv: RecyclerView, position: Int) {
        if (!rv.isComputingLayout) {
            adapterMyDoctors.updateData(position)
            adapterMyDoctors.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listDoctorsForAutoComplete = arrayDoctorSpecialities
        binding.etSearch.text = null
    }

    override fun onResume() {
        super.onResume()
        listDoctorsForAutoComplete = arrayDoctorSpecialities
        binding.etSearch.text = null
    }

}