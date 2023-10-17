package com.example.patientsportal.fragmentsDrawerMenu

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.MedicalTestPreparationsAdapter
import com.example.patientsportal.adapters.PrescriptionsAdapter
import com.example.patientsportal.adapters.appointmentsAdapters.AppointmentsAheadAdapter
import com.example.patientsportal.adapters.listDoctorsAdapters.SuperDoctorAdapter
import com.example.patientsportal.adapters.listPatientTestAdapters.SuperPatientTestAdapter
import com.example.patientsportal.adapters.listRequestedPatientTestAdapters.SuperRequestedPatientTestAdapter
import com.example.patientsportal.databinding.FragmentSearchAndListBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.fragments.MedicalTestResultDisplayer
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.AppointmentsStep1
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.AppointmentsStep5
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class SearchAndListFragment : Fragment(R.layout.fragment_search_and_list) {

    private lateinit var binding: FragmentSearchAndListBinding
    private lateinit var adapterAppointmentAhead: AppointmentsAheadAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchAndListBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val patient = getPatient(requireActivity(), requireContext())
        val task = arguments?.getString(getString(R.string.task_tag))
        val areWeLookingForDate = arguments?.getBoolean(getString(R.string.arewelookingfordate))


        if (idContainer != null) {
            setupSearchTab(patient.idPatient, task, idContainer)
            switchList(task, idContainer, patient.idPatient, "", areWeLookingForDate)
        }

    }

    private fun setupSearchTab(idPatient: Int, task: String?, idContainer: Int) {
        with(binding.etSearch) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()) {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_search_24, 0, R.drawable.baseline_clear_24, 0)
                        switchList(task, idContainer, idPatient, s.toString())
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_search_24, 0, 0, 0)
                        switchList(task, idContainer, idPatient, "")
                    }
                }
            })
            setOnTouchListener { v, event ->
                if (compoundDrawables[2] != null) {
                    if (event.action == ACTION_UP) {
                        val drawableEnd = compoundDrawables[2]
                        if (event.rawX >= (right - drawableEnd.bounds.width())) {
                            text = null
                            switchList(task, idContainer, idPatient, "")
                            v.performClick()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
    }

    private fun switchList(task: String?, idContainer: Int, idPatient: Int, searchLetter: String, areWeLookingForDate: Boolean? = null) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        when (task) {
            getString(R.string.doctorlist) -> { setDoctorListAdapter(dbPatientsPortal, searchLetter, idPatient, idContainer) }

            getString(R.string.prescriptionlist) -> { setPrescriptionListAdapter(dbPatientsPortal, searchLetter, idPatient) }

            in arrayOf(getString(R.string.patienttestlist_tag), getString(R.string.clinicdocumentslist_tag)) -> { setPatientOrClinicDocumentListAdapter(dbPatientsPortal, areWeLookingForDate, idPatient, idContainer) }

            getString(R.string.requestedpatienttestlist_tag) -> { setRequestedPatientListAdapter(dbPatientsPortal, areWeLookingForDate, idPatient, idContainer) }

            getString(R.string.medicaltestpreparationslist) -> { setMedicalTestPreparationAdapter(dbPatientsPortal, searchLetter) }

            getString(R.string.appointmentsahead_task) -> { setAppointmentsAheadAdapter(dbPatientsPortal, idPatient, idContainer) }

            else -> {
                binding.toolbarSearchAndList.visibility = View.GONE
                Handler().postDelayed({
                    binding.pbSearchAndList.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }, 1000)
            }
        }
    }

    private fun setDoctorListAdapter(dbPatientsPortal: DbPatientsPortal, searchLetter: String, idPatient: Int, idContainer: Int) {
        binding.etSearch.hint = getString(R.string.buscar_profesional)
        lifecycleScope.launch(Dispatchers.IO) {
            val list = dbPatientsPortal.readDoctorsBySpeciality(idPatient, searchLetter)
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    val adapter = SuperDoctorAdapter(list, requireContext()) { doc, b ->
                        if (b) { // Si b es false, significa que el boton de tomar un turno fue clickeado
                            showFragmentFromFragment(requireActivity(), AppointmentsStep1(), getString(R.string.appointmentsstep1_tag), requireContext(), idContainer)
                        } else { // Si b es true, significa que el boton de enviar mail fue clickeado
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:${doc.email}")))
                        }
                    }
                    binding.rvItemsRecycler.adapter = adapter
                    binding.pbSearchAndList.visibility = View.GONE
                    binding.rvItemsRecycler.visibility = View.VISIBLE
                    binding.tvNoRegisters.visibility = View.GONE
                } else {
                    binding.rvItemsRecycler.visibility = View.GONE
                    binding.pbSearchAndList.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setPrescriptionListAdapter(dbPatientsPortal: DbPatientsPortal, searchLetter: String, idPatient: Int) {
        binding.etSearch.hint = getString(R.string.buscar_por_medicamento_marca_o_profesional_que_recet)
        val prescriptionTabTitle = arguments?.getString(getString(R.string.prescriptiontabtitle_tag))
        if (prescriptionTabTitle != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = when (prescriptionTabTitle) {
                    getString(R.string.current) -> {
                        dbPatientsPortal.readAllPrescriptionsByPatient(idPatient, getString(R.string.current), searchLetter)
                    }

                    getString(R.string.expired) -> {
                        dbPatientsPortal.readAllPrescriptionsByPatient(idPatient, getString(R.string.expired), searchLetter)
                    }

                    else -> {
                        dbPatientsPortal.readAllPrescriptionsByPatient(idPatient, getString(R.string.expired), searchLetter)
                    }
                }
                withContext(Dispatchers.Main) {
                    if (list.size > 0) {
                        val adapter = PrescriptionsAdapter(list, requireContext())
                        binding.rvItemsRecycler.adapter = adapter
                        Handler().postDelayed({
                            binding.pbSearchAndList.visibility = View.GONE
                            binding.rvItemsRecycler.visibility = View.VISIBLE
                            binding.tvNoRegisters.visibility = View.GONE
                        }, 650)
                    } else {
                        binding.rvItemsRecycler.visibility = View.GONE
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setPatientOrClinicDocumentListAdapter(dbPatientsPortal: DbPatientsPortal, areWeLookingForDate: Boolean?, idPatient: Int, idContainer: Int) {
        binding.toolbarSearchAndList.visibility = View.GONE
        if (areWeLookingForDate != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = dbPatientsPortal.readAllPatientTestsByDateOrName(idPatient, areWeLookingForDate)
                withContext(Dispatchers.Main) {
                    if (list.isNotEmpty()) {
                        val adapter = SuperPatientTestAdapter(list, areWeLookingForDate) {
                            showFragmentFromFragment(
                                requireActivity(),
                                MedicalTestResultDisplayer(),
                                getString(R.string.medicaltestresultdisplayer),
                                requireContext(),
                                idContainer,
                                link = it.urlResult
                            )
                        }
                        binding.rvItemsRecycler.adapter = adapter
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.rvItemsRecycler.visibility = View.VISIBLE
                        binding.tvNoRegisters.visibility = View.GONE
                    } else {
                        binding.rvItemsRecycler.visibility = View.GONE
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setRequestedPatientListAdapter(dbPatientsPortal: DbPatientsPortal, areWeLookingForDate: Boolean?, idPatient: Int, idContainer: Int) {
        binding.toolbarSearchAndList.visibility = View.GONE
        if (areWeLookingForDate != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = dbPatientsPortal.readAllRequestedPatientTests(idPatient, areWeLookingForDate)
                withContext(Dispatchers.Main) {
                    if (list.isNotEmpty()) {
                        val adapter = SuperRequestedPatientTestAdapter(list, areWeLookingForDate) {
                            showFragmentFromFragment(
                                requireActivity(),
                                MedicalTestResultDisplayer(),
                                getString(R.string.medicaltestresultdisplayer),
                                requireContext(),
                                idContainer,
                                link = it.urlResult
                            )
                        }
                        binding.rvItemsRecycler.adapter = adapter
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.rvItemsRecycler.visibility = View.VISIBLE
                        binding.tvNoRegisters.visibility = View.GONE
                    } else {
                        binding.rvItemsRecycler.visibility = View.GONE
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setMedicalTestPreparationAdapter(dbPatientsPortal: DbPatientsPortal, searchLetter: String) {
        binding.etSearch.hint = getString(R.string.busca_el_estudio_que_necesites_y_como_debes_prepararte)
        lifecycleScope.launch(Dispatchers.IO) {
            val list = dbPatientsPortal.readMedicalTestPreparations(searchLetter)
            withContext(Dispatchers.Main) {
                if (list.size > 0) {
                    val adapter = MedicalTestPreparationsAdapter(list, requireContext())
                    binding.rvItemsRecycler.adapter = adapter
                    binding.pbSearchAndList.visibility = View.GONE
                    binding.rvItemsRecycler.visibility = View.VISIBLE
                    binding.tvNoRegisters.visibility = View.GONE
                } else {
                    binding.rvItemsRecycler.visibility = View.GONE
                    binding.pbSearchAndList.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setAppointmentsAheadAdapter(dbPatientsPortal: DbPatientsPortal, idPatient: Int, idContainer: Int) {
        binding.toolbarSearchAndList.visibility = View.GONE
        val appointmentTabTitle = arguments?.getString(getString(R.string.appointmentstabtitle_tag))
        if (appointmentTabTitle != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = when (appointmentTabTitle) {
                    getString(R.string.todos_tab_title) -> {
                        dbPatientsPortal.readPatientAppointmentsAhead(idPatient)
                    }

                    getString(R.string.presenciales_tab_title) -> {
                        dbPatientsPortal.readPatientAppointmentsAhead(idPatient)
                    }

                    getString(R.string.teleconsultas_tab_title) -> {
                        dbPatientsPortal.readPatientAppointmentsAhead(0)
                    }

                    else -> {
                        dbPatientsPortal.readPatientAppointmentsAhead(idPatient)
                    }
                }
                withContext(Dispatchers.Main) {
                    if (list.size > 0) {
                        adapterAppointmentAhead = AppointmentsAheadAdapter(list, requireContext()) { appointment, position, b ->
                            if (b) { // Si b es true, significa que el boton de añadir al calendario fue clickeado

                            } else { // Si b es false, significa que el boton de borrar fue clickeado
                                showDeleteAppointmentDialog(appointment, position, idContainer)
                            }
                        }
                        binding.rvItemsRecycler.adapter = adapterAppointmentAhead
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.rvItemsRecycler.visibility = View.VISIBLE
                        binding.tvNoRegisters.visibility = View.GONE
                    } else {
                        binding.rvItemsRecycler.visibility = View.GONE
                        binding.pbSearchAndList.visibility = View.GONE
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showDeleteAppointmentDialog(appointment: Appointment, position: Int, idContainer: Int) {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage(getString(R.string.desea_eliminar_este_turno))
            setPositiveButton(getString(R.string.si)) { _, _ ->
                if (dbPatientsPortal.updateReleaseAppointment(appointment)) {
                    adapterAppointmentAhead.notifyItemRemoved(position)
                    showFragmentFromFragment(requireActivity(), AppointmentsStep5(), getString(R.string.appointmentsstep5_tag), requireContext(), idContainer, appointmentCreatedOrDeleted = false)
                }
            }
            setNegativeButton(getString(R.string.no), null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}