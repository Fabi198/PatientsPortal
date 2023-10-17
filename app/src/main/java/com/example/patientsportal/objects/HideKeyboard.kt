package com.example.patientsportal.objects

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import android.view.inputmethod.InputMethodManager

object HideKeyboard {

    fun hideKeyboardOnFragment(fragmentActivity: FragmentActivity, view: View) {
        val imm = fragmentActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboardOnFragment(fragmentActivity: FragmentActivity, view: View) {
        val imm = fragmentActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }


}