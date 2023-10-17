package com.example.patientsportal.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.patientsportal.adapters.pregnancyAdapters.PregnancyFeelAdapter
import com.example.patientsportal.adapters.pregnancyAdapters.PregnancyNotesAdapter
import com.example.patientsportal.adapters.pregnancyAdapters.PregnancyPhotosAdapter
import com.example.patientsportal.databinding.CustomAlertDialogPregnancyWeekBinding
import com.example.patientsportal.databinding.FragmentPregnancyNotesAndPhotosBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.PregnancyFeel
import com.example.patientsportal.entities.dbEntities.PregnancyNote
import com.example.patientsportal.entities.dbEntities.PregnancyPhoto
import com.example.patientsportal.objects.FileUtils
import com.example.patientsportal.objects.GetPatient.getPatient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class PregnantNotesAndPhotosFragment : Fragment(R.layout.fragment_pregnancy_notes_and_photos) {

    private lateinit var binding: FragmentPregnancyNotesAndPhotosBinding
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private var title: String ?= null
    private val ddMMyyyyFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var date: String = ""
    private var feel: String = ""
    private val requestImageCapture = 1
    private var currentPhotoPath: String = ""
    private val cameraPermissionRequestCode = 101
    private val readExternalStoragePermissionRequestCode = 202
    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData = result.data
            if (intentData != null) {
                val fileUri = intentData.data
                // Aquí puedes manejar el archivo seleccionado (fileUri)
                if (fileUri != null) {
                    if (isJpgFile(fileUri)) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // El archivo seleccionado es un archivo JPG, puedes continuar con el procesamiento
                            val filePath = FileUtils.getPath(requireContext(), fileUri)
                            if (filePath != null) {
                                setChoosedPhoto(fileUri, filePath)
                                showSuccessfullyPhoto(false)
                                Log.i("portetPath", filePath)
                            }
                        } else {
                            // La aplicación no tiene permisos de cámara, solicita los permisos.
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), readExternalStoragePermissionRequestCode)
                        }
                    } else {
                        // El archivo seleccionado no es un archivo JPG, muestra un mensaje de error o realiza la acción adecuada.
                        Log.i("PhotoExisting", "Aparentemente no es un jpg")
                    }
                } else {
                    Log.i("PhotoExisting", "Debe ser que es nulo")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPregnancyNotesAndPhotosBinding.bind(view)
        title = arguments?.getString(getString(R.string.title_tag))
        dbPatientsPortal = DbPatientsPortal(requireContext())
        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        binding.tvDate.text = getDate()
        binding.tvDate.setOnClickListener { openCalendar() }

        binding.etSubTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    setBtnSaveEnabled()
                } else {
                    setBtnSaveEnabled()
                }
            }

        })

        binding.etNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    setBtnSaveEnabled()
                } else {
                    setBtnSaveEnabled()
                }
            }

        })


        if (title != null) {
            when (title) {
                getString(R.string.feels_tag) -> {
                    binding.tvSubTitle.visibility = View.GONE
                    binding.etSubTitle.visibility = View.GONE
                    binding.tvFeelTitle.visibility = View.VISIBLE
                    binding.clFeel.visibility = View.VISIBLE
                    val iconColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                    binding.btnSad.imageTintList = iconColor
                    binding.btnSad.setOnClickListener { saveFeel(getString(R.string.mal)) }
                    binding.btnSadTV.setOnClickListener { saveFeel(getString(R.string.mal)) }
                    binding.btnNeutral.imageTintList = iconColor
                    binding.btnNeutral.setOnClickListener { saveFeel(getString(R.string.bien)) }
                    binding.btnNeutralTV.setOnClickListener { saveFeel(getString(R.string.bien)) }
                    binding.btnHappy.imageTintList = iconColor
                    binding.btnHappy.setOnClickListener { saveFeel(getString(R.string.muy_bien)) }
                    binding.btnHappyTV.setOnClickListener { saveFeel(getString(R.string.muy_bien)) }
                    val listPregnancyFeel = dbPatientsPortal.readAllPregnancyFeelsFromPatient(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
                    if (listPregnancyFeel.size > 0) {
                        val adapter = PregnancyFeelAdapter(listPregnancyFeel) { pf, _ ->
                            showCustomAlertDialog(pf = pf)
                        }
                        binding.rvPregnancyNotesOrPhotos.adapter = adapter
                        binding.rvPregnancyNotesOrPhotos.visibility = View.VISIBLE
                    }
                }
                getString(R.string.appointmentsnotes_tag) -> {
                    binding.tvNote.visibility = View.VISIBLE
                    binding.etNote.visibility = View.VISIBLE
                    binding.tvMainTitle.text = getString(R.string.mis_notas)
                    binding.tvTitle.text = getString(R.string.nota_de_turno)
                    binding.tvSave.text = getString(R.string.guardar_nota)
                    val listPregnancyAppointmentNotes = dbPatientsPortal.readAllPregnancyNotesFromPatient(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
                    if (listPregnancyAppointmentNotes.size > 0) {
                        val adapter = PregnancyNotesAdapter(listPregnancyAppointmentNotes) { pn, _ ->
                            showCustomAlertDialog(pn = pn)
                        }
                        binding.rvPregnancyNotesOrPhotos.adapter = adapter
                        binding.rvPregnancyNotesOrPhotos.visibility = View.VISIBLE
                    }
                }
                getString(R.string.photos_tag) -> {
                    binding.btnPhoto.visibility = View.VISIBLE
                    binding.tvMainTitle.text = getString(R.string.mis_fotos)
                    binding.tvTitle.text = getString(R.string.fotos)
                    binding.tvSave.text = getString(R.string.guardar_foto)
                    val iconColor = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
                    binding.fabPhoto.imageTintList = iconColor
                    binding.fabPhoto.setOnClickListener { openCameraDialog() }
                    val listPregnancyPhotos = dbPatientsPortal.readAllPregnancyPhotosFromPatient(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
                    if (listPregnancyPhotos.size > 0) {
                        val adapter = PregnancyPhotosAdapter(listPregnancyPhotos, requireContext()) { pp, _ ->
                            showCustomAlertDialog(pp = pp)
                        }
                        binding.rvPregnancyNotesOrPhotos.adapter = adapter
                        binding.rvPregnancyNotesOrPhotos.visibility = View.VISIBLE
                    }
                }
                getString(R.string.notes_tag) -> {
                    binding.tvNote.visibility = View.VISIBLE
                    binding.etNote.visibility = View.VISIBLE
                    binding.tvMainTitle.text = getString(R.string.mis_notas)
                    binding.tvTitle.text = getString(R.string.notas)
                    binding.tvSave.text = getString(R.string.guardar_nota)
                    val listPregnancyNotes = dbPatientsPortal.readAllPregnancyNotesFromPatient(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient))
                    if (listPregnancyNotes.size > 0) {
                        val adapter = PregnancyNotesAdapter(listPregnancyNotes) { pn, _ ->
                            showCustomAlertDialog(pn = pn)
                        }
                        binding.rvPregnancyNotesOrPhotos.adapter = adapter
                        binding.rvPregnancyNotesOrPhotos.visibility = View.VISIBLE
                    }
                }
            }

            binding.btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (checkDataCompleted()) {
                        when (title) {
                            getString(R.string.feels_tag) -> {
                                if (dbPatientsPortal.createPregnancyFeels(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), feel, date) > 0) {
                                    withContext(Dispatchers.Main) {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            getString(R.string.appointmentsnotes_tag) -> {
                                if (dbPatientsPortal.createPregnancyNotes(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), binding.etSubTitle.text.toString(), binding.etNote.text.toString(), date) > 0) {
                                    withContext(Dispatchers.Main) {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            getString(R.string.photos_tag) -> {
                                if (dbPatientsPortal.createPregnancyPhotos(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), binding.etSubTitle.text.toString(), currentPhotoPath, date) > 0) {
                                    withContext(Dispatchers.Main) {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            getString(R.string.notes_tag) -> {
                                if (dbPatientsPortal.createPregnancyNotes(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient), binding.etSubTitle.text.toString(), binding.etNote.text.toString(), date) > 0) {
                                    withContext(Dispatchers.Main) {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), getString(R.string.hubo_un_error_en_la_base_de_datos), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), getString(R.string.campos_incompletos), Toast.LENGTH_SHORT).show()
                        }
                    }


                }
            }
        }


    }

    private fun saveFeel(feel: String) {
        showSuccessfullyPhoto(true)
        this.feel = feel
        setBtnSaveEnabled()
    }

    private fun checkDataCompleted(): Boolean {
        return when (title) {
            getString(R.string.feels_tag) -> {
                date.isNotEmpty() && feel.isNotEmpty()
            }
            getString(R.string.appointmentsnotes_tag) -> {
                date.isNotEmpty() && binding.etSubTitle.text.isNotEmpty() && binding.etNote.text.isNotEmpty()
            }
            getString(R.string.photos_tag) -> {
                date.isNotEmpty() && binding.etSubTitle.text.isNotEmpty() && currentPhotoPath.isNotEmpty() && binding.ivCheck.visibility == View.VISIBLE
            }
            getString(R.string.notes_tag) -> {
                date.isNotEmpty() && binding.etSubTitle.text.isNotEmpty() && binding.etNote.text.isNotEmpty()
            }
            else -> { false }
        }
    }

    private fun setBtnSaveEnabled() {
        binding.btnSave.alpha = if (checkDataCompleted()) {
            1F
        } else {
            0.5F
        }
    }

    private fun openCameraDialog() {
        val animScrollUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scroll_to_up_custom_alert_dialog)
        val animScrollDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scroll_to_down_custom_alert_dialog)

        animScrollUp.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) { binding.modalBackground.visibility = View.VISIBLE }
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        animScrollDown.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) { binding.modalBackground.visibility = View.GONE; binding.cvPhoto.visibility = View.GONE }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.cvPhoto.visibility = View.VISIBLE
        binding.cvPhoto.startAnimation(animScrollUp)

        binding.modalBackground.setOnClickListener { binding.cvPhoto.startAnimation(animScrollDown) }
        binding.btnTakePhoto.setOnClickListener { // Crear un intent para abrir la cámara
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // La aplicación tiene permisos de cámara, puedes abrir la cámara aquí.
                openCamera()
            } else {
                // La aplicación no tiene permisos de cámara, solicita los permisos.
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
            }
            binding.cvPhoto.startAnimation(animScrollDown)
        }
        binding.btnChooseExisting.setOnClickListener {
            openFilePicker()
            binding.cvPhoto.startAnimation(animScrollDown)
        }
        binding.btnCancel.setOnClickListener { binding.cvPhoto.startAnimation(animScrollDown) }
    }

    private fun showSuccessfullyPhoto(b: Boolean) {
        if (b) { binding.tv2.text = getString(R.string.guardamos_tu_estado_de_nimo) }
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

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  // Para todos los tipos de archivos
        pickFile.launch(intent)
    }

    private fun setChoosedPhoto(fileUri: Uri, imagePath: String) {
        // Cargar y mostrar la imagen en el ImageView usando Glide
        Glide.with(this)
            .load(fileUri)
            .into(binding.ivPhotoThumbnail)
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvDatePhotoThumbnail.text = outputFormat.format(Calendar.getInstance().time)
        binding.clPhotoThumbnail.visibility = View.VISIBLE
        binding.cvErase.setOnClickListener {
            currentPhotoPath = ""
            binding.clPhotoThumbnail.visibility = View.GONE
            setBtnSaveEnabled()
        }
        binding.cvConfirm.setOnClickListener {
            binding.cvConfirm.visibility = View.GONE
            binding.cvErase.visibility = View.GONE
            binding.pbSuccessfullyPhotoThumbnail.visibility = View.VISIBLE
            Handler().postDelayed({
                binding.pbSuccessfullyPhotoThumbnail.visibility = View.GONE
                binding.ivCheck.visibility = View.VISIBLE
                currentPhotoPath = imagePath
                setBtnSaveEnabled()
            }, 700)
        }
    }


    private fun isJpgFile(uri: Uri): Boolean {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri)
        return mimeType != null && mimeType.startsWith("image/jpeg")
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Comprobar si hay una aplicación de cámara disponible para manejar la solicitud
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error al crear el archivo
                null
            }

            if (photoFile != null && photoFile.exists()) {
                currentPhotoPath = photoFile.absolutePath
                // El archivo se creó con éxito y existe, puedes continuar con la captura de la imagen.
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.patientsportal.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, requestImageCapture)
            } else {
                Log.e("PhotoFile", "Failed to create the image file")
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara otorgado, puedes abrir la cámara aquí.
                openCamera()
            } else {
                // Permiso de cámara denegado, puedes mostrar un mensaje al usuario o tomar otras medidas.
            }
        }
    }

    // Método para crear un archivo para la imagen
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    // Código para manejar el resultado de la captura de la imagen
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestImageCapture && resultCode == Activity.RESULT_OK) {
            showSuccessfullyPhoto(false)
            val imageFile = File(currentPhotoPath)
            if (imageFile.exists()) {
                // El archivo de imagen existe, intenta decodificarlo y mostrarlo en el ImageView.
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap != null) {
                    binding.ivPhotoThumbnail.setImageBitmap(bitmap)
                    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    binding.tvDatePhotoThumbnail.text = outputFormat.format(Calendar.getInstance().time)
                    binding.clPhotoThumbnail.visibility = View.VISIBLE
                    binding.cvErase.setOnClickListener {
                        currentPhotoPath = ""
                        binding.clPhotoThumbnail.visibility = View.GONE
                        setBtnSaveEnabled()
                    }
                    binding.cvConfirm.setOnClickListener {
                        binding.cvConfirm.visibility = View.GONE
                        binding.cvErase.visibility = View.GONE
                        binding.pbSuccessfullyPhotoThumbnail.visibility = View.VISIBLE
                        Handler().postDelayed({
                            binding.pbSuccessfullyPhotoThumbnail.visibility = View.GONE
                            binding.ivCheck.visibility = View.VISIBLE
                            setBtnSaveEnabled()
                        }, 700)
                    }
                } else {
                    Log.e("PhotoDecode", "Failed to decode the captured image")
                }
            } else {
                Log.e("PhotoFile", "Image file does not exist at path: ${imageFile.absolutePath}")
            }

        }
    }

    private fun getDate(): String {
        val cal: Calendar = Calendar.getInstance()
        val outputFormat2 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        date = ddMMyyyyFormat.format(cal.time)
        return outputFormat2.format(cal.time)
    }

    private fun openCalendar() {
        val cal: Calendar = Calendar.getInstance()
        val yearGetter = cal.get(Calendar.YEAR)
        val monthGetter = cal.get(Calendar.MONTH)
        val dayGetter = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), 0,
            { _, year, month, dayOfMonth ->
                lateinit var fecha: String
                if ((month+1) in 0..9 && dayOfMonth in 10..31) {
                    fecha = "$year/0${month+1}/$dayOfMonth"
                } else if ((month+1) in 0..9 && dayOfMonth in 0..9) {
                    fecha = "$year/0${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 0..9) {
                    fecha = "$year/${month+1}/0$dayOfMonth"
                } else if ((month+1) in 10..12 && dayOfMonth in 10..31) {
                    fecha = "$year/${month+1}/$dayOfMonth"
                }
                val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val outputFormat2 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                val dateFormatted = inputFormat.parse(fecha)
                binding.tvDate.text = outputFormat2.format(dateFormatted!!)
                date = ddMMyyyyFormat.format(inputFormat.parse(fecha)!!)
            }, yearGetter, monthGetter, dayGetter)
        dpd.datePicker.maxDate = cal.timeInMillis
        val minDate = cal.clone() as Calendar
        minDate.time = ddMMyyyyFormat.parse(dbPatientsPortal.readPatientPregnancy(dbPatientsPortal.readPregnancyFromPatient(getPatient(requireActivity(), requireContext()).idPatient)).startPregnancyDate)!!
        dpd.datePicker.minDate = minDate.timeInMillis
        dpd.show()

    }

    @SuppressLint("SetTextI18n")
    private fun showCustomAlertDialog(pf: PregnancyFeel ?= null, pn: PregnancyNote ?= null, pp: PregnancyPhoto ?= null) {
        val outputFormat2 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val binding = CustomAlertDialogPregnancyWeekBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
            .setView(binding.root)
            .create()
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        if (pf != null) {
            val ivFeelImageResource = when (pf.feel) {
                getString(R.string.mal) -> { R.drawable.baseline_sentiment_very_dissatisfied_24 }
                getString(R.string.bien) -> { R.drawable.baseline_sentiment_neutral_24 }
                getString(R.string.muy_bien) -> { R.drawable.baseline_sentiment_satisfied_alt_24 }
                else -> 0
            }
            binding.ivFeel.setImageResource(ivFeelImageResource)
            binding.tvDate.text = outputFormat2.format(ddMMyyyyFormat.parse(pf.date)!!)
            binding.tvFeel.text = pf.feel
            binding.llFeel.visibility = View.VISIBLE
        }
        if (pn != null) {
            binding.tvDate.text = outputFormat2.format(ddMMyyyyFormat.parse(pn.date)!!)
            binding.tvNoteTitle.text = pn.title
            binding.tvNote.text = pn.note
            binding.llNote.visibility = View.VISIBLE
        }
        if (pp != null) {
            binding.tvDate.text = outputFormat2.format(ddMMyyyyFormat.parse(pp.date)!!)
            binding.tvPhoto.text = pp.title
            Glide.with(this)
                .load(pp.imagePath)
                .into(binding.ivPhoto)
            binding.llPhoto.visibility = View.VISIBLE
        }
        binding.clSecondMain.visibility = View.VISIBLE
        alertDialog.show()
    }

}