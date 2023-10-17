package com.example.patientsportal.fragmentsBottomMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentNotificationsSettingsBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.NotificationsSettings
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
class NotificationsSettingsFragment : Fragment(R.layout.fragment_notifications_settings) {

    private lateinit var binding: FragmentNotificationsSettingsBinding
    private lateinit var cardClicked: CardView
    private lateinit var notiSet: NotificationsSettings
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private lateinit var animLeftOut: Animation
    private lateinit var animLeftIn: Animation
    private lateinit var animRightIn: Animation
    private lateinit var animRightOut: Animation
    private lateinit var saNotiEnable: String
    private lateinit var saNotiPush: String
    private lateinit var saNotiEmail: String
    private lateinit var saReminderOne: String
    private lateinit var saReminderTwo: String
    private lateinit var saReminderThree: String
    private lateinit var saReminderFour: String
    private lateinit var saReminderFive: String

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsSettingsBinding.bind(view)
        val idContainer = arguments?.getInt(getString(R.string.containerid_tag))

        if (idContainer != null) {
            animLeftOut = AnimationUtils.loadAnimation(requireContext(), R.anim.move_left_out)
            animRightIn = AnimationUtils.loadAnimation(requireContext(), R.anim.move_right_in)
            animLeftIn = AnimationUtils.loadAnimation(requireContext(), R.anim.move_left_in)
            animRightOut = AnimationUtils.loadAnimation(requireContext(), R.anim.move_right_out)

            binding.btnBack.setOnClickListener {
                if (binding.llCardsViews.visibility == View.GONE) {
                    cardClicked = CardView(requireContext())
                    binding.clEditView.startAnimation(animRightOut)
                    binding.clEditView.visibility = View.GONE
                    Handler().postDelayed({
                        binding.clOtherOptions.visibility = View.GONE
                        binding.clReminders.visibility = View.GONE
                    }, 350)
                    setOptionsEnabledText()
                    binding.llCardsViews.visibility = View.VISIBLE
                    binding.llCardsViews.startAnimation(animLeftIn)
                } else {
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }

            lifecycleScope.launch(Dispatchers.IO) {
                dbPatientsPortal = DbPatientsPortal(requireContext())
                notiSet = dbPatientsPortal.readPatientNotificationSettings(getPatient(requireActivity(), requireContext()).idPatient)
                withContext(Dispatchers.Main) {
                    setOptionsEnabledText()
                    setMailText()
                }
            }

            binding.cvMedicalTest.setOnClickListener { cardClicked = binding.cvMedicalTest; showEditView(cardClicked) }
            binding.cvDrugs.setOnClickListener { cardClicked = binding.cvDrugs; showEditView(cardClicked) }
            binding.cvDerivations.setOnClickListener { cardClicked = binding.cvDerivations; showEditView(cardClicked) }
            binding.cvPatientsPortal.setOnClickListener { cardClicked = binding.cvPatientsPortal; showEditView(cardClicked) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOptionsEnabledText() {
        binding.tvMTEnabledOptions.text = "${if (notiSet.medicalTestPush == "true") { "Push (Aviso en el celular)" } else { "" } } ${if (notiSet.medicalTestEmail == "true") { "Correo electrónico" } else { "" } }"
        binding.tvDrugsEnabledOptions.text = "${if (notiSet.drugsPush == "true") { "Push (Aviso en el celular)" } else { "" } } ${if (notiSet.drugsEmail == "true") { "Correo electrónico" } else { "" } }"
        binding.tvDeriEnabledOptions.text = "${if (notiSet.derivationsPush == "true") { "Push (Aviso en el celular)" } else { "" } } ${if (notiSet.derivationsEmail == "true") { "Correo electrónico" } else { "" } }"
        binding.tvPPEnabledOptions.text = "${if (notiSet.patientsPortalPush == "true") { "Push (Aviso en el celular)" } else { "" } } ${if (notiSet.patientsPortalEmail == "true") { "Correo electrónico" } else { "" } }"
    }

    private fun setMailText() {
        val email = getPatient(requireActivity(), requireContext()).email
        val text = "${getString(R.string.este_es_el_mail_que_verificamos_y_al_que_nos_comunicaremos)}$email"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf(email)
        val endIndex = startIndex + email.length
        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.blue, null))
        spannableString.setSpan(colorSpan, startIndex, endIndex, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        binding.tvEmailDescNoti.text = spannableString
    }

    private fun showEditView(cardSelected: CardView) {
        binding.llCardsViews.startAnimation(animLeftOut)
        binding.llCardsViews.visibility = View.GONE

        when (cardSelected) {
            binding.cvMedicalTest -> {
                saNotiEnable = getString(R.string.medicaltestenabled)
                saNotiPush = getString(R.string.medicaltestpush)
                saNotiEmail = getString(R.string.medicaltestemail)
                saReminderOne = getString(R.string.medicaltestreminderone)
                saReminderTwo = getString(R.string.medicaltestremindertwo)
                saReminderThree = getString(R.string.medicaltestreminderthree)
                saReminderFour = getString(R.string.medicaltestreminderfour)
                saReminderFive = getString(R.string.medicaltestreminderfive)
            }
            binding.cvDrugs -> {
                saNotiEnable = getString(R.string.drugsenabled)
                saNotiPush = getString(R.string.drugspush)
                saNotiEmail = getString(R.string.drugsemail)
                saReminderOne = getString(R.string.drugsreminderone)
                saReminderTwo = getString(R.string.drugsremindertwo)
                saReminderThree = getString(R.string.drugsreminderthree)
                saReminderFour = getString(R.string.drugsreminderfour)
                saReminderFive = getString(R.string.drugsreminderfive)
            }
            binding.cvDerivations -> {
                saNotiEnable = getString(R.string.derivationsenabled)
                saNotiPush = getString(R.string.derivationspush)
                saNotiEmail = getString(R.string.derivationsemail)
            }
            binding.cvPatientsPortal -> {
                saNotiEnable = getString(R.string.patientsportalenabled)
                saNotiPush = getString(R.string.patientsportalpush)
                saNotiEmail = getString(R.string.patientsportalemail)
            }
        }

        binding.swEnableNoti.isChecked = when (cardSelected) {
            binding.cvMedicalTest -> { notiSet.medicalTestEnabled == "true" }
            binding.cvDrugs -> { notiSet.drugsEnabled == "true" }
            binding.cvDerivations -> { notiSet.derivationsEnabled == "true" }
            binding.cvPatientsPortal -> { notiSet.patientsPortalEnabled == "true" }
            else -> { false }
        }
        if (binding.swEnableNoti.isChecked) {
            if (cardSelected == binding.cvMedicalTest || cardSelected == binding.cvDrugs) {
                binding.clReminders.visibility = View.VISIBLE
            }
            binding.clOtherOptions.visibility = View.VISIBLE
        }
        binding.swEnableNoti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiEnable, true, 0)) {
                    if (cardSelected == binding.cvMedicalTest || cardSelected == binding.cvDrugs) {
                        binding.clReminders.visibility = View.VISIBLE
                    }
                    binding.clOtherOptions.visibility = View.VISIBLE
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestEnabled = "true" }
                        binding.cvDrugs -> { notiSet.drugsEnabled = "true" }
                        binding.cvDerivations -> { notiSet.derivationsEnabled = "true" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalEnabled = "true" }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swEnableNoti.isChecked = false
                }
            } else {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiEnable, false, 0)) {
                    binding.clReminders.visibility = View.GONE
                    binding.clOtherOptions.visibility = View.GONE
                    when (cardSelected) {
                        binding.cvMedicalTest -> {
                            notiSet.medicalTestEnabled = "false"
                            notiSet.medicalTestPush = "false"
                            notiSet.medicalTestEmail = "false"
                        }
                        binding.cvDrugs -> {
                            notiSet.drugsEnabled = "false"
                            notiSet.drugsPush = "false"
                            notiSet.drugsEmail = "false"
                        }
                        binding.cvDerivations -> {
                            notiSet.derivationsEnabled = "false"
                            notiSet.derivationsPush = "false"
                            notiSet.derivationsEmail = "false"
                        }
                        binding.cvPatientsPortal -> {
                            notiSet.patientsPortalEnabled = "false"
                            notiSet.patientsPortalPush = "false"
                            notiSet.patientsPortalEmail = "false"
                        }
                    }
                    binding.swPushNoti.isChecked = when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestPush == "true" }
                        binding.cvDrugs -> { notiSet.drugsPush == "true" }
                        binding.cvDerivations -> { notiSet.derivationsPush == "true" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalPush == "true" }
                        else -> { false }
                    }
                    binding.swEmailNoti.isChecked = when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestEmail == "true" }
                        binding.cvDrugs -> { notiSet.drugsEmail == "true" }
                        binding.cvDerivations -> { notiSet.derivationsEmail == "true" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalEmail == "true" }
                        else -> { false }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swEnableNoti.isChecked = true
                }
            }
        }
        binding.swPushNoti.isChecked = when (cardSelected) {
            binding.cvMedicalTest -> { notiSet.medicalTestPush == "true" }
            binding.cvDrugs -> { notiSet.drugsPush == "true" }
            binding.cvDerivations -> { notiSet.derivationsPush == "true" }
            binding.cvPatientsPortal -> { notiSet.patientsPortalPush == "true" }
            else -> { false }
        }
        binding.swPushNoti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiPush, true, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestPush = "true" }
                        binding.cvDrugs -> { notiSet.drugsPush = "true" }
                        binding.cvDerivations -> { notiSet.derivationsPush = "true" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalPush = "true" }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swPushNoti.isChecked = false
                }
            } else {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiPush, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestPush = "false" }
                        binding.cvDrugs -> { notiSet.drugsPush = "false" }
                        binding.cvDerivations -> { notiSet.derivationsPush = "false" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalPush = "false" }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swPushNoti.isChecked = true
                }
            }
        }
        binding.swEmailNoti.isChecked = when (cardSelected) {
            binding.cvMedicalTest -> { notiSet.medicalTestEmail == "true" }
            binding.cvDrugs -> { notiSet.drugsEmail == "true" }
            binding.cvDerivations -> { notiSet.derivationsEmail == "true" }
            binding.cvPatientsPortal -> { notiSet.patientsPortalEmail == "true" }
            else -> { false }
        }
        binding.swEmailNoti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiEmail, true, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestEmail = "true" }
                        binding.cvDrugs -> { notiSet.drugsEmail = "true" }
                        binding.cvDerivations -> { notiSet.derivationsEmail = "true" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalEmail = "true" }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swEmailNoti.isChecked = false
                }
            } else {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saNotiEmail, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestEmail = "false" }
                        binding.cvDrugs -> { notiSet.drugsEmail = "false" }
                        binding.cvDerivations -> { notiSet.derivationsEmail = "false" }
                        binding.cvPatientsPortal -> { notiSet.patientsPortalEmail = "false" }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    binding.swEmailNoti.isChecked = true
                }
            }
        }

        if (cardSelected == binding.cvMedicalTest || cardSelected == binding.cvDrugs) {
            val arrayOptions = Array(30) { (it + 1).toString() }
            val adapterReminder = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOptions)
            adapterReminder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(binding.countSelecterOne) {
                adapter = adapterReminder
                when (cardSelected) {
                    binding.cvMedicalTest -> { setSelection(if (notiSet.medicalTestReminderOne > 0) { notiSet.medicalTestReminderOne-1 } else { 1 }) }
                    binding.cvDrugs -> { setSelection(if (notiSet.drugsReminderOne > 0) { notiSet.drugsReminderOne-1 } else { 1 }) }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderOne, true, Integer.parseInt(binding.countSelecterOne.selectedItem.toString()))) {
                            when (cardSelected) {
                                binding.cvMedicalTest -> { notiSet.medicalTestReminderOne = Integer.parseInt(binding.countSelecterOne.selectedItem.toString()) }
                                binding.cvDrugs -> { notiSet.drugsReminderOne = Integer.parseInt(binding.countSelecterOne.selectedItem.toString()) }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                            setSelection(1)
                        }
                    }
                }
            }
            with(binding.countSelecterTwo) {
                adapter = adapterReminder
                when (cardSelected) {
                    binding.cvMedicalTest -> { setSelection(if (notiSet.medicalTestReminderTwo > 0) { notiSet.medicalTestReminderTwo-1 } else { 1 }) }
                    binding.cvDrugs -> { setSelection(if (notiSet.drugsReminderTwo > 0) { notiSet.drugsReminderTwo-1 } else { 1 }) }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderTwo, true, Integer.parseInt(binding.countSelecterTwo.selectedItem.toString()))) {
                            when (cardSelected) {
                                binding.cvMedicalTest -> { notiSet.medicalTestReminderTwo = Integer.parseInt(binding.countSelecterTwo.selectedItem.toString()) }
                                binding.cvDrugs -> { notiSet.drugsReminderTwo = Integer.parseInt(binding.countSelecterTwo.selectedItem.toString()) }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                            setSelection(1)
                        }
                    }
                }
            }
            with(binding.countSelecterThree) {
                adapter = adapterReminder
                when (cardSelected) {
                    binding.cvMedicalTest -> { setSelection(if (notiSet.medicalTestReminderThree > 0) { notiSet.medicalTestReminderThree-1 } else { 1 }) }
                    binding.cvDrugs -> { setSelection(if (notiSet.drugsReminderThree > 0) { notiSet.drugsReminderThree-1 } else { 1 }) }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderThree, true, Integer.parseInt(binding.countSelecterThree.selectedItem.toString()))) {
                            when (cardSelected) {
                                binding.cvMedicalTest -> { notiSet.medicalTestReminderThree = Integer.parseInt(binding.countSelecterThree.selectedItem.toString()) }
                                binding.cvDrugs -> { notiSet.drugsReminderThree = Integer.parseInt(binding.countSelecterThree.selectedItem.toString()) }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                            setSelection(1)
                        }
                    }
                }
            }
            with(binding.countSelecterFour) {
                adapter = adapterReminder
                when (cardSelected) {
                    binding.cvMedicalTest -> { setSelection(if (notiSet.medicalTestReminderFour > 0) { notiSet.medicalTestReminderFour-1 } else { 1 }) }
                    binding.cvDrugs -> { setSelection(if (notiSet.drugsReminderFour > 0) { notiSet.drugsReminderFour-1 } else { 1 }) }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFour, true, Integer.parseInt(binding.countSelecterFour.selectedItem.toString()))) {
                            when (cardSelected) {
                                binding.cvMedicalTest -> { notiSet.medicalTestReminderFour = Integer.parseInt(binding.countSelecterFour.selectedItem.toString()) }
                                binding.cvDrugs -> { notiSet.drugsReminderFour = Integer.parseInt(binding.countSelecterFour.selectedItem.toString()) }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                            setSelection(1)
                        }
                    }
                }
            }
            with(binding.countSelecterFive) {
                adapter = adapterReminder
                when (cardSelected) {
                    binding.cvMedicalTest -> { setSelection(if (notiSet.medicalTestReminderFive > 0) { notiSet.medicalTestReminderFive-1 } else { 1 }) }
                    binding.cvDrugs -> { setSelection(if (notiSet.drugsReminderFive > 0) { notiSet.drugsReminderFive-1 } else { 1 }) }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFive, true, Integer.parseInt(binding.countSelecterFive.selectedItem.toString()))) {
                            when (cardSelected) {
                                binding.cvMedicalTest -> { notiSet.medicalTestReminderFive = Integer.parseInt(binding.countSelecterFive.selectedItem.toString()) }
                                binding.cvDrugs -> { notiSet.drugsReminderFive = Integer.parseInt(binding.countSelecterFive.selectedItem.toString()) }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                            setSelection(1)
                        }
                    }
                }
            }

            binding.reminderOne.visibility = when (cardSelected) {
                binding.cvMedicalTest -> { if (notiSet.medicalTestReminderOne > 0) { View.VISIBLE } else { View.GONE } }
                binding.cvDrugs -> { if (notiSet.drugsReminderOne > 0) { View.VISIBLE } else { View.GONE } }
                else -> { View.GONE }
            }
            binding.reminderTwo.visibility = when (cardSelected) {
                binding.cvMedicalTest -> { if (notiSet.medicalTestReminderTwo > 0) { View.VISIBLE } else { View.GONE } }
                binding.cvDrugs -> { if (notiSet.drugsReminderTwo > 0) { View.VISIBLE } else { View.GONE } }
                else -> { View.GONE }
            }
            binding.reminderThree.visibility = when (cardSelected) {
                binding.cvMedicalTest -> { if (notiSet.medicalTestReminderThree > 0) { View.VISIBLE } else { View.GONE } }
                binding.cvDrugs -> { if (notiSet.drugsReminderThree > 0) { View.VISIBLE } else { View.GONE } }
                else -> { View.GONE }
            }
            binding.reminderFour.visibility = when (cardSelected) {
                binding.cvMedicalTest -> { if (notiSet.medicalTestReminderFour > 0) { View.VISIBLE } else { View.GONE } }
                binding.cvDrugs -> { if (notiSet.drugsReminderFour > 0) { View.VISIBLE } else { View.GONE } }
                else -> { View.GONE }
            }
            binding.reminderFive.visibility = when (cardSelected) {
                binding.cvMedicalTest -> { if (notiSet.medicalTestReminderFive > 0) { View.VISIBLE } else { View.GONE } }
                binding.cvDrugs -> { if (notiSet.drugsReminderFive > 0) { View.VISIBLE } else { View.GONE } }
                else -> { View.GONE }
            }


            binding.btnEraseItemOne.setOnClickListener {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderOne, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestReminderOne = 0 }
                        binding.cvDrugs -> { notiSet.drugsReminderOne = 0 }
                    }
                    binding.reminderOne.visibility = View.GONE
                    binding.btnAddReminder.alpha = 1F
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnEraseItemTwo.setOnClickListener {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderTwo, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestReminderTwo = 0 }
                        binding.cvDrugs -> { notiSet.drugsReminderTwo = 0 }
                    }
                    binding.reminderTwo.visibility = View.GONE
                    binding.btnAddReminder.alpha = 1F
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnEraseItemThree.setOnClickListener {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderThree, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestReminderThree = 0 }
                        binding.cvDrugs -> { notiSet.drugsReminderThree = 0 }
                    }
                    binding.reminderThree.visibility = View.GONE
                    binding.btnAddReminder.alpha = 1F
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnEraseItemFour.setOnClickListener {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFour, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestReminderFour = 0 }
                        binding.cvDrugs -> { notiSet.drugsReminderFour = 0 }
                    }
                    binding.reminderFour.visibility = View.GONE
                    binding.btnAddReminder.alpha = 1F
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnEraseItemFive.setOnClickListener {
                if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFive, false, 0)) {
                    when (cardSelected) {
                        binding.cvMedicalTest -> { notiSet.medicalTestReminderFive = 0 }
                        binding.cvDrugs -> { notiSet.drugsReminderFive = 0 }
                    }
                    binding.reminderFive.visibility = View.GONE
                    binding.btnAddReminder.alpha = 1F
                } else {
                    Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                }
            }


            binding.btnAddReminder.setOnClickListener {
                if (binding.reminderOne.visibility == View.GONE && binding.btnAddReminder.alpha == 1F) {
                    if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderOne, true, 2)) {
                        binding.reminderOne.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.reminderTwo.visibility == View.GONE) {
                    if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderTwo, true, 2)) {
                        binding.reminderTwo.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.reminderThree.visibility == View.GONE) {
                    if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderThree, true, 2)) {
                        binding.reminderThree.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.reminderFour.visibility == View.GONE) {
                    if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFour, true, 2)) {
                        binding.reminderFour.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.reminderFive.visibility == View.GONE) {
                    if (dbPatientsPortal.updatePatientNotificationSettings(notiSet.idNotificationSettings, saReminderFive, true, 2)) {
                        binding.reminderFive.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                    }
                    binding.btnAddReminder.alpha = 0.5F
                }
            }
        }

        binding.clEditView.visibility = View.VISIBLE
        binding.clEditView.startAnimation(animRightIn)
    }
}