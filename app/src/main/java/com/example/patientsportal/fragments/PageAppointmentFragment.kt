package com.example.patientsportal.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.appointmentsAdapters.AppointmentsAdapter
import com.example.patientsportal.databinding.FragmentPageAppointmentBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.OnAddPageAppointmentClicked
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.OnAppointmentSelected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PageAppointmentFragment : Fragment(R.layout.fragment_page_appointment) {

    private lateinit var binding: FragmentPageAppointmentBinding
    private lateinit var adapterRV: AppointmentsAdapter
    private var positionSelected = -1
    lateinit var onAppointmentSelected: OnAppointmentSelected
    lateinit var onAddPageAppointmentClicked: OnAddPageAppointmentClicked

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPageAppointmentBinding.bind(view)




        setListAppointments()
        binding.btnSeeMore.setOnClickListener { onAddPageAppointmentClicked.onAddPageAppointmentClicked() }
    }

    private fun setListAppointments() {
        val bundle = arguments
        if (bundle != null) {
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            val numberPage = bundle.getInt(getString(R.string.numberpage_tag))
            val minDays = bundle.getInt(getString(R.string.mindays_tag))
            val maxDays = bundle.getInt(getString(R.string.maxdays_tag))
            val idDoctorSpeciality = bundle.getIntArray(getString(R.string.iddoctorspecialityselected_tag))
            val idPlace = bundle.getIntArray(getString(R.string.idplaceselected_tag))
            lifecycleScope.launch(Dispatchers.IO) {
                val listAppointment = dbPatientsPortal.readPatientLessAppointments(idDoctorSpeciality, idPlace, minDays, maxDays)
                withContext(Dispatchers.Main) {
                    binding.pbAppointments.visibility = View.GONE
                    if (listAppointment.size > 0) {
                        adapterRV = AppointmentsAdapter(listAppointment, requireContext(), -1
                        ) { appointment, position ->
                            if (positionSelected != position) {
                                onAppointmentSelected.onAppointmentSelected(appointment)
                                updateAdapter(position)
                                onAppointmentSelected.undoCheckOnOtherPages(numberPage)
                                positionSelected = position
                            } else {
                                positionSelected = -1
                                updateAdapter(positionSelected)
                                onAppointmentSelected.onAppointmentDeselected()
                            }
                        }
                        binding.rvAppointment.adapter = adapterRV
                        binding.rvAppointment.visibility = View.VISIBLE
                        binding.btnSeeMore.visibility = View.VISIBLE
                    } else {
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }



        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(position: Int) {
        if (!binding.rvAppointment.isComputingLayout) {
            adapterRV.updateData(position)
            adapterRV.notifyDataSetChanged()
        }
    }

}