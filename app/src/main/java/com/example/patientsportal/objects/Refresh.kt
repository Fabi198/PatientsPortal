package com.example.patientsportal.objects

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.patientsportal.MainActivity

object Refresh {

    fun refresh(activity: FragmentActivity, context: Context) {
        activity.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

}