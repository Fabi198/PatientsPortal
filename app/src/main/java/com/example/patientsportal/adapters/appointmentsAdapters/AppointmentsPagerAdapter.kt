package com.example.patientsportal.adapters.appointmentsAdapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.patientsportal.fragments.PageAppointmentFragment

class AppointmentsPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = ArrayList<PageAppointmentFragment>()

    @SuppressLint("NotifyDataSetChanged")
    fun addPage(paf: PageAppointmentFragment) {
        fragmentList.add(paf)
        notifyDataSetChanged()
    }

    fun getFragment(position: Int): PageAppointmentFragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
