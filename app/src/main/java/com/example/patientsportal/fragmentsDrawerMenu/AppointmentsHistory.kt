package com.example.patientsportal.fragmentsDrawerMenu

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.patientsportal.R
import com.example.patientsportal.adapters.appointmentsAdapters.AppointmentsHistoryAdapter
import com.example.patientsportal.databinding.FragmentAppointmentsHistoryBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.AppointmentsStep4
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Suppress("NAME_SHADOWING")
class AppointmentsHistory : Fragment(R.layout.fragment_appointments_history) {

    private lateinit var binding: FragmentAppointmentsHistoryBinding
    private lateinit var adapterHistoryAppointments: AppointmentsHistoryAdapter
    private lateinit var selectedSinceDate: Calendar
    private lateinit var selectedUntilDate: Calendar
    private var setMinDate = false
    private var setMaxDate = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsHistoryBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val patient = getPatient(requireActivity(), requireContext())
        val dbPatientsPortal = DbPatientsPortal(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            val listAppointment = dbPatientsPortal.readPatientHistoryAppointments(patient.idPatient)
            withContext(Dispatchers.Main) {
                if (listAppointment.size > 0) {
                    adapterHistoryAppointments = AppointmentsHistoryAdapter(listAppointment, requireContext()) {
                        showFragmentFromFragment(requireActivity(), AppointmentsStep4(), getString(R.string.appointmentsstep4_tag), requireContext(), idContainer, idPCPSelected = it.patientCoveragePlan.idPCP, idDoctorSpecialitySelected = intArrayOf(it.doctorSpeciality.idDoctorSpeciality), idPlaceSelected = intArrayOf(it.place.idPlace))
                    }
                    binding.rvAppointments.adapter = adapterHistoryAppointments
                    binding.pbAppointments.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                } else {
                    binding.pbAppointments.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
                binding.btnSince.setOnClickListener { openSinceCalendar(binding.btnSince) }
                binding.btnUntil.setOnClickListener { openUntilCalendar(binding.btnUntil) }
                binding.btnCleanSelection.setOnClickListener { cleanFilters(); updateAdapter(binding.rvAppointments, dbPatientsPortal) }
                binding.btnSetDates.setOnClickListener { updateAdapter(binding.rvAppointments, dbPatientsPortal) }
                binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(rvAppointments: RecyclerView, dbPatientsPortal: DbPatientsPortal) {
        binding.rvAppointments.visibility = View.GONE
        binding.pbAppointments.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val newListAppointment = when {
                setMinDate && setMaxDate -> { dbPatientsPortal.readPatientHistoryAppointments(getPatient(requireActivity(), requireContext()).idPatient, selectedSinceDate, selectedUntilDate) }
                setMinDate -> { dbPatientsPortal.readPatientHistoryAppointments(getPatient(requireActivity(), requireContext()).idPatient, minDate = selectedSinceDate) }
                setMaxDate -> { dbPatientsPortal.readPatientHistoryAppointments(getPatient(requireActivity(), requireContext()).idPatient, maxDate = selectedUntilDate) }
                else -> { dbPatientsPortal.readPatientHistoryAppointments(getPatient(requireActivity(), requireContext()).idPatient) }
            }
            withContext(Dispatchers.Main) {
                if (newListAppointment.size > 0) {
                    if (!rvAppointments.isComputingLayout) {
                        adapterHistoryAppointments.updateData(newListAppointment)
                        adapterHistoryAppointments.notifyDataSetChanged()
                        binding.pbAppointments.visibility = View.GONE
                        binding.rvAppointments.visibility = View.VISIBLE
                    }
                } else {
                    binding.pbAppointments.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun cleanFilters() {
        selectedSinceDate = Calendar.getInstance()
        setMinDate = false
        selectedUntilDate = Calendar.getInstance()
        setMaxDate = false
        binding.btnSince.text = null
        binding.btnUntil.text = null
    }

    private fun openSinceCalendar(btnSelected: TextView) {
        val cal: Calendar = Calendar.getInstance()
        val yearGetter = cal.get(Calendar.YEAR)
        val monthGetter = cal.get(Calendar.MONTH)
        val dayGetter = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), 0,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                setMinDate = true
                selectedSinceDate = cal

                val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(cal.time)

                btnSelected.text = formattedDate
            }, yearGetter, monthGetter, dayGetter
        )
        if (setMaxDate) {
            val maxDate = selectedUntilDate
            dpd.datePicker.maxDate = maxDate.timeInMillis
        } else {
            dpd.datePicker.maxDate = cal.timeInMillis
        }
        dpd.show()
    }

    private fun openUntilCalendar(btnSelected: TextView) {
        val cal: Calendar = Calendar.getInstance()
        val yearGetter = cal.get(Calendar.YEAR)
        val monthGetter = cal.get(Calendar.MONTH)
        val dayGetter = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), 0,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                setMaxDate = true
                selectedUntilDate = cal

                val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(cal.time)

                btnSelected.text = formattedDate
            }, yearGetter, monthGetter, dayGetter
        )

        if (setMinDate) {
            val minDate = selectedSinceDate
            dpd.datePicker.minDate = minDate.timeInMillis
        }
        dpd.datePicker.maxDate = cal.timeInMillis
        dpd.show()
    }


}