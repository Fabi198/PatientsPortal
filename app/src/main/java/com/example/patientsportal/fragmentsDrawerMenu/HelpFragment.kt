package com.example.patientsportal.fragmentsDrawerMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentHelpBinding


class HelpFragment : Fragment(R.layout.fragment_help) {

    private lateinit var binding: FragmentHelpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHelpBinding.bind(view)

        val url = "https://www.hospitalitaliano.org.ar/JiraApi/verificoToken.php?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c3VhcmlvIjoiRE5JLTE2Mzc0NjAwIiwicGFzc3dvcmQiOiJiSEJsWlc1cGIyTjJaRzlvIiwib3JpZ2VuIjoiUFAiLCJleHAiOiIxNlwvMTBcLzIwMjMgMjM6NDg6MTkifQ.LPqZcPOyFcLy4bvwHolpdOlhb0ODAgBCXaE4p592kM4"

        setWebView(url)
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

        // Carga la página web en el WebView
        if (link != null) { binding.webView.loadUrl(link) }
    }

}