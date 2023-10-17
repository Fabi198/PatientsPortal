package com.example.patientsportal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.HealthControlAdapter
import com.example.patientsportal.databinding.FragmentTabResultsBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.ShowFragment
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TabResultsFragment : Fragment(R.layout.fragment_tab_results) {

    private lateinit var binding: FragmentTabResultsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTabResultsBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val title = arguments?.getString(getString(R.string.title_tag))

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        if (idContainer != null && title != null) {
            setFABMenu(idContainer, title)
            setHeader(title)
            setRecycler(title)
        }



    }

    private fun setRecycler(title: String) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val listRegisters = dbPatientsPortal.readAllHealthControlsRegistersByHC(getPatient(requireActivity(), requireContext()), title)
            withContext(Dispatchers.Main) {
                if (listRegisters.size > 0) {
                    binding.pbHealthControls.visibility = View.GONE
                    val adapter = HealthControlAdapter(listRegisters, title)
                    binding.rvHealthControls.adapter = adapter
                    binding.rvHealthControls.visibility = View.VISIBLE
                } else {
                    binding.pbHealthControls.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setHeader(title: String) {
        when (title) {
            getString(R.string.weightandheight_tag) -> { binding.headerFiveColumns.visibility = View.VISIBLE }
            getString(R.string.temperature_tag) -> { binding.headerThreeColumn.visibility = View.VISIBLE; binding.specialColumn.text = getString(R.string.temperatura_c) }
            getString(R.string.heartfrequency_tag) -> { binding.headerThreeColumn.visibility = View.VISIBLE; binding.specialColumn.text = getString(R.string.frecuencia_card_aca_ppm) }
            getString(R.string.arterialpressure_tag) -> { binding.headerThreeColumn.visibility = View.VISIBLE; binding.specialColumn.text = getString(R.string.presi_n_arterial_mmhg) }
            getString(R.string.glucose_tag) -> { binding.headerFourColumns.visibility = View.VISIBLE; binding.secondColumn.text = getString(R.string.glucemia_mg_dl); binding.thirdColumn.text = getString(R.string.comi_en_las_ultimas_2_horas) }
            getString(R.string.breathfrequency_tag) -> { binding.headerThreeColumn.visibility = View.VISIBLE; binding.specialColumn.text = getString(R.string.frecuencia_respiratoria_res_min) }
            getString(R.string.oxygensaturation_tag) -> { binding.headerFourColumns.visibility = View.VISIBLE; binding.secondColumn.text = getString(R.string.saturacion_de_oxigeno_); binding.thirdColumn.text = getString(R.string.ten_as_ox_geno_suplementario) }
            getString(R.string.breathlessness_tag) -> { binding.headerThreeColumn.visibility = View.VISIBLE; binding.specialColumn.text = getString(R.string.disnea_x_10) }
            else -> {}
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

    private fun setFABMenu(idContainer: Int, title: String) {
        binding.fabHealthControl.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() { binding.modalBackground.visibility = View.VISIBLE }
            override fun onMenuCollapsed() { binding.modalBackground.visibility = View.GONE }
        })
        val loadNewTitle = when (title) {
            getString(R.string.weightandheight_tag) -> { getString(R.string.cargar_peso_y_altura) }
            getString(R.string.temperature_tag) -> { getString(R.string.cargar_temperatura) }
            getString(R.string.heartfrequency_tag) -> { getString(R.string.cargar_frecuencia_card_aca) }
            getString(R.string.arterialpressure_tag) -> { getString(R.string.cargar_presi_n_arterial) }
            getString(R.string.glucose_tag) -> { getString(R.string.cargar_glucemia) }
            getString(R.string.breathfrequency_tag) -> { getString(R.string.cargar_frecuencia_respiratoria) }
            getString(R.string.oxygensaturation_tag) -> { getString(R.string.cargar_saturaci_n_de_oxigeno) }
            getString(R.string.breathlessness_tag) -> { getString(R.string.cargar_disnea) }
            else -> { "" }
        }
        binding.loadNewHealthControl.title = loadNewTitle
        binding.loadNewHealthControl.setOnClickListener {
            ShowFragment.showFragmentFromFragment(
                requireActivity(),
                NewHealthControlFragment(),
                getString(R.string.newhealthcontrolfragment_tag),
                requireContext(),
                idContainer,
                title = title
            )
            binding.fabHealthControl.collapse()
        }
        binding.showGraphs.setOnClickListener {
            binding.fabHealthControl.collapse()
        }
    }

    override fun onResume() {
        super.onResume()
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val title = arguments?.getString(getString(R.string.title_tag))
        if (idContainer != null && title != null) {
            setRecycler(title)
        }
    }

}


/*
when (title) {
            getString(R.string.weightandheight_tag) -> {  }
            getString(R.string.temperature_tag) -> {  }
            getString(R.string.heartfrequency_tag) -> {  }
            getString(R.string.arterialpressure_tag) -> {  }
            getString(R.string.glucose_tag) -> {  }
            getString(R.string.breathfrequency_tag) -> {  }
            getString(R.string.oxygensaturation_tag) -> {  }
            getString(R.string.breathlessness_tag) -> {  }
            else -> { "" }
        }
 */