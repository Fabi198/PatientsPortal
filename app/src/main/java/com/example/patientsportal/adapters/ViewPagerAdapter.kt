package com.example.patientsportal.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.patientsportal.R
import com.example.patientsportal.fragmentsDrawerMenu.SearchAndListFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val context: Context, private val idContainer: Int, private val title: String, private val tabTitles: Array<String>, private val task: String ?= null) : FragmentStateAdapter(fragmentActivity) {



    override fun createFragment(position: Int): Fragment {
        val fragment = when(title) {
            in arrayOf(context.getString(R.string.medicamentos), context.getString(R.string.see_medical_test_title), context.getString(R.string.requested_medical_tests_title), context.getString(R.string.documentos_clinicos), context.getString(R.string.mis_turnos_agendados_title)) -> { SearchAndListFragment() }
            else -> { SearchAndListFragment() }
        }
        val bundle = Bundle()
        bundle.putInt(context.getString(R.string.containerid_tag), idContainer)
        bundle.putString(context.getString(R.string.title_tag), tabTitles[position])
        if (task != null) {
            bundle.putString(context.getString(R.string.task_tag), task)
            when (task) {
                in arrayOf(context.getString(R.string.patienttestlist_tag), context.getString(R.string.requestedpatienttestlist_tag), context.getString(R.string.clinicdocumentslist_tag), context.getString(R.string.billslist_tag), context.getString(R.string.clinicdocumentslist_tag)) -> {
                    when (tabTitles[position]) {
                        context.getString(R.string.por_fecha) -> { bundle.putBoolean(context.getString(R.string.arewelookingfordate), true) }
                        else -> { bundle.putBoolean(context.getString(R.string.arewelookingfordate), false) }
                    }
                }
                in arrayOf(context.getString(R.string.appointmentsahead_task)) -> {
                    when (tabTitles[position]) {
                        context.getString(R.string.todos_tab_title) -> { bundle.putString(context.getString(R.string.appointmentstabtitle_tag), context.getString(R.string.todos_tab_title)) }
                        context.getString(R.string.presenciales_tab_title) -> { bundle.putString(context.getString(R.string.appointmentstabtitle_tag), context.getString(R.string.presenciales_tab_title)) }
                        context.getString(R.string.teleconsultas_tab_title) -> { bundle.putString(context.getString(R.string.appointmentstabtitle_tag), context.getString(R.string.teleconsultas_tab_title)) }
                    }
                }
                in arrayOf(context.getString(R.string.prescriptionlist)) -> {
                    when (tabTitles[position]) {
                        context.getString(R.string.vigentes) -> { bundle.putString(context.getString(R.string.prescriptiontabtitle_tag), context.getString(R.string.current)) }
                        context.getString(R.string.vencidos) -> { bundle.putString(context.getString(R.string.prescriptiontabtitle_tag), context.getString(R.string.expired)) }
                    }
                }
            }
        }

        fragment.arguments = bundle
        return fragment
    }

    override fun getItemCount(): Int {
        return tabTitles.size
    }
}
