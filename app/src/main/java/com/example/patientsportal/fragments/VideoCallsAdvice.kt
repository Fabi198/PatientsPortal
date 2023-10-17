package com.example.patientsportal.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentVideoCallsAdviceBinding


class VideoCallsAdvice : Fragment(R.layout.fragment_video_calls_advice) {

    private lateinit var binding: FragmentVideoCallsAdviceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVideoCallsAdviceBinding.bind(view)

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        binding.btnNextStep.setOnClickListener { setNextStep() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setNextStep() {
        binding.llTexts.visibility = View.GONE
        binding.cvContainer.visibility = View.GONE
        binding.llAnnounceStep.visibility = View.VISIBLE
        binding.webView.settings.javaScriptEnabled = true

        // Configurar WebViewClient para manejar las redirecciones dentro del WebView
        binding.webView.webViewClient = WebViewClient()

        // Configurar WebChromeClient para la reproducción de videos en pantalla completa
        binding.webView.webChromeClient = WebChromeClient()

        // Cargar el video de YouTube en el WebView
        val videoId = getString(R.string.idYoutubeVideoCallExplained)
        val videoUrl = "https://www.youtube.com/embed/$videoId?autoplay=1&vq=small"
        binding.webView.loadUrl(videoUrl)
    }

}