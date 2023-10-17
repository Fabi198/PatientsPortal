package com.example.patientsportal.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.patientsportal.MainActivity
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ActivitySplashScreenBinding

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            if (!checkSession()) {
                startActivity(Intent(this, AuthActivity()::class.java))
                finish()
            } else {
                startActivity(Intent(this, MainActivity()::class.java))
                finish()
            }
        }, 100)



    }

    private fun checkSession(): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.my_pref_tag), MODE_PRIVATE)
        val dni = prefs.getString(getString(R.string.dni_tag), null)
        val password = prefs.getString(getString(R.string.password_tag), null)
        return dni != null && password != null
    }
}