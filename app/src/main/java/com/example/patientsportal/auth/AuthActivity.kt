package com.example.patientsportal.auth


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.patientsportal.MainActivity
import com.example.patientsportal.R
import com.example.patientsportal.databinding.ActivityAuthBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.google.android.material.textfield.TextInputLayout


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var numberLengthError = false
    private var passwordLengthError = false
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSpinnerDocument()
        setIDNumber()
        setPassword()
        setForgotPassword()
        binding.btnLogin.setOnClickListener { login() }
    }



    private fun setSpinnerDocument() {
        val adapterDoc = ArrayAdapter(this, android.R.layout.simple_spinner_item, setDocuments())
        adapterDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinnerDocument) {
            adapter = adapterDoc
            setSelection(3)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(this@AuthActivity, "NothingSelected", Toast.LENGTH_SHORT).show() }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { binding.spinnerDocument.selectedItem.toString() }
            }
        }
    }

    private fun setForgotPassword() {
        val text = getString(R.string.olvid_mi_contrase_a)
        val spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
        binding.btnForgotPassword.text = spannableString

        binding.btnForgotPassword.setOnClickListener {

        }
    }


    private fun setPassword() {
        binding.tvEditPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.tvEditPassword.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (binding.tvEditPassword.text?.isNotEmpty() == true && !passwordLengthError) {
                    login()
                }
                return@OnEditorActionListener true
            }
            false
        })
        binding.tvInputPassword.setEndIconOnClickListener {
            if (!passwordVisible) {
                binding.tvEditPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvInputPassword.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.baseline_visibility_24, null)
            } else {
                binding.tvEditPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.tvInputPassword.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.baseline_visibility_off_24, null)
            }
            passwordVisible = !passwordVisible
        }

        binding.tvEditIdNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty()) {
                        if (text.length == 1) {
                            showPhraseError(binding.tvInputIdNumber, getString(R.string.el_numero_es_demasiado_corto))
                        } else if (text.length > 8) {
                            showPhraseError(binding.tvInputIdNumber, getString(R.string.el_numero_es_demasiado_largo))
                        } else {
                            hidePhraseError(binding.tvInputIdNumber)
                        }
                    } else {
                        hidePhraseError(binding.tvInputIdNumber)
                    }
                }
            }
        })
    }

    private fun login() {
        val dbPatientsPortal = DbPatientsPortal(this)
        val patient = dbPatientsPortal.readPatientByDocumentNumber(binding.tvEditIdNumber.text.toString())
        if (patient.documentType == binding.spinnerDocument.selectedItem.toString() && patient.documentNumber == binding.tvEditIdNumber.text.toString()) {
            if (patient.password == binding.tvEditPassword.text.toString()) {
                saveData(binding.tvEditIdNumber.text.toString(), binding.tvEditPassword.text.toString())
                startActivity(Intent(this, MainActivity()::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.contrase_a_incorrecta), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.usuario_no_registrado), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData(dni: String, password: String) {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.my_pref_tag), MODE_PRIVATE)
        val prefsEd = prefs.edit()
        prefsEd.putString(getString(R.string.dni_tag), dni)
        prefsEd.putString(getString(R.string.password_tag), password)
        prefsEd.apply()
    }

    private fun setIDNumber() {
        binding.tvEditIdNumber.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (binding.tvEditIdNumber.text?.isNotEmpty() == true && !numberLengthError) {
                    binding.tvInputIdNumber.focusable = View.NOT_FOCUSABLE
                    binding.tvInputPassword.focusable = View.FOCUSABLE
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.tvEditIdNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (text.isNotEmpty()) {
                        if (text.length == 1) {
                            showPhraseError(binding.tvInputIdNumber, getString(R.string.el_numero_es_demasiado_corto))
                        } else if (text.length > 8) {
                            showPhraseError(binding.tvInputIdNumber, getString(R.string.el_numero_es_demasiado_largo))
                        } else {
                            hidePhraseError(binding.tvInputIdNumber)
                        }
                    } else {
                        hidePhraseError(binding.tvInputIdNumber)
                    }
                }
            }
        })
    }





    private fun showPhraseError(tvInput: TextInputLayout, errorMsg: String) {
        tvInput.error = errorMsg
        numberLengthError = true
    }

    private fun hidePhraseError(tvInput: TextInputLayout) {
        tvInput.error = null
        numberLengthError = false
    }

    private fun setDocuments(): Array<String> { return arrayOf("CI", "CIE", "CM", "DNI", "DNM", "LC", "LE", "PAS") }
}