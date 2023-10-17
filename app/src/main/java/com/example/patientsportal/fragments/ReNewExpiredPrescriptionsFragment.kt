package com.example.patientsportal.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.PrescriptionExpiredAdapter
import com.example.patientsportal.databinding.FragmentReNewExpiredPrescriptionsBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.PrescriptionExpired
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class ReNewExpiredPrescriptionsFragment : Fragment(R.layout.fragment_re_new_expired_prescriptions) {

    private lateinit var binding: FragmentReNewExpiredPrescriptionsBinding
    private var listToReNew = ArrayList<PrescriptionExpired>()
    private var listPExpired = ArrayList<PrescriptionExpired>()
    private var enabledReNewPrescription = false
    private lateinit var adapter: PrescriptionExpiredAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReNewExpiredPrescriptionsBinding.bind(view)

        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnReNewPrescription.setOnClickListener {
            if (checkEnableBtn()) {
                val texto = StringBuilder()

                for (p in listToReNew) {
                    if (p.prescription.idPrescription > 0) {
                        texto.append("Nombre: ${p.prescription.doctor.name}, ID: ${p.prescription.idPrescription}, Checked: ${p.isChecked}\n")
                        // Aca se agregan para renovarlas PROXIMAMENTE
                    }
                }
            }
        }

        val currentOrExpired = arguments?.getString(getString(R.string.title_tag))
        if (currentOrExpired != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val task = arguments?.getString(getString(R.string.task_tag))
                if (task != null) {
                    if (task == getString(R.string.generar_recetas)) {
                        binding.tvMainTitle.text = getString(R.string.generar_recetas)
                        binding.tvTitle.text = getString(R.string.seleccione_los_medicamentos_de_los_que_requiere_generar_una_receta)
                        binding.tvBtnReNewPrescription.text = getString(R.string.generar_recetas)
                    }
                }
                val dbPatientsPortal = DbPatientsPortal(requireContext())
                val list = dbPatientsPortal.readAllPrescriptionsByPatient(getPatient(requireActivity(), requireContext()).idPatient, currentOrExpired, "")
                withContext(Dispatchers.Main) {
                    if (list.size > 0) {
                        list.forEach { listPExpired.add(PrescriptionExpired(it, false)) }
                        list.forEach { _ -> listToReNew.add(PrescriptionExpired()) }
                        adapter = PrescriptionExpiredAdapter(listPExpired, requireContext(), currentOrExpired) { p, b, i ->
                            if (b) {
                                listPExpired[i].isChecked = true
                                listToReNew[i] = p
                                checkEnableBtn()
                                updateData(listPExpired)
                            } else {
                                listPExpired[i].isChecked = false
                                listToReNew[i] = PrescriptionExpired()
                                checkEnableBtn()
                                updateData(listPExpired)
                            }
                        }
                        binding.rvExpiredPrescriptions.adapter = adapter
                        Handler().postDelayed({
                            binding.pbExpiredPrescription.visibility = View.GONE
                            binding.rvExpiredPrescriptions.visibility = View.VISIBLE
                        }, 650)
                    }
                }
            }
        }

    }

    private fun checkEnableBtn(): Boolean {
        val b: Boolean
        var count = 0
        listToReNew.forEach { if (it.prescription.idPrescription != 0) { count++ } }
        b = count > 0
        enabledReNewPrescription = count > 0
        binding.btnReNewPrescription.alpha = if (count > 0) { 1F } else { 0.5F }
        return b
    }

    private fun updateData(newList: ArrayList<PrescriptionExpired>) {
        if (!binding.rvExpiredPrescriptions.isComputingLayout) {
            adapter.updateData(newList)
        }
    }

}