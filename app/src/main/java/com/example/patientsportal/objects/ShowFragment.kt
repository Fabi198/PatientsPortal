package com.example.patientsportal.objects

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.patientsportal.R
import com.example.patientsportal.entities.dbEntities.AlterOrDrugShopItemParce

object ShowFragment {

    fun showFragmentFromFragment(
        fragmentActivity: FragmentActivity,
        fragment: Fragment,
        tag: String,
        context: Context,
        id: Int ?= null,
        title: String ?= null,
        tabTitles: Array<String> ?= null,
        task: String ?= null,
        link: String ?= null,
        idPCPSelected: Int ?= null,
        doctorOrSpeciality: Boolean ?= null,
        idDoctorSpecialitySelected: IntArray ?= null,
        idPlaceSelected: IntArray ?= null,
        appointmentCreatedOrDeleted: Boolean ?= null,
        listAlterOrDrugShopItemParce: ArrayList<AlterOrDrugShopItemParce> ?= null
    ) {
        if (id != null) {
            val bundle = Bundle()
            bundle.putInt(context.getString(R.string.containerid_tag), id)
            if (title != null) { bundle.putString(context.getString(R.string.title_tag), title) }
            if (tabTitles != null) { bundle.putStringArray(context.getString(R.string.tabtitles_tag), tabTitles) }
            if (task != null) { bundle.putString(context.getString(R.string.task_tag), task) }
            if (link != null) { bundle.putString(context.getString(R.string.link_tag), link) }
            if (idPCPSelected != null) { bundle.putInt(context.getString(R.string.idpcpselected_tag), idPCPSelected) }
            if (doctorOrSpeciality != null) { bundle.putBoolean(context.getString(R.string.doctororspeciality_tag), doctorOrSpeciality) }
            if (idDoctorSpecialitySelected != null) { bundle.putIntArray(context.getString(R.string.iddoctorspecialityselected_tag), idDoctorSpecialitySelected) }
            if (idPlaceSelected != null) { bundle.putIntArray(context.getString(R.string.idplaceselected_tag), idPlaceSelected) }
            if (appointmentCreatedOrDeleted != null) { bundle.putBoolean(context.getString(R.string.appointmentcreatedordeleted), appointmentCreatedOrDeleted) }
            if (listAlterOrDrugShopItemParce != null) { bundle.putParcelableArrayList(context.getString(R.string.alterordrug_tag), listAlterOrDrugShopItemParce) }
            fragment.arguments = bundle
            fragmentActivity
                .supportFragmentManager
                .beginTransaction()
                .replace(id, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }


}