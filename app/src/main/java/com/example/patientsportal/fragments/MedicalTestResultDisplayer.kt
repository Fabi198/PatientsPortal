package com.example.patientsportal.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentMedicalTestResultDisplayerBinding


class MedicalTestResultDisplayer : Fragment(R.layout.fragment_medical_test_result_displayer) {

    private lateinit var binding: FragmentMedicalTestResultDisplayerBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMedicalTestResultDisplayerBinding.bind(view)

        setWebView(arguments?.getString(getString(R.string.link_tag)))
        binding.btnShare.setOnClickListener { shareLink(arguments?.getString(getString(R.string.link_tag))) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(link: String?) {
        binding.webView.settings.javaScriptEnabled = true // Habilita JavaScript si es necesario

        // Configura un WebViewClient para gestionar las interacciones de página web dentro del WebView
        binding.webView.webViewClient = WebViewClient()

        binding.webView.settings.apply {
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Abre el enlace en el navegador predeterminado del usuario
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }
        }

        // Carga la página web en el WebView
        if (link != null) { binding.webView.loadUrl(link) }
    }

    private fun shareLink(link: String?) {
        // Crea un Intent para enviar
        val intent = Intent(Intent.ACTION_SEND)
        // Establece el tipo de contenido
        intent.type = "text/plain"
        // Agrega el enlace al Intent
        intent.putExtra(Intent.EXTRA_TEXT, link)
        // Inicia la actividad para compartir
        startActivity(Intent.createChooser(intent, getString(R.string.compartir_enlace)))
    }
    
}