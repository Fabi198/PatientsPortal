package com.example.patientsportal.fragmentsDrawerMenu

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.CoveragesAdapter
import com.example.patientsportal.adapters.customAlertDialogAdapters.CustomAlertDialogCoveragesAdapter
import com.example.patientsportal.databinding.FragmentCoveragesBinding
import com.example.patientsportal.databinding.CustomAlertDialogCoveragesFromPatientBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.PatientCoveragePlan
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.OnCoverageSelected
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class CoveragesFragment : Fragment(R.layout.fragment_coverages) {

    private lateinit var binding: FragmentCoveragesBinding
    lateinit var onCoverageSelected: OnCoverageSelected
    private lateinit var adapter: CoveragesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCoveragesBinding.bind(view)

        setupRecyclerView(arguments?.getBoolean(getString(R.string.comefromappointments_tag)))

        binding.btnAddCoverage.setOnClickListener {
            addOrEditCoverage(true)
        }
    }

    private fun addOrEditCoverage(areWeAdd: Boolean, pcpEditable: PatientCoveragePlan ?= null) {
        binding.clMainView.visibility = View.GONE
        if (areWeAdd) {
            binding.tvSaveCoverage.text = getString(R.string.guardar_cobertura)
        } else {
            binding.tvSaveCoverage.text = getString(R.string.editar_cobertura)
        }
        binding.clAddView.visibility = View.VISIBLE
        setAutoCompleteTextCoverage(pcpEditable)
    }

    private fun setAutoCompleteTextCoverage(pcpEditable: PatientCoveragePlan?) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, setCoveragesSuggestions())
        binding.etSearch.threshold = 0
        binding.etSearch.setAdapter(adapter)
        binding.etSearch.setOnItemClickListener { parent, _, position, _ ->
            val nameCoverage = parent.getItemAtPosition(position) as String
            coverageSelected(nameCoverage, pcpEditable)
        }
        pcpEditable?.let {
            binding.etSearch.setText(it.planType.coverage.name)
            coverageSelected(it.planType.coverage.name, pcpEditable)
        }
    }

    private fun coverageSelected(nameCoverage: String, pcpEditable: PatientCoveragePlan?) {
        pcpEditable?.let {
            binding.etPlan.setText(it.planType.planType)
            binding.etAffiliateNumber.setText(it.affiliateNumber)
        }
        binding.etPlan.isEnabled = true
        binding.etAffiliateNumber.isEnabled = true
        binding.etPlan.alpha = 1F
        binding.etAffiliateNumber.alpha = 1F
        //binding.btnCredentialPhotoText.alpha = 1F
        binding.btnSaveCoverage.alpha = 1F
        binding.btnSaveCoverage.isEnabled = true
        binding.etPlan.setOnClickListener {
            binding.etPlan.setText(showSpinnerOptionsDialog(nameCoverage, binding.etPlan))
        }
        binding.btnSaveCoverage.setOnClickListener {
            if (binding.etPlan.text.toString().isNotEmpty() && binding.etAffiliateNumber.text.toString().isNotEmpty()) {
                binding.tvSaveCoverage.visibility = View.INVISIBLE
                binding.pbSaveCoverage.visibility = View.VISIBLE
                val dbPatientsPortal = DbPatientsPortal(requireContext())
                val planType = binding.etPlan.text.toString()
                val affiliateNumber = binding.etAffiliateNumber.text.toString()
                when (binding.tvSaveCoverage.text) {
                    getString(R.string.editar_cobertura) -> {
                        pcpEditable?.let {
                            Handler().postDelayed({
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val result = dbPatientsPortal.editPatientCoveragePlan(it, getPatient(requireActivity(), requireContext()).idPatient, nameCoverage, planType, affiliateNumber)
                                    withContext(Dispatchers.Main) {
                                        if (result) {
                                            refreshListView()
                                        } else {
                                            Toast.makeText(requireContext(), getString(R.string.hubo_un_problema_con_la_base_de_datos), Toast.LENGTH_SHORT).show()
                                        }
                                        binding.pbSaveCoverage.visibility = View.INVISIBLE
                                        binding.tvSaveCoverage.visibility = View.VISIBLE
                                    }
                                }
                            }, 500)
                        }
                    }
                    getString(R.string.guardar_cobertura) -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val pcp = dbPatientsPortal.createPatientCoveragePlan(getPatient(requireActivity(), requireContext()).idPatient, nameCoverage, planType, affiliateNumber)
                            withContext(Dispatchers.Main) {
                                Handler().postDelayed({
                                    if (pcp > 0) {
                                        refreshListView()
                                    } else {
                                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_guardando_la_cobertura), Toast.LENGTH_SHORT).show()
                                    }
                                    binding.pbSaveCoverage.visibility = View.INVISIBLE
                                    binding.tvSaveCoverage.visibility = View.VISIBLE
                                }, 500)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.debe_completar_todos_los_campos), Toast.LENGTH_SHORT).show()
            }
            binding.etSearch.setText("")
            binding.etPlan.setText("")
            binding.etAffiliateNumber.setText("")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshListView() {
        setupRecyclerView(arguments?.getBoolean(getString(R.string.comefromappointments_tag)))
        binding.clAddView.visibility = View.GONE
        binding.clMainView.visibility = View.VISIBLE
    }

    private fun setCoveragesSuggestions(): ArrayList<String> {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        return dbPatientsPortal.readAllCoverageNames()
    }

    private fun setupRecyclerView(comeFromAppointments: Boolean?) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val d = dbPatientsPortal.readCoveragesPlansByPatient(getPatient(requireActivity(), requireContext()).idPatient)
            withContext(Dispatchers.Main) {
                adapter = CoveragesAdapter(d, requireContext(), -1) { pcp, position, task ->
                    when (task) {
                        getString(R.string.edit) -> {
                            // Editar en base de datos
                            addOrEditCoverage(false, pcp)
                        }
                        getString(R.string.delete) -> {
                            // Borrar en base de datos
                            showDeleteCoverageAlert(pcp)
                        }
                        getString(R.string.select) -> {
                            if (comeFromAppointments == true) {
                                updateAdapter(position)
                                onCoverageSelected.onCoverageSelected(pcp)
                            }
                        }
                    }
                }
                binding.rvCoverages.adapter = adapter
                if (d.size == 1 && comeFromAppointments == true) { onCoverageSelected.onCoverageSelected(d[0]) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(position: Int) {
        if (!binding.rvCoverages.isComputingLayout) {
            adapter.updateData(position)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showSpinnerOptionsDialog(nameCoverage: String, etPlan: EditText): String {
        var planType = ""
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val binding = CustomAlertDialogCoveragesFromPatientBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setOnDismissListener { etPlan.setText(planType) }
            .create()
        val adapter = CustomAlertDialogCoveragesAdapter(dbPatientsPortal.readPlansByCoverage(nameCoverage)) {
            planType = it.planType
            alertDialog.dismiss()
        }
        binding.rvSpinnerItems.adapter = adapter
        binding.cvCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
        return planType
    }

    private fun showDeleteCoverageAlert(pcp: PatientCoveragePlan) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage(getString(R.string.seguro_desea_eliminar_la_cobertura))
            setPositiveButton(getString(R.string.si)) { _, _ ->
                if (dbPatientsPortal.deletePatientCoveragePlan(pcp)) {
                    binding.rvCoverages.visibility = View.GONE
                    setupRecyclerView(arguments?.getBoolean(getString(R.string.comefromappointments_tag)))
                    binding.rvCoverages.visibility = View.VISIBLE
                }
            }
            setNegativeButton(getString(R.string.no), null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}