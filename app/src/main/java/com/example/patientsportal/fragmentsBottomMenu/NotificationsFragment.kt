package com.example.patientsportal.fragmentsBottomMenu

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.NotificationAdapter
import com.example.patientsportal.databinding.CustomAlertDialogNotificationsBinding
import com.example.patientsportal.databinding.FragmentNotificationsBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Notification
import com.example.patientsportal.objects.DateConverter.calculateDateDifference
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private lateinit var btnNotificationsSettings: ImageButton
    private lateinit var adapter: NotificationAdapter
    private lateinit var listNotis: ArrayList<Notification>
    lateinit var refreshBadgeNoti: RefreshBadgeNoti

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        val idNotification = arguments?.getInt(getString(R.string.idnotification_tag))

        if (idContainer != null) {

            if (idNotification != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    dbPatientsPortal = DbPatientsPortal(requireContext())
                    val noti = dbPatientsPortal.readPatientNotification(idNotification)
                    withContext(Dispatchers.Main) {
                        if (noti.idNotification > 0) {
                            showNotiDialog(noti, -1)
                        }
                    }
                }
            }


            btnNotificationsSettings = requireActivity().findViewById(R.id.btnNotificationsSettings)

            btnNotificationsSettings.visibility = View.VISIBLE
            btnNotificationsSettings.setOnClickListener {
                showFragmentFromFragment(requireActivity(), NotificationsSettingsFragment(), "NotificationsSettingsFragment", requireContext(), idContainer)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                dbPatientsPortal = DbPatientsPortal(requireContext())
                listNotis = dbPatientsPortal.readAllUnReadedPatientsNotifications(getPatient(requireActivity(), requireContext()).idPatient)
                withContext(Dispatchers.Main) {
                    if (listNotis.size > 0) {
                        binding.pbAppointments.visibility = View.GONE
                        adapter = NotificationAdapter(listNotis) { noti, i -> showNotiDialog(noti, i) }
                        binding.rvNotifications.adapter = adapter
                        binding.rvNotifications.visibility = View.VISIBLE
                    } else {
                        binding.pbAppointments.visibility = View.GONE
                        binding.tvNoRegisters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showNotiDialog(noti: Notification, i: Int) {
        val binding = CustomAlertDialogNotificationsBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setOnDismissListener { if (i > -1) { listNotis.removeAt(i); updateNotis(noti, i) } }
            .create()
        val image = when (noti.title) {
            "Estudios" -> { R.drawable.baseline_description_24 }
            "Portal de Salud" -> { R.drawable.icon_main }
            "Medicamentos" -> { R.drawable.icon_drugs }
            "Derivaciones" -> { R.drawable.icon_doctors }
            else -> { R.drawable.baseline_help_24 }
        }
        binding.ivNoti.setImageResource(image)
        binding.tvTitle.text = noti.title
        binding.tvDate.text = noti.date
        binding.tvDateUntilToday.text = calculateDateDifference(noti.date)
        binding.tvDescription.text = noti.description
        binding.cvCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun updateNotis(noti: Notification, position: Int) {
        if (!binding.rvNotifications.isComputingLayout) {
            val dbPatientsPortal = DbPatientsPortal(requireContext())
            if (dbPatientsPortal.updatePatientNotification(noti.idNotification)) {
                refreshBadgeNoti.refreshBadgeNoti()
                adapter.notifyItemRemoved(position)
                adapter.updateData(listNotis)
                if (listNotis.size < 1) {
                    binding.rvNotifications.visibility = View.GONE
                    binding.tvNoRegisters.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), "Hubo un error al leer la notificación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        btnNotificationsSettings.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        btnNotificationsSettings.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        btnNotificationsSettings.visibility = View.GONE
    }

}