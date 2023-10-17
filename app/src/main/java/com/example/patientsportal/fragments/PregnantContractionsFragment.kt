package com.example.patientsportal.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.patientsportal.R
import com.example.patientsportal.adapters.pregnancyAdapters.PregnancyContractionsAdapter
import com.example.patientsportal.databinding.CustomAlertDialogInfoContractionBinding
import com.example.patientsportal.databinding.FragmentPregnantContractionsBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.PregnancyContraction
import com.example.patientsportal.entities.dbEntities.PregnancyContractions
import com.example.patientsportal.objects.CreatorPDF.createPdf
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PregnantContractionsFragment : Fragment(R.layout.fragment_pregnant_contractions) {

    private lateinit var binding: FragmentPregnantContractionsBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isChronometerRunning = false
    private var isIntervalChronometerRunning = false
    private var elapsedSeconds = 0
    private var counting = false
    private var countContractions = 0
    private var intervalElapsedSeconds = 0
    private var calendar = Calendar.getInstance()
    private var startHour = ""
    private var finishHour = ""
    private var hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private var listContraction = ArrayList<PregnancyContraction>()
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private var listContractions = ArrayList<PregnancyContractions>()
    private lateinit var adapter: PregnancyContractionsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPregnantContractionsBinding.bind(view)
        dbPatientsPortal = DbPatientsPortal(requireContext())
        val animLeftOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.move_left_out)
        animLeftOut.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) { binding.rlCreateContractionView.visibility = View.GONE }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        val animRightIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.move_right_in)
        val animLeftIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.move_left_in)
        val animRightOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.move_right_out)
        animRightOut.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) { binding.rlHistoryView.visibility = View.GONE }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        lifecycleScope.launch(Dispatchers.IO) {
            listContractions = dbPatientsPortal.readAllPregnancyContractions(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
            adapter = PregnancyContractionsAdapter(listContractions, requireContext())
            withContext(Dispatchers.Main) {
                binding.rvContractions.adapter = adapter
            }
        }

        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        binding.btnStartContraction.setOnClickListener {
            if (!counting) {
                startChronometer()
                binding.btnStartContraction.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.orange, null))
                binding.btnStartContraction.text = getString(R.string.termina_la_contracci_n)
            } else {
                stopChronometer()
                binding.btnStartContraction.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blueDark, null))
                binding.btnStartContraction.text = getString(R.string.empieza_la_contracci_n)
            }
            counting = !counting
        }

        binding.btnInfo.setOnClickListener { showInfoAlertDialog() }

        binding.btnShare.setOnClickListener {

            createPdf(requireContext(), listContractions, getString(R.string.pdf_contractions_title))

            // Crear un intent para compartir el PDF
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            val uri = FileProvider.getUriForFile(requireContext(), "com.example.patientsportal.fileprovider", File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getString(R.string.pdf_contractions_title)))
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Compartir historial con:"))

        }

        binding.btnChangeView.setOnClickListener {
            if (binding.tvChangeView.text == getString(R.string.ver_hist_rico)) {
                binding.rlCreateContractionView.startAnimation(animLeftOut)
                binding.rlHistoryView.visibility = View.VISIBLE
                binding.rlHistoryView.startAnimation(animRightIn)
                binding.tvChangeView.text = getString(R.string.volver_atras)
                binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_arrow_back_ios_24, 0, 0, 0)
                binding.btnInfo.visibility = View.GONE
                binding.btnShare.visibility = View.VISIBLE
            } else if (binding.tvChangeView.text == getString(R.string.volver_atras)) {
                binding.rlHistoryView.startAnimation(animRightOut)
                binding.rlCreateContractionView.visibility = View.VISIBLE
                binding.rlCreateContractionView.startAnimation(animLeftIn)
                binding.tvChangeView.text = getString(R.string.ver_hist_rico)
                binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_receipt_24, 0, 0, 0)
                binding.btnShare.visibility = View.GONE
                binding.btnInfo.visibility = View.VISIBLE
            }
        }

        binding.btnErase.setOnClickListener {
            stopIntervalChronometer()
            for (i in binding.tlMain.childCount - 1 downTo 1) {
                val row = binding.tlMain.getChildAt(i)
                binding.tlMain.removeView(row)
            }
            binding.miniCardsConfirm.visibility = View.GONE
        }
        binding.btnSave.setOnClickListener {
            stopIntervalChronometer()
            lifecycleScope.launch(Dispatchers.IO) {
                val idPregnancyContractions = dbPatientsPortal.createPregnancyContractions(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), dateFormat.format(calendar.time)).toInt()
                listContraction.forEach {
                    dbPatientsPortal.createPregnancyContraction(idPregnancyContractions, it.duration, it.interval, it.startAndFinish)
                }
                withContext(Dispatchers.Main) {
                    for (i in binding.tlMain.childCount - 1 downTo 1) {
                        val row = binding.tlMain.getChildAt(i)
                        binding.tlMain.removeView(row)
                    }
                    showSuccessfullySavedContractions(true)
                    listContractions = dbPatientsPortal.readAllPregnancyContractions(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
                    adapter.notifyItemInserted(listContractions.size-1)
                }
            }
            binding.miniCardsConfirm.visibility = View.GONE
        }
    }

    private fun showInfoAlertDialog() {
        val binding = CustomAlertDialogInfoContractionBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private val chronometerRunnable = object : Runnable {
        override fun run() {
            if (isChronometerRunning) {
                // Incrementa el tiempo transcurrido en 1 segundo
                elapsedSeconds++

                // Calcula horas, minutos y segundos
                val hours = elapsedSeconds / 3600
                val minutes = (elapsedSeconds % 3600) / 60
                val seconds = elapsedSeconds % 60

                // Actualiza la UI con el tiempo transcurrido en el formato deseado
                val timeElapsed = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                binding.tvChronometer.text = timeElapsed

                // Programa la próxima ejecución de esta función después de 1 segundo
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val intervalChronometerRunnable = object : Runnable {
        override fun run() {
            if (isIntervalChronometerRunning) {
                // Incrementa el tiempo transcurrido en 1 segundo
                intervalElapsedSeconds++

                // Programa la próxima ejecución de esta función después de 1 segundo
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startChronometer() {
        if (!isChronometerRunning) {
            isChronometerRunning = true
            startHour = hourFormat.format(calendar.time)
            handler.post(chronometerRunnable)
        }
    }

    private fun stopChronometer() {
        isChronometerRunning = false
        startIntervalContractions()
        finishHour = hourFormat.format(calendar.time)
        elapsedSeconds = 0
        addNewRow(binding.tvChronometer.text.toString(), if (countContractions == 0) { "00:00:00" } else { String.format("%02d:%02d:%02d", (intervalElapsedSeconds / 3600), ((intervalElapsedSeconds % 3600) / 60), (intervalElapsedSeconds % 60)) }, "$startHour-$finishHour")
        startHour = ""
        finishHour = ""
        countContractions++
        binding.tvChronometer.text = getString(R.string._00_00_00)
        handler.removeCallbacks(chronometerRunnable)
        binding.miniCardsConfirm.visibility = View.VISIBLE
    }

    private fun addNewRow(duration: String, interval: String, startAndFinish: String) {
        // Crea una nueva TableRow
        val tableRow = TableRow(context)

        // Columna 1 - Duración
        val tvDuration = TextView(context)
        with(tvDuration) {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
            text = duration
            setTextColor(resources.getColor(R.color.black, null))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTypeface(null, Typeface.BOLD)
            setPadding(8, 8, 8, 8)
            tableRow.addView(tvDuration)
        }

        // Columna 2 - Intervalo
        val tvInterval = TextView(context)
        with(tvInterval) {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
            text = interval
            setTextColor(resources.getColor(R.color.black, null))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTypeface(null, Typeface.BOLD)
            setPadding(8, 8, 8, 8)
            tableRow.addView(tvInterval)
        }

        // Columna 3 - Inicio y Fin
        val tvStartAndFinish = TextView(context)
        with(tvStartAndFinish) {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 30f)
            text = startAndFinish
            setTextColor(resources.getColor(R.color.black, null))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTypeface(null, Typeface.BOLD)
            setPadding(8, 8, 8, 8)
            tableRow.addView(tvStartAndFinish)
        }

        // Agrega la TableRow al TableLayout
        binding.tlMain.addView(tableRow)

        listContraction.add(PregnancyContraction(duration = duration, interval = interval, startAndFinish = startAndFinish))
    }

    private fun stopIntervalChronometer() {
        isIntervalChronometerRunning = false
        intervalElapsedSeconds = 0
        countContractions = 0
        handler.removeCallbacks(intervalChronometerRunnable)
    }

    private fun startIntervalContractions() {
        if (!isIntervalChronometerRunning) {
            isIntervalChronometerRunning = true
            handler.post(intervalChronometerRunnable)
        }
    }

    private fun showSuccessfullySavedContractions(b: Boolean) {
        if (b) { binding.tv2.text = getString(R.string.guardamos_tus_contracci_nes) }
        binding.cvSuccessfullyPhoto.visibility = View.VISIBLE
        val gif = R.raw.animated_check
        Glide.with(this)
            .asGif()
            .load(gif)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                    resource?.setLoopCount(1)

                    resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            val animFadeOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                            animFadeOut.setAnimationListener(object : AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) { binding.cvSuccessfullyPhoto.visibility = View.GONE }
                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                            binding.cvSuccessfullyPhoto.startAnimation(animFadeOut)
                        }
                    })
                    return false
                }
            })
            .into(binding.animatedCheck)
    }

}