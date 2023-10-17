package com.example.patientsportal.fragmentsDrawerMenu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.adapters.CardViewAdapter
import com.example.patientsportal.databinding.FragmentCardViewsBinding
import com.example.patientsportal.entities.CustomCardView
import com.example.patientsportal.fragments.TabResultsFragment
import com.example.patientsportal.fragments.TripAdviceFragment
import com.example.patientsportal.fragments.VideoCallsAdvice
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.AppointmentsStep1
import com.example.patientsportal.objects.CardLists
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment


class CardViewsFragment : Fragment(R.layout.fragment_card_views) {

    private lateinit var binding: FragmentCardViewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCardViewsBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))

        setupUI(idContainer)
    }

    private fun setupUI(idContainer: Int?) {
        if (idContainer != null) {
            val list: ArrayList<CustomCardView> = when (arguments?.getString(getString(R.string.title_tag))) {
                getString(R.string.turnos) -> { CardLists.getAppointmentList(requireContext()) }
                getString(R.string.estudios) -> { CardLists.getMedicalTestList(requireContext()) }
                getString(R.string.mi_cobertura) -> { CardLists.getHealthCoverageList(requireContext()) }
                getString(R.string.mis_medicos) -> { CardLists.getMyDoctorsList(requireContext()) }
                getString(R.string.comunidades) -> { CardLists.getCommunityList(requireContext()) }
                getString(R.string.cartilla) -> { CardLists.getProviderDirectoryList(requireContext()) }
                getString(R.string.controles_de_salud) -> { CardLists.getHealthControlList(requireContext()) }
                else -> { arrayListOf(CustomCardView()) }
            }
            val adapter = CardViewAdapter(list) {
                when (it) {
                    // Appointments Cards
                    getString(R.string.reservar_turnos) -> { showFragmentFromFragment(requireActivity(), AppointmentsStep1(), getString(R.string.appointmentsstep1_tag), requireContext(), idContainer) }
                    getString(R.string.mis_turnos_agendados) -> { showFragmentFromFragment(requireActivity(), TwoPagesFragment(), getString(R.string.twopagesfragment_tag), requireContext(), idContainer, getString(R.string.mis_turnos_agendados_title), arrayOf(getString(R.string.todos_tab_title), getString(R.string.presenciales_tab_title), getString(R.string.teleconsultas_tab_title)), task = getString(R.string.appointmentsahead_task)) }
                    getString(R.string.historial) -> { showFragmentFromFragment(requireActivity(), AppointmentsHistory(), getString(R.string.appointmentshistory_tag), requireContext(), idContainer) }
                    getString(R.string.guardia_virtual) -> { showFragmentFromFragment(requireActivity(), VideoCallsAdvice(), getString(R.string.videocallsadvice_tag), requireContext(), idContainer) }

                    // Medical Test Cards
                    getString(R.string.ver_resultados) -> { showFragmentFromFragment(requireActivity(), TwoPagesFragment(), getString(R.string.twopagesfragment_tag), requireContext(), idContainer, getString(R.string.see_medical_test_title), arrayOf(getString(R.string.por_fecha), getString(R.string.por_tipo_de_estudio)), task = getString(R.string.patienttestlist_tag)) }
                    getString(R.string.estudios_solicitados) -> { showFragmentFromFragment(requireActivity(), TwoPagesFragment(), getString(R.string.twopagesfragment_tag), requireContext(), idContainer, getString(R.string.requested_medical_tests_title), arrayOf(getString(R.string.por_fecha), getString(R.string.por_tipo_de_estudio)), task = getString(R.string.requestedpatienttestlist_tag)) }
                    getString(R.string.preparacion_de_estudios) -> { showFragmentFromFragment(requireActivity(), SearchAndListFragment(), getString(R.string.searchandlistfragment_tag), requireContext(), idContainer, task = getString(R.string.medicaltestpreparationslist)) }

                    // Health Coverages Cards
                    getString(R.string.my_plan) -> { showFragmentFromFragment(requireActivity(), CoveragesFragment(), getString(R.string.coveragesfragment_tag), requireContext(), idContainer, getString(R.string.my_coverages_title)) }
                    getString(R.string.my_bills) -> { showFragmentFromFragment(requireActivity(), TwoPagesFragment(), getString(R.string.twopagesfragment_tag), requireContext(), idContainer, getString(R.string.my_bills_title), arrayOf(getString(R.string.por_fecha), getString(R.string.por_tipo_de_factura)), task = getString(R.string.billslist_tag)) }
                    getString(R.string.travel_assistance) -> { showFragmentFromFragment(requireActivity(), TripAdviceFragment(), getString(R.string.tripadvicefragment_tag), requireContext(), idContainer, getString(R.string.trip_title)) }

                    // Doctors Card
                    getString(R.string.mis_medicos) -> { showFragmentFromFragment(requireActivity(), SearchAndListFragment(), getString(R.string.searchandlistfragment_tag), requireContext(), idContainer, task = getString(R.string.doctorlist)) }

                    // Community Cards
                    getString(R.string.my_pregnancy) -> { showFragmentFromFragment(requireActivity(), MyPregnantFragment(), getString(R.string.mypregnantfragment_tag), requireContext(), idContainer) }

                    // Provider Directory Card
                    getString(R.string.especialties_and_proffessionals) -> { showFragmentFromFragment(requireActivity(), TwoPagesFragment(), getString(R.string.twopagesfragment_tag), requireContext(), idContainer, getString(R.string.provider_directory_title), arrayOf(getString(R.string.especialidad), getString(R.string.profesional))) }

                    // Health Control Cards
                    getString(R.string.peso_y_altura) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.weightandheight_tag)) }
                    getString(R.string.temp) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.temperature_tag)) }
                    getString(R.string.frecuencia_cardiaca) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.heartfrequency_tag)) }
                    getString(R.string.presion_arterial) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.arterialpressure_tag)) }
                    getString(R.string.glucemia) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.glucose_tag)) }
                    getString(R.string.frecuencia_respiratoria) -> showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.breathfrequency_tag))
                    getString(R.string.saturacion_de_oxigeno) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.oxygensaturation_tag)) }
                    getString(R.string.disnea) -> { showFragmentFromFragment(requireActivity(), TabResultsFragment(), getString(R.string.tabresultsfragment_tag), requireContext(), idContainer, title = getString(R.string.breathlessness_tag)) }
                }
            }
            binding.rvCardView.adapter = adapter
        }

    }


}