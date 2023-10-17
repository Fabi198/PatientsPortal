package com.example.patientsportal.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.adapters.pregnancyAdapters.PregnancyWeekAdapter
import com.example.patientsportal.databinding.CustomAlertDialogPregnancyWeekBinding
import com.example.patientsportal.databinding.FragmentPregnantDiaryBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.PregnancyWeek
import com.example.patientsportal.objects.DateConverter.generateWeekDates
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.PregnancyWeeks.getPregnancyWeeks
import com.example.patientsportal.objects.ShowFragment
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Suppress("DEPRECATION")
class PregnantDiaryFragment : Fragment(R.layout.fragment_pregnant_diary) {

    private lateinit var binding: FragmentPregnantDiaryBinding
    private lateinit var adapter: PregnancyWeekAdapter
    private lateinit var dbPatientsPortal: DbPatientsPortal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPregnantDiaryBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        if (idContainer != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                dbPatientsPortal = DbPatientsPortal(requireContext())
                val listPregnancyWeek = getPregnancyWeeks()
                val listWeekDates = generateWeekDates(dbPatientsPortal.readPatientPregnancy(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient)).startPregnancyDate)
                withContext(Dispatchers.Main) {
                    if (listWeekDates.size > 0) {
                        listPregnancyWeek.forEachIndexed { i, pw ->
                            pw.pregnancyTestDate = listWeekDates[i].pregnancyTestDate
                            pw.motherChangesDate = listWeekDates[i].motherChangesDate
                            pw.babyDevelopmentDate = listWeekDates[i].babyDevelopmentDate
                        }

                        Handler().postDelayed({
                            binding.pbPregnancyDiary.visibility = View.GONE
                            binding.btnGoToPresentWeek.visibility = View.VISIBLE
                            binding.rvPregnancyWeeks.visibility = View.VISIBLE
                            binding.fabPregnancyDiary.visibility = View.VISIBLE

                            adapter = PregnancyWeekAdapter(listPregnancyWeek, requireContext()) { pw, cv, _ ->
                                showPregnancyWeekAlertDialog(pw, cv)
                            }

                            binding.rvPregnancyWeeks.adapter = adapter
                            binding.rvPregnancyWeeks.smoothScrollToPosition(goToPresentWeek(listPregnancyWeek))

                            binding.btnGoToPresentWeek.setOnClickListener {
                                binding.rvPregnancyWeeks.smoothScrollToPosition(goToPresentWeek(listPregnancyWeek))
                            }
                        }, 700)
                    } else {
                        binding.pbPregnancyDiary.visibility = View.GONE
                    }
                }
            }

            setFABMenu(idContainer)
        }
    }

    private fun goToPresentWeek(listPregnancyWeek: ArrayList<PregnancyWeek>): Int {
        var i = 0
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()

        // Establecer la hora, minuto, segundo y milisegundo a 0
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        for (pw in listPregnancyWeek) {
            val testDate = inputFormat.parse(pw.pregnancyTestDate)

            if (testDate != null && testDate > currentDate.time) {
                i = pw.numberWeek - 1
                break
            }
        }
        return i
    }


    private fun setFABMenu(idContainer: Int) {
        binding.fabPregnancyDiary.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() { binding.modalBackground.visibility = View.VISIBLE }
            override fun onMenuCollapsed() { binding.modalBackground.visibility = View.GONE }
        })
        binding.loadNewPregnancyFeel.setOnClickListener {
            ShowFragment.showFragmentFromFragment(
                requireActivity(),
                PregnantNotesAndPhotosFragment(),
                getString(R.string.pregnancynotesandphotosfragment_tag),
                requireContext(),
                idContainer,
                title = getString(R.string.feels_tag)
            )
            binding.fabPregnancyDiary.collapse()
        }
        binding.loadNewPregnancyAppointmentNote.setOnClickListener {
            ShowFragment.showFragmentFromFragment(
                requireActivity(),
                PregnantNotesAndPhotosFragment(),
                getString(R.string.pregnancynotesandphotosfragment_tag),
                requireContext(),
                idContainer,
                title = getString(R.string.appointmentsnotes_tag)
            )
            binding.fabPregnancyDiary.collapse()
        }
        binding.loadNewPregnancyPhoto.setOnClickListener {
            ShowFragment.showFragmentFromFragment(
                requireActivity(),
                PregnantNotesAndPhotosFragment(),
                getString(R.string.pregnancynotesandphotosfragment_tag),
                requireContext(),
                idContainer,
                title = getString(R.string.photos_tag)
            )
            binding.fabPregnancyDiary.collapse()
        }
        binding.loadNewPregnancyNote.setOnClickListener {
            ShowFragment.showFragmentFromFragment(
                requireActivity(),
                PregnantNotesAndPhotosFragment(),
                getString(R.string.pregnancynotesandphotosfragment_tag),
                requireContext(),
                idContainer,
                title = getString(R.string.notes_tag)
            )
            binding.fabPregnancyDiary.collapse()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPregnancyWeekAlertDialog(pw: PregnancyWeek, cv: String) {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val binding = CustomAlertDialogPregnancyWeekBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        binding.tvDate.text = when (cv) {
            "cvPregnancyTest" -> { outputFormat.format(inputFormat.parse(pw.pregnancyTestDate)!!) }
            "cvMotherChanges" -> { outputFormat.format(inputFormat.parse(pw.motherChangesDate)!!) }
            "cvBabyDevelopment" -> { outputFormat.format(inputFormat.parse(pw.babyDevelopmentDate)!!) }
            else -> {""}
        }
        binding.tvWeek.text = "Semana ${pw.numberWeek}"
        binding.tvDesc.text = when (cv) {
            "cvPregnancyTest" -> { Html.fromHtml(pw.pregnancyTestText, Html.FROM_HTML_MODE_COMPACT) }
            "cvMotherChanges" -> { Html.fromHtml(pw.motherChangesText, Html.FROM_HTML_MODE_COMPACT) }
            "cvBabyDevelopment" -> { Html.fromHtml(pw.babyDevelopmentText, Html.FROM_HTML_MODE_COMPACT) }
            else -> {""}
        }
        val url = when (cv) {
            "cvPregnancyTest" -> { pw.pregnancyTestImageUrl }
            "cvMotherChanges" -> { pw.motherChangesImageUrl }
            "cvBabyDevelopment" -> { pw.babyDevelopmentImageUrl }
            else -> { "" }
        }
        Picasso.get().load(url).into(binding.ivPW)
        binding.svMain.visibility = View.VISIBLE
        alertDialog.show()
    }


}