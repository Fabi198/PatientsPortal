package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentAppointmentsStep2Binding
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment


class AppointmentsStep2 : Fragment(R.layout.fragment_appointments_step2) {

    private lateinit var binding: FragmentAppointmentsStep2Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsStep2Binding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val idPCPSelected = arguments?.getInt(getString(R.string.idpcpselected_tag))

        if (idContainer != null && idPCPSelected != null) {

            binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

            binding.cvSpecialities.setOnClickListener { nextStep(idContainer, idPCPSelected, false) }
            binding.cvDoctors.setOnClickListener { nextStep(idContainer, idPCPSelected, true) }
        }


    }

    private fun nextStep(idContainer: Int, idPCPSelected: Int, doctorOrSpeciality: Boolean) {
        showFragmentFromFragment(requireActivity(), AppointmentsStep3(), getString(R.string.appointmentsstep3_tag), requireContext(), idContainer, idPCPSelected = idPCPSelected, doctorOrSpeciality = doctorOrSpeciality)
    }

}