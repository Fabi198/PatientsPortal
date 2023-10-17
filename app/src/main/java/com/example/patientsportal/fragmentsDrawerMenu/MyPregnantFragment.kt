package com.example.patientsportal.fragmentsDrawerMenu

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.databinding.CustomAlertDialogFetusWeekBinding
import com.example.patientsportal.databinding.CustomAlertDialogSetNewLaborDateBinding
import com.example.patientsportal.databinding.FragmentMyPregnantBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.fragments.PregnantContractionsFragment
import com.example.patientsportal.fragments.PregnantDiaryFragment
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.HideKeyboard
import com.example.patientsportal.objects.ShowFragment.showFragmentFromFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale


@Suppress("DEPRECATION")
class MyPregnantFragment : Fragment(R.layout.fragment_my_pregnant) {

    private lateinit var binding: FragmentMyPregnantBinding
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private lateinit var newDate: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPregnantBinding.bind(view)
        setHasOptionsMenu(true)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))
        dbPatientsPortal = DbPatientsPortal(requireContext())
        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }


        if (dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient) > 0) {
            setPregnancyView(idContainer)
        } else {
            setNotPregnancyYetView(idContainer)
        }



    }

    private fun setNotPregnancyYetView(idContainer: Int?) {
        binding.notPregnancyYetView.visibility = View.VISIBLE
        Picasso.get().load("https://hiba.hospitalitaliano.org.ar/archivos/noticias_archivos/53/archivos/M40%20-%20final.png").fit().into(binding.ivPregnancy)

        binding.etStartPregnancyDate.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCorrectDate(s, start, before, count, binding.etStartPregnancyDate)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPositivePregnancyDate.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCorrectDate(s, start, before, count, binding.etPositivePregnancyDate)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnCreatePregnancy.setOnClickListener {
            if (binding.tvCreatePregnancy.text.toString() == getString(R.string.establecer_fechas_de_embarazo)) {
                val animFadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                val animFadeOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                animFadeOut.duration = 700
                animFadeIn.duration = 700
                animFadeOut.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.tvCreatePregnancy.text = getString(R.string.crear_modulo_de_embarazo)
                        binding.tvWelcomeTextPregnancy.visibility = View.GONE
                        binding.cvStartPregnancyDate.visibility = View.VISIBLE
                        binding.cvPositivePregnancyDate.visibility = View.VISIBLE
                        binding.cvStartPregnancyDate.startAnimation(animFadeIn)
                        binding.cvPositivePregnancyDate.startAnimation(animFadeIn)
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                binding.tvWelcomeTextPregnancy.startAnimation(animFadeOut)
            } else if (binding.tvCreatePregnancy.text.toString() == getString(R.string.crear_modulo_de_embarazo)) {
                if (binding.etStartPregnancyDate.text.length == 10 && binding.etPositivePregnancyDate.text.length == 10) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (dbPatientsPortal.createPatientPregnancy(getPatient(requireActivity(), requireContext()).idPatient, binding.etStartPregnancyDate.text.toString(), binding.etPositivePregnancyDate.text.toString()) > 0) {
                            withContext(Dispatchers.Main) {
                                binding.notPregnancyYetView.visibility = View.GONE
                                binding.pbPregnancy.visibility = View.VISIBLE
                                Handler().postDelayed({ setPregnancyView(idContainer) }, 700)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCorrectDate(s: CharSequence?, start: Int, before: Int, count: Int, editText: EditText) {
        val newText = s?.let { StringBuilder(it) }

        when {
            count == 1 && start == 1 -> {
                val dayDigits = s?.subSequence(0, 2)?.toString()?.toIntOrNull()
                if (!(dayDigits != null && dayDigits >= 1 && dayDigits <= 31)) {
                    editText.error = "Día inválido"
                } else {
                    newText?.insert(2, "/")
                    editText.setText(newText)
                    editText.setSelection(editText.text.length)
                }
            }
            count == 1 && start == 4 -> {
                val dayDigits = s?.subSequence(3, 5)?.toString()?.toIntOrNull()
                if (!(dayDigits != null && dayDigits >= 1 && dayDigits <= 12)) {
                    editText.error = "Mes inválido"
                } else {
                    newText?.insert(5, "/")
                    editText.setText(newText)
                    editText.setSelection(editText.text.length)
                }
            }
            before == 1 && start == 6 -> {
                newText?.deleteCharAt(5)
                editText.setText(newText)
                editText.setSelection(editText.text.length)
            }
            before == 1 && start == 3 -> {
                newText?.deleteCharAt(2)
                editText.setText(newText)
                editText.setSelection(editText.text.length)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPregnancyView(idContainer: Int?) {
        binding.pregnancyView.visibility = View.VISIBLE
        binding.pbPregnancy.visibility = View.GONE
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMyPregnancy)
        (activity as AppCompatActivity).title = null
        binding.toolbarMyPregnancy.inflateMenu(R.menu.mypregnant_menu)

        binding.btnFetusWeek05.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_05, getString(R.string.semana_5), getString(R.string.description_fetus_week_05)) }
        binding.btnFetusWeek10.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_10, getString(R.string.semana_10), getString(R.string.description_fetus_week_10)) }
        binding.btnFetusWeek15.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_15, getString(R.string.semana_15), getString(R.string.description_fetus_week_15)) }
        binding.btnFetusWeek20.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_20, getString(R.string.semana_20), getString(R.string.description_fetus_week_20)) }
        binding.btnFetusWeek25.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_25, getString(R.string.semana_25), getString(R.string.description_fetus_week_25)) }
        binding.btnFetusWeek30.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_30, getString(R.string.semana_30), getString(R.string.description_fetus_week_30)) }
        binding.btnFetusWeek35.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_35, getString(R.string.semana_35), getString(R.string.description_fetus_week_35)) }
        binding.btnFetusWeek40.setOnClickListener { showFetusWeekDialog(R.drawable.fetus_week_40, getString(R.string.semana_40), getString(R.string.description_fetus_week_40)) }

        if (idContainer != null) {
            setCVLabor()
            binding.cvLaborDate.setOnClickListener { showNewDateCustomAlertDialog() }
            binding.cvDiary.setOnClickListener { showFragmentFromFragment(requireActivity(), PregnantDiaryFragment(), getString(R.string.pregnantdiaryfragment_tag), requireContext(), idContainer) }
            binding.cvContractions.setOnClickListener { showFragmentFromFragment(requireActivity(), PregnantContractionsFragment(), getString(R.string.pregnantcontractionsfragment_tag), requireContext(), idContainer) }
        }
    }

    private fun showNewDateCustomAlertDialog() {
        val binding = CustomAlertDialogSetNewLaborDateBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        binding.etNewDate.setOnClickListener { openCalendar(binding.etNewDate) }
        binding.btnCancel.setOnClickListener { alertDialog.dismiss() }
        binding.btnSave.setOnClickListener {
            if (newDate.isNotEmpty()) {
                if (dbPatientsPortal.updateFinishPregnancyDate(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), newDate)) {
                    setCVLabor()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setCVLabor() {
        val patientPregnancy = dbPatientsPortal.readPatientPregnancy(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()
        val daysRemaining = ChronoUnit.DAYS.between(today, LocalDate.parse(patientPregnancy.finishPregnancyDate, formatter))
        val weeksRemaining = daysRemaining / 7
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd\nMMM", Locale.getDefault())
        val outputFormat2 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvMinimalDate.text = outputFormat.format(inputFormat.parse(patientPregnancy.finishPregnancyDate)!!).replace(".", "")
        binding.tvFullLaborDate.text = "${getString(R.string.fecha_de_parto)} ${outputFormat2.format(inputFormat.parse(patientPregnancy.finishPregnancyDate)!!)}"
        binding.tvTimeRemain.text = getString(R.string.tiempo_restante_fecha_parto, weeksRemaining, daysRemaining % 7)
    }

    private fun showFetusWeekDialog(ivFetus: Int, tvFetus: String, tvDescFetus: String) {
        val binding = CustomAlertDialogFetusWeekBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        binding.ivFetus.setImageResource(ivFetus)
        binding.tvFetus.text = tvFetus
        binding.tvDescFetus.text = tvDescFetus
        alertDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mypregnant_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemDeletePregnantModule) {
            if (dbPatientsPortal.deletePatientPregnancy(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openCalendar(etNewDate: EditText) {
        HideKeyboard.hideKeyboardOnFragment(requireActivity(), binding.rlMain)
        val cal: Calendar = Calendar.getInstance()
        val yearGetter = cal.get(Calendar.YEAR)
        val monthGetter = cal.get(Calendar.MONTH)
        val dayGetter = cal.get(Calendar.DAY_OF_MONTH)
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val dpd = DatePickerDialog(requireContext(), 0,
            { _, year, month, dayOfMonth ->
                lateinit var fecha: String
                if ((month+1) in 0..9 && dayOfMonth in 10..31) {
                    fecha = "$year/0${month+1}/$dayOfMonth"
                } else if ((month+1) in 0..9 && dayOfMonth in 0..9) {
                    fecha = "$year/0${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 0..9) {
                    fecha = "$year/${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 10..31) {
                    fecha = "$year/${month+1}/$dayOfMonth"
                }
                etNewDate.setText(outputFormat.format(inputFormat.parse(fecha)!!))
                newDate = outputFormat.format(inputFormat.parse(fecha)!!)
            }, yearGetter, monthGetter, dayGetter)
        dpd.datePicker.minDate = cal.timeInMillis
        dpd.show()
    }
}