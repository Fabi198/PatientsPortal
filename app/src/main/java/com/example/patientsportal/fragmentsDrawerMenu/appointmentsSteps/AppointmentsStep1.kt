package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentAppointmentsStep1Binding
import com.example.patientsportal.entities.dbEntities.PatientCoveragePlan
import com.example.patientsportal.fragmentsDrawerMenu.CoveragesFragment
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment


class AppointmentsStep1 : Fragment(R.layout.fragment_appointments_step1), OnCoverageSelected {

    private lateinit var binding: FragmentAppointmentsStep1Binding
    private var pcpSelected = PatientCoveragePlan()
    private var isFilled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsStep1Binding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))

        val coveragesFragment = CoveragesFragment()
        coveragesFragment.onCoverageSelected = this
        val bundle = Bundle()
        bundle.putBoolean(getString(R.string.comefromappointments_tag), true)
        coveragesFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().add(binding.flContainer.id, coveragesFragment, getString(R.string.coveragesfragment_tag)).commit()

        binding.btnNextStep.setOnClickListener {
            if (isFilled) {
                if (idContainer != null) { showFragmentFromFragment(requireActivity(), AppointmentsStep2(), getString(R.string.appointmentsstep2_tag), requireContext(), idContainer, idPCPSelected = pcpSelected.idPCP) }
            } else {
                Toast.makeText(requireContext(), getString(R.string.debe_seleccionar_alguna_cobertura), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    override fun onCoverageSelected(pcp: PatientCoveragePlan) {
        pcpSelected = pcp
        isFilled = true
    }

}