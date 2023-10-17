package com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.appointmentsAdapters.AppointmentsPagerAdapter
import com.example.patientsportal.databinding.FragmentAppointmentsStep4Binding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.fragments.PageAppointmentFragment
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.NotificationsReceiver
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AppointmentsStep4 : Fragment(R.layout.fragment_appointments_step4), OnAppointmentSelected, OnAddPageAppointmentClicked {

    private lateinit var binding: FragmentAppointmentsStep4Binding
    private var appointmentSelected = Appointment()
    private lateinit var vpaAdapter: AppointmentsPagerAdapter
    private var minDays = 0
    private var maxDays = 10
    private var enabledNextStep = false

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppointmentsStep4Binding.bind(view)


        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        vpaAdapter = AppointmentsPagerAdapter(requireActivity())
        binding.viewPagerAppointments.adapter = vpaAdapter
        vpaAdapter.addPage(setNewPageToViewPager())
        binding.viewPagerAppointments.setCurrentItem(vpaAdapter.itemCount - 1, true)
        binding.btnNextStep.setOnClickListener {
            if (enabledNextStep) {
                showConfirmAppointmentDialog()
            } else {
                Toast.makeText(requireContext(), "No se pudio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setNewPageToViewPager(): PageAppointmentFragment {
        val paf = PageAppointmentFragment()
        val idDoctorSpeciality = arguments?.getIntArray(getString(R.string.iddoctorspecialityselected_tag))
        val idPlace = arguments?.getIntArray(getString(R.string.idplaceselected_tag))
        if (idDoctorSpeciality != null && idPlace != null) {
            val bundle = Bundle()
            paf.onAppointmentSelected = this
            paf.onAddPageAppointmentClicked = this
            bundle.putIntArray(getString(R.string.iddoctorspecialityselected_tag), idDoctorSpeciality)
            bundle.putIntArray(getString(R.string.idplaceselected_tag), idPlace)
            bundle.putInt(getString(R.string.mindays_tag), minDays)
            bundle.putInt(getString(R.string.maxdays_tag), maxDays)
            bundle.putInt(getString(R.string.numberpage_tag), (vpaAdapter.itemCount))
            paf.arguments = bundle
            minDays = maxDays
            maxDays += 10
        }
        return paf
    }


    private fun enableNextStep(b: Boolean) {
        binding.btnNextStep.alpha = if (b) { 1F } else { 0.5F }
        enabledNextStep = b
    }

    override fun onAppointmentSelected(appointment: Appointment) {
        appointmentSelected = appointment
        enableNextStep(true)
    }

    override fun onAppointmentDeselected() {
        appointmentSelected = Appointment()
        enableNextStep(false)
    }

    override fun undoCheckOnOtherPages(numberPage: Int) {
        for (i in 0 until vpaAdapter.itemCount) {
            if (i != numberPage) {
                vpaAdapter.getFragment(i).updateAdapter(-1)
            }
        }
    }

    override fun onAddPageAppointmentClicked() {
        vpaAdapter.addPage(setNewPageToViewPager())
        binding.viewPagerAppointments.setCurrentItem(vpaAdapter.itemCount - 1, true)
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage(getString(R.string.hubo_un_error_asignando_el_turno_intente_nuevamente))
            setNegativeButton(getString(R.string.volver), null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showConfirmAppointmentDialog() {
        val dbPatientsPortal = DbPatientsPortal(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage(getString(R.string.desea_confirmar_este_turno))
            setPositiveButton(getString(R.string.si)) { _, _ ->
                binding.tvSaveAppointment.visibility = View.INVISIBLE
                binding.pbSaveAppointment.visibility = View.VISIBLE
                val idPCP = arguments?.getInt(getString(R.string.idpcpselected_tag))
                val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
                if (idPCP != null && idContainer != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val saveAppointment = dbPatientsPortal.assignAppointmentToPatient(appointmentSelected, idPCP, getPatient(requireActivity(), requireContext()).idPatient)
                        withContext(Dispatchers.Main) {
                            binding.pbSaveAppointment.visibility = View.INVISIBLE
                            binding.tvSaveAppointment.visibility = View.VISIBLE
                            if (saveAppointment) {
                                val notiSet = dbPatientsPortal.readPatientNotificationSettings(getPatient(requireActivity(), requireContext()).idPatient)
                                if (notiSet.medicalTestPush == "true") {
                                    val reminders = arrayOf(notiSet.medicalTestReminderOne, notiSet.medicalTestReminderTwo, notiSet.medicalTestReminderThree, notiSet.medicalTestReminderFour, notiSet.medicalTestReminderFive)
                                    val remindersSorted = reminders.sortedDescending()
                                    remindersSorted.forEach {
                                        if (it > 0) {
                                            val formato = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                                            val fechaDate: Date = formato.parse(appointmentSelected.date)!!
                                            val calendar = Calendar.getInstance()
                                            calendar.time = fechaDate
                                            calendar.add(Calendar.DAY_OF_MONTH, -it)
                                            val notificationDate = calendar.time


                                            val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                            createChannel(notificationManager)
                                            val intent = Intent(requireContext(), NotificationsReceiver::class.java)
                                            intent.putExtra(getString(R.string.title_tag), getString(R.string.recordatorio_turno_title))
                                            intent.putExtra(getString(R.string.message_tag), "El ${appointmentSelected.date} con el Dr. ${appointmentSelected.doctorSpeciality.doctor.name} ${appointmentSelected.doctorSpeciality.doctor.lastName} en ${appointmentSelected.place.address}")
                                            intent.putExtra(getString(R.string.idpatient_tag), getPatient(requireActivity(), requireContext()).idPatient)
                                            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                                            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                            alarmManager.set(AlarmManager.RTC, notificationDate.time, pendingIntent)
                                        }
                                    }
                                }
                                showFragmentFromFragment(requireActivity(), AppointmentsStep5(), getString(R.string.appointmentsstep5_tag), requireContext(), idContainer, appointmentCreatedOrDeleted = true)
                            } else {
                                showErrorDialog()
                            }
                        }
                    }
                }
            }
            setNegativeButton(getString(R.string.no), null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun createChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            1.toString(),
            "Nuevo turno",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}
