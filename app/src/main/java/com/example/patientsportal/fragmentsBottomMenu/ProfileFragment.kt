package com.example.patientsportal.fragmentsBottomMenu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.patientsportal.R
import com.example.patientsportal.databinding.FragmentProfileBinding
import com.example.patientsportal.databinding.ProfileCardViewsAddressBinding
import com.example.patientsportal.databinding.ProfileCardViewsBillAddressBinding
import com.example.patientsportal.db.arrays.ArrayCountries.arrayCountries
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.entities.dbEntities.Address
import com.example.patientsportal.entities.dbEntities.ContactPerson
import com.example.patientsportal.entities.dbEntities.Patient
import com.example.patientsportal.entities.dbEntities.PhonesPatient
import com.example.patientsportal.objects.DateConverter.dateSimplePatientTestConverter
import com.example.patientsportal.objects.FileUtils
import com.example.patientsportal.objects.GetPatient.getPatient
import com.example.patientsportal.objects.HideKeyboard.hideKeyboardOnFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private var isExpanded = false
    private lateinit var patient: Patient
    private lateinit var phones: PhonesPatient
    private lateinit var address: Address
    private lateinit var billAddress: Address
    private lateinit var contactPerson: ContactPerson
    private lateinit var dbPatientsPortal: DbPatientsPortal
    private val requestImageCapture = 1
    private var currentPhotoPath: String = ""
    private val cameraPermissionRequestCode = 101
    private val readExternalStoragePermissionRequestCode = 202
    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
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
        binding = FragmentProfileBinding.bind(view)
        val scaleUpAnimation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDownAnimation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        val scrollToUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scroll_to_up)
        val scrollToDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scroll_to_down)
        val fadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        fadeIn.duration = 500
        val fadeOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        fadeOut.duration = 500

        lifecycleScope.launch(Dispatchers.IO) {
            patient = getPatient(requireActivity(), requireContext())
            dbPatientsPortal = DbPatientsPortal(requireContext())
            phones = dbPatientsPortal.readPhonesFromPatient(patient.idPatient)
            address = dbPatientsPortal.readAddressFromPatient(patient.idPatient)
            billAddress = dbPatientsPortal.readBillAddressFromPatient(patient.idPatient)
            contactPerson = dbPatientsPortal.readContactPersonFromPatient(patient.idPatient)
            withContext(Dispatchers.Main) {
                setupUI()
                setupFullView()
            }
        }

        binding.miniProfileView.startAnimation(scrollToDown)
        binding.miniProfileView.visibility = View.VISIBLE

        binding.profileImage.setOnClickListener {
            if (!isExpanded) {
                binding.profileBigImageBackground.visibility = View.VISIBLE
                binding.profileBigImageBackground.startAnimation(fadeIn)
                binding.profileBigImage.visibility = View.VISIBLE
                binding.profileBigImage.startAnimation(scaleUpAnimation)
            } else {
                binding.profileBigImage.startAnimation(scaleDownAnimation)
                binding.profileBigImage.visibility = View.GONE
                binding.profileBigImageBackground.startAnimation(fadeOut)
                binding.profileBigImageBackground.visibility = View.GONE
            }
            isExpanded = !isExpanded
        }

        binding.clMain.setOnClickListener {
            if (isExpanded) {
                binding.profileBigImage.startAnimation(scaleDownAnimation)
                binding.profileBigImage.visibility = View.GONE
                binding.profileBigImageBackground.startAnimation(fadeOut)
                binding.profileBigImageBackground.visibility = View.GONE
                isExpanded = false
            }
        }

        binding.btnSeeFullProfile.setOnClickListener {
            if (isExpanded) {
                binding.profileBigImage.startAnimation(scaleDownAnimation)
                binding.profileBigImage.visibility = View.GONE
                binding.profileBigImageBackground.startAnimation(fadeOut)
                binding.profileBigImageBackground.visibility = View.GONE
                isExpanded = false
            }
            binding.miniProfileView.startAnimation(scrollToUp)
            binding.miniProfileView.visibility = View.GONE
            binding.fullProfileView.startAnimation(fadeIn)
            binding.fullProfileView.visibility = View.VISIBLE
        }

        binding.backToMiniView.setOnClickListener {
            binding.fullProfileView.startAnimation(fadeOut)
            binding.fullProfileView.visibility = View.GONE
            setupUI()
            binding.miniProfileView.startAnimation(scrollToDown)
            binding.miniProfileView.visibility = View.VISIBLE
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setupFullView() {
        setPhotoCardView()
        setPersonalDataCardView()
        setNationalityCardView()
        setAddressCardView()
        setPhoneCardView()
        setMailCardView()
        setBillAddressCardView()
        setEducationCardView()
        setSocialAspectCardView()
        setCulturalAspectCardView()
        setContactPersonCardView()
        setDisabilityCardView()
    }



    private fun setPhotoCardView() {
        with(binding.itemPhoto) {

            // Reading Mode Set

            if (patient.intImage != 0) { profileImage.setImageResource(patient.intImage) } else { Glide.with(this@ProfileFragment).load(patient.uriImage).into(binding.itemPhoto.profileImage) }

            // Writing Mode Set

            btnEdit.setOnClickListener {
                setPhotoCardViewWritingMode()
            }
        }
    }

    private fun setPhotoCardViewWritingMode() {
        with(binding.itemPhoto) {
            miniCards.visibility = View.VISIBLE
            btnCancel.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                miniCards.visibility = View.GONE
                miniCardsConfirm.visibility = View.GONE
                miniCardsConfirmExisting.visibility = View.GONE
                btnCancel.visibility = View.GONE
                setPhotoCardView()
            }
            btnTakePhoto.setOnClickListener {
                // Crear un intent para abrir la cámara
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // La aplicación tiene permisos de cámara, puedes abrir la cámara aquí.
                    openCamera()
                } else {
                    // La aplicación no tiene permisos de cámara, solicita los permisos.
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                }
            }
            btnChooseExisting.setOnClickListener {
                openFilePicker()
            }
        }
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
            .into(binding.itemPhoto.profileImage)
        binding.itemPhoto.miniCards.visibility = View.GONE
        binding.itemPhoto.miniCardsConfirmExisting.visibility = View.VISIBLE
        binding.itemPhoto.btnReChooseExisting.setOnClickListener {
            openFilePicker()
        }
        binding.itemPhoto.btnSaveChangeExisting.setOnClickListener {
            // Verificar y solicitar permisos de almacenamiento si es necesario
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Llamar a tu método para obtener la ruta aquí
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalIntImage = patient.intImage
                    val originalUriImage = patient.uriImage
                    patient.intImage = 0
                    patient.uriImage = imagePath
                    val editPhotoPatient = dbPatientsPortal.editPatient(patient, getString(R.string.photo_tag))
                    withContext(Dispatchers.Main) {
                        if (editPhotoPatient) {
                            binding.itemPhoto.miniCardsConfirmExisting.visibility = View.GONE
                            binding.itemPhoto.btnCancel.visibility = View.GONE
                            setPhotoCardView()
                        } else {
                            patient.intImage = originalIntImage
                            patient.uriImage = originalUriImage
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.i("Image", "Entro aca")
            }
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
        if (requestCode == requestImageCapture && resultCode == RESULT_OK) {
            val imageFile = File(currentPhotoPath)
            if (imageFile.exists()) {
                // El archivo de imagen existe, intenta decodificarlo y mostrarlo en el ImageView.
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap != null) {
                    binding.itemPhoto.profileImage.setImageBitmap(bitmap)
                    binding.itemPhoto.miniCards.visibility = View.GONE
                    binding.itemPhoto.miniCardsConfirm.visibility = View.VISIBLE
                    binding.itemPhoto.btnReTakePhoto.setOnClickListener {
                        // Crear un intent para abrir la cámara
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            // La aplicación tiene permisos de cámara, puedes abrir la cámara aquí.
                            openCamera()
                        } else {
                            // La aplicación no tiene permisos de cámara, solicita los permisos.
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestCode)
                        }
                    }
                    binding.itemPhoto.btnSaveChange.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val originalIntImage = patient.intImage
                            val originalUriImage = patient.uriImage
                            patient.intImage = 0
                            patient.uriImage = imageFile.absolutePath
                            val editPhotoPatient = dbPatientsPortal.editPatient(patient, getString(R.string.photo_tag))
                            withContext(Dispatchers.Main) {
                                if (editPhotoPatient) {
                                    binding.itemPhoto.miniCardsConfirm.visibility = View.GONE
                                    binding.itemPhoto.btnCancel.visibility = View.GONE
                                    setPhotoCardView()
                                } else {
                                    patient.intImage = originalIntImage
                                    patient.uriImage = originalUriImage
                                    Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Log.e("PhotoDecode", "Failed to decode the captured image")
                }
            } else {
                Log.e("PhotoFile", "Image file does not exist at path: ${imageFile.absolutePath}")
            }

        }
    }


    private fun setPersonalDataCardView() {
        with(binding.itemPersonalData) {

            // Reading Mode Set

            tvDisplayedName.text = if (patient.name != "") { patient.name } else { loadEmptyFieldText(tvDisplayedName) }
            tvDisplayedLastName.text = if (patient.lastName != "") { patient.lastName } else { loadEmptyFieldText(tvDisplayedLastName) }
            tvDisplayedMotherLastName.text = if (patient.mothersLastName != "") { patient.mothersLastName } else { loadEmptyFieldText(tvDisplayedMotherLastName) }
            tvDisplayedBirthday.text = if (patient.birthday != "") { dateSimplePatientTestConverter(patient.birthday) } else { loadEmptyFieldText(tvDisplayedBirthday) }
            tvDisplayedDocumentType.text = if (patient.documentType != "") { patient.documentType } else { loadEmptyFieldText(tvDisplayedDocumentType) }
            tvDisplayedDocument.text = if (patient.documentNumber != "") { patient.documentNumber } else { loadEmptyFieldText(tvDisplayedDocument) }
            tvDisplayedBiologicalSex.text = if (patient.biologicalSex != "") { patient.biologicalSex } else { loadEmptyFieldText(tvDisplayedBiologicalSex) }
            tvDisplayedGender.text = if (patient.gender != "") { patient.gender } else { loadEmptyFieldText(tvDisplayedGender) }

            // Writing Mode Set

            tvEditName.setText(patient.name)
            tvEditLastName.setText(patient.lastName)
            tvEditMotherLastName.setText(patient.mothersLastName)
            tvDisplayedBirthdayW.text = patient.birthday
            val docTypes = arrayOf("", "CI", "CIE", "CM", "DNI", "DNM", "LC", "LE", "PAS")
            val adapterDoc = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, docTypes)
            adapterDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerDocumentType) {
                adapter = adapterDoc
                setSelection(0)
                docTypes.forEachIndexed { i, doc ->
                    if (doc == patient.documentType) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerDocumentType.selectedItem.toString() }
                }
            }
            tvEditDocument.setText(patient.documentNumber)
            val biologicalSex = arrayOf("", "F", "M")
            val adapterBiologicalSex = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, biologicalSex)
            adapterBiologicalSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerBiologicalSex) {
                adapter = adapterBiologicalSex
                setSelection(0)
                biologicalSex.forEachIndexed { i, bS ->
                    if (bS == patient.biologicalSex) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerBiologicalSex.selectedItem.toString() }
                }
            }
            val genderTypes = arrayOf("", "HOMBRE", "MUJER", "TRANS HOMBRE", "TRANS MUJER", "INTERGENERO")
            val adapterGenders = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderTypes)
            adapterGenders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerGender) {
                adapter = adapterGenders
                setSelection(0)
                genderTypes.forEachIndexed { i, g ->
                    if (g == patient.gender) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerGender.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setPersonalDataCardViewWritingMode()
            }
        }
    }

    private fun setPersonalDataCardViewWritingMode() {
        with(binding.itemPersonalData) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalName = patient.name
                    val originalLastName = patient.lastName
                    val originalMothersLastName = patient.mothersLastName
                    val originalDocumentType = patient.documentType
                    val originalDocument = patient.documentNumber
                    val originalBiologicalSex = patient.biologicalSex
                    val originalGender = patient.gender
                    patient.name = tvEditName.text.toString()
                    patient.lastName = tvEditLastName.text.toString()
                    patient.mothersLastName = tvEditMotherLastName.text.toString()
                    patient.documentType = tvSpinnerDocumentType.selectedItem.toString()
                    patient.documentNumber = tvEditDocument.text.toString()
                    patient.biologicalSex = tvSpinnerBiologicalSex.selectedItem.toString()
                    patient.gender = tvSpinnerGender.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.personaldata_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setPersonalDataCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.name = originalName
                            patient.lastName = originalLastName
                            patient.mothersLastName = originalMothersLastName
                            patient.documentType = originalDocumentType
                            patient.documentNumber = originalDocument
                            patient.biologicalSex = originalBiologicalSex
                            patient.gender = originalGender
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setNationalityCardView() {
        with(binding.itemNationality) {

            // Reading Mode Set

            tvDisplayedLanguage.text = if (patient.language != "") { patient.language } else { loadEmptyFieldText(tvDisplayedLanguage) }
            tvDisplayedCountry.text = if (patient.countryBirth != "") { patient.countryBirth } else { loadEmptyFieldText(tvDisplayedCountry) }

            // Writing Mode Set

            tvEditLanguage.setText(patient.language)
            val adapterCountries = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCountries)
            adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerCountry) {
                adapter = adapterCountries
                setSelection(0)
                arrayCountries.forEachIndexed { i, country ->
                    if (country == patient.countryBirth) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerCountry.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setNationalityCardViewWritingMode()
            }
        }
    }

    private fun setNationalityCardViewWritingMode() {
        with(binding.itemNationality) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalLanguage = patient.language
                    val originalCountry = patient.countryBirth
                    patient.language = tvEditLanguage.text.toString()
                    patient.countryBirth = tvSpinnerCountry.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.nationality_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setNationalityCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.language = originalLanguage
                            patient.countryBirth = originalCountry
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setAddressCardView() {
        with(binding.itemAddress) {

            // Reading Mode Set

            tvDisplayedStreet.text = if (address.street != "") { address.street } else { loadEmptyFieldText(tvDisplayedStreet) }
            tvDisplayedNumber.text = if (address.number != "") { address.number } else { loadEmptyFieldText(tvDisplayedNumber) }
            tvDisplayedFloor.text = if (address.floor != "") { address.floor } else { loadEmptyFieldText(tvDisplayedFloor) }
            tvDisplayedDepartment.text = if (address.department != "") { address.department } else { loadEmptyFieldText(tvDisplayedDepartment) }
            tvDisplayedObservations.text = if (address.observations != "") { address.observations } else { loadEmptyFieldText(tvDisplayedObservations) }
            tvDisplayedPostalCode.text = if (address.postalCode != "") { address.postalCode } else { loadEmptyFieldText(tvDisplayedPostalCode) }
            tvDisplayedCountry.text = if (address.country != "") { address.country } else { loadEmptyFieldText(tvDisplayedCountry) }
            tvDisplayedProvince.text = if (address.province != "") { address.province } else { loadEmptyFieldText(tvDisplayedProvince) }
            tvDisplayedLocality.text = if (address.locality != "") { address.locality } else { loadEmptyFieldText(tvDisplayedLocality) }

            // Writing Mode Set

            tvEditStreet.setText(address.street)
            tvEditNumber.setText(address.number)
            tvEditFloor.setText(address.floor)
            tvEditDepartment.setText(address.department)
            tvEditObservations.setText(address.observations)
            postalCodeShippingAddress.setText(address.postalCode)
            tvDisplayedCountryW.text = address.country
            postalCodeShippingAddress.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboardOnFragment(requireActivity(), binding.clMain)
                    setSpinnerProvince(postalCodeShippingAddress.text.toString(), spinnerProvinceShippingAddress, profileCardViewsAddressBinding = this)
                    return@setOnKeyListener true }
                false
            }
            postalCodeShippingAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val text = it.toString()
                        if (text.isNotEmpty()) {
                            if (text.length == 4) {
                                hideKeyboardOnFragment(requireActivity(), binding.clMain)
                                setSpinnerProvince(postalCodeShippingAddress.text.toString(), spinnerProvinceShippingAddress, profileCardViewsAddressBinding = this@with)
                            }
                        }
                    }
                }
            })
            setKnownProvinceSpinner(spinnerProvinceShippingAddress, address.province)
            setKnownLocalitySpinner(spinnerLocalityShippingAddress, address.locality)
            cbLocalityShippingNotFound.setOnClickListener { notFoundShippingLocality(this) }

            btnEdit.setOnClickListener {
                setAddressCardViewWritingMode()
            }
        }
    }

    private fun setAddressCardViewWritingMode() {
        with(binding.itemAddress) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                setAddressCardView()
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalStreet = address.street
                    val originalNumber = address.number
                    val originalFloor = address.floor
                    val originalDepartment = address.department
                    val originalObservations = address.observations
                    val originalPostalCode = address.postalCode
                    val originalProvince = address.province
                    val originalLocality = address.locality
                    address.street = tvEditStreet.text.toString()
                    address.number = tvEditNumber.text.toString()
                    address.floor = tvEditFloor.text.toString()
                    address.department = tvEditDepartment.text.toString()
                    address.observations = tvEditObservations.text.toString()
                    address.postalCode = postalCodeShippingAddress.text.toString()
                    address.province = spinnerProvinceShippingAddress.selectedItem.toString()
                    address.locality = if (localityShippingAddress.text.toString() != "") {
                        localityShippingAddress.text.toString()
                    } else {
                        spinnerLocalityShippingAddress.selectedItem.toString()
                    }

                    val editAddressPatient = dbPatientsPortal.editAddress(address)
                    withContext(Dispatchers.Main) {
                        if (editAddressPatient) {
                            clWritingView.visibility = View.GONE
                            setAddressCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            address.street = originalStreet
                            address.number = originalNumber
                            address.floor = originalFloor
                            address.department = originalDepartment
                            address.observations = originalObservations
                            address.postalCode = originalPostalCode
                            address.province = originalProvince
                            address.locality = originalLocality
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setPhoneCardView() {
        with(binding.itemPhone) {

            // Reading Mode Set

            tvDisplayedPhoneHome.text = if (phones.phoneHome != "") { phones.phoneHome } else { loadEmptyFieldText(tvDisplayedPhoneHome) }
            tvDisplayedPhoneHomeAlternative.text = if (phones.alternativePhoneHome != "") { phones.alternativePhoneHome } else { loadEmptyFieldText(tvDisplayedPhoneHomeAlternative) }
            tvDisplayedPhoneParticular.text = if (phones.particularPhone != "") { phones.particularPhone } else { loadEmptyFieldText(tvDisplayedPhoneParticular) }
            tvDisplayedCellPhone.text = if (phones.cellphone != "") { phones.cellphone } else { loadEmptyFieldText(tvDisplayedCellPhone) }
            tvDisplayedCompany.text = if (phones.company != "") { phones.company } else { loadEmptyFieldText(tvDisplayedCompany) }
            tvDisplayedFax.text = if (phones.fax != "") { phones.fax } else { loadEmptyFieldText(tvDisplayedFax) }
            tvDisplayedPhoneWork.text = if (phones.phoneWork != "") { phones.phoneWork } else { loadEmptyFieldText(tvDisplayedPhoneWork) }

            // Writing Mode Set

            tvEditPhoneHome.setText(phones.phoneHome)
            tvEditPhoneHomeAlternative.setText(phones.alternativePhoneHome)
            tvEditPhoneParticular.setText(phones.particularPhone)
            tvEditCellphone.setText(phones.cellphone)
            val arrayCompanies = arrayOf("", "Personal", "Movistar", "Claro", "Nextel", "Antel (UR)", "Movistar (UR)", "Tuenti")
            val adapterCompanies = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCompanies)
            adapterCompanies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerCompany) {
                adapter = adapterCompanies
                setSelection(0)
                arrayCompanies.forEachIndexed { i, company ->
                    if (company == phones.company) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerCompany.selectedItem.toString() }
                }
            }
            tvEditFax.setText(phones.fax)
            tvEditPhoneWork.setText(phones.phoneWork)

            btnEdit.setOnClickListener {
                setPhoneCardViewWritingMode()
            }
        }
    }

    private fun setPhoneCardViewWritingMode() {
        with(binding.itemPhone) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalPhoneHome = phones.phoneHome
                    val originalAlternativePhoneHome = phones.alternativePhoneHome
                    val originalParticularPhone = phones.particularPhone
                    val originalCellphone = phones.cellphone
                    val originalCompany = phones.company
                    val originalFax = phones.fax
                    val originalPhoneWork = phones.phoneWork
                    phones.phoneHome = tvEditPhoneHome.text.toString()
                    phones.alternativePhoneHome = tvEditPhoneParticular.text.toString()
                    phones.particularPhone = tvEditPhoneParticular.text.toString()
                    phones.cellphone = tvEditCellphone.text.toString()
                    phones.company = tvSpinnerCompany.selectedItem.toString()
                    phones.fax = tvEditFax.text.toString()
                    phones.phoneWork = tvEditPhoneWork.text.toString()
                    val editPatientPhonesInfo = dbPatientsPortal.editPhones(phones)
                    withContext(Dispatchers.Main) {
                        if (editPatientPhonesInfo) {
                            clWritingView.visibility = View.GONE
                            setPhoneCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            phones.phoneHome = originalPhoneHome
                            phones.alternativePhoneHome = originalAlternativePhoneHome
                            phones.particularPhone = originalParticularPhone
                            phones.cellphone = originalCellphone
                            phones.company = originalCompany
                            phones.fax = originalFax
                            phones.phoneWork = originalPhoneWork
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setMailCardView() {
        with(binding.itemMail) {

            // Reading Mode Set

            tvDisplayedPrincipalMail.text = if (patient.email != "") { patient.email } else { loadEmptyFieldText(tvDisplayedPrincipalMail) }
            tvDisplayedAlternativeMail.text = if (patient.alternativeEmail != "") { patient.alternativeEmail } else { loadEmptyFieldText(tvDisplayedAlternativeMail) }

            // Writing Mode Set

            tvEditMail.setText(patient.email)
            tvEditAlternativeMail.setText(patient.alternativeEmail)

            btnEdit.setOnClickListener {
                setMailCardViewWritingMode()
            }
        }
    }

    private fun setMailCardViewWritingMode() {
        with(binding.itemMail) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalMail = patient.email
                    val originalAlternativeMail = patient.alternativeEmail
                    patient.email = tvEditMail.text.toString()
                    patient.alternativeEmail = tvEditAlternativeMail.text.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.mail_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setMailCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.email = originalMail
                            patient.alternativeEmail = originalAlternativeMail
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }



    private fun setBillAddressCardView() {
        with(binding.itemBillAddress) {

            // Reading Mode Set

            tvDisplayedStreet.text = if (billAddress.street != "") { billAddress.street } else { loadEmptyFieldText(tvDisplayedStreet) }
            tvDisplayedNumber.text = if (billAddress.number != "") { billAddress.number } else { loadEmptyFieldText(tvDisplayedNumber) }
            tvDisplayedFloor.text = if (billAddress.floor != "") { billAddress.floor } else { loadEmptyFieldText(tvDisplayedFloor) }
            tvDisplayedDepartment.text = if (billAddress.department != "") { billAddress.department } else { loadEmptyFieldText(tvDisplayedDepartment) }
            tvDisplayedObservations.text = if (billAddress.observations != "") { billAddress.observations } else { loadEmptyFieldText(tvDisplayedObservations) }
            tvDisplayedPostalCode.text = if (billAddress.postalCode != "") { billAddress.postalCode } else { loadEmptyFieldText(tvDisplayedPostalCode) }
            tvDisplayedCountry.text = if (billAddress.country != "") { billAddress.country } else { loadEmptyFieldText(tvDisplayedCountry) }
            tvDisplayedProvince.text = if (billAddress.province != "") { billAddress.province } else { loadEmptyFieldText(tvDisplayedProvince) }
            tvDisplayedLocality.text = if (billAddress.locality != "") { billAddress.locality } else { loadEmptyFieldText(tvDisplayedLocality) }

            // Writing Mode Set

            tvEditStreet.setText(billAddress.street)
            tvEditNumber.setText(billAddress.number)
            tvEditFloor.setText(billAddress.floor)
            tvEditDepartment.setText(billAddress.department)
            tvEditObservations.setText(billAddress.observations)
            postalCodeBillAddress.setText(billAddress.postalCode)
            tvDisplayedCountryW.text = billAddress.country
            postalCodeBillAddress.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboardOnFragment(requireActivity(), binding.clMain)
                    setSpinnerProvince(postalCodeBillAddress.text.toString(), spinnerProvinceBillAddress, profileCardViewsBillAddressBinding = this)
                    return@setOnKeyListener true
                }
                false
            }
            postalCodeBillAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val text = it.toString()
                        if (text.isNotEmpty()) {
                            if (text.length == 4) {
                                hideKeyboardOnFragment(requireActivity(), binding.clMain)
                                setSpinnerProvince(postalCodeBillAddress.text.toString(), spinnerProvinceBillAddress, profileCardViewsBillAddressBinding = this@with)
                            }
                        }
                    }
                }
            })
            setKnownProvinceSpinner(spinnerProvinceBillAddress, billAddress.province)
            setKnownLocalitySpinner(spinnerLocalityBillAddress, billAddress.locality)
            cbLocalityBillNotFound.setOnClickListener { notFoundBillLocality(this) }

            btnEdit.setOnClickListener {
                setBillAddressCardViewWritingMode()
            }
        }
    }

    private fun setBillAddressCardViewWritingMode() {
        with(binding.itemBillAddress) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                setBillAddressCardView()
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalStreet = billAddress.street
                    val originalNumber = billAddress.number
                    val originalFloor = billAddress.floor
                    val originalDepartment = billAddress.department
                    val originalObservations = billAddress.observations
                    val originalPostalCode = billAddress.postalCode
                    val originalProvince = billAddress.province
                    val originalLocality = billAddress.locality
                    billAddress.street = tvEditStreet.text.toString()
                    billAddress.number = tvEditNumber.text.toString()
                    billAddress.floor = tvEditFloor.text.toString()
                    billAddress.department = tvEditDepartment.text.toString()
                    billAddress.observations = tvEditObservations.text.toString()
                    billAddress.postalCode = postalCodeBillAddress.text.toString()
                    billAddress.province = spinnerProvinceBillAddress.selectedItem.toString()
                    billAddress.locality = if (localityBillAddress.text.toString() != "") {
                        localityBillAddress.text.toString()
                    } else {
                        spinnerLocalityBillAddress.selectedItem.toString()
                    }

                    val editBillAddressPatient = dbPatientsPortal.editBillAddress(billAddress)
                    withContext(Dispatchers.Main) {
                        if (editBillAddressPatient) {
                            clWritingView.visibility = View.GONE
                            setBillAddressCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            billAddress.street = originalStreet
                            billAddress.number = originalNumber
                            billAddress.floor = originalFloor
                            billAddress.department = originalDepartment
                            billAddress.observations = originalObservations
                            billAddress.postalCode = originalPostalCode
                            billAddress.province = originalProvince
                            billAddress.locality = originalLocality
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setEducationCardView() {
        with(binding.itemEducation) {

            // Reading Mode Set

            tvDisplayedEducationLevelReached.text = if (patient.educationLevelReached != "") { patient.educationLevelReached } else { loadEmptyFieldText(tvDisplayedEducationLevelReached) }

            // Writing Mode Set

            val educationLevels = arrayOf("", "Primario incompleto", "Primario completo", "Secundario incompleto", "Secundario completo", "Terciario incompleto", "Terciario completo", "Universitario incompleto", "Universitario completo")
            val adapterEducationLevels = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, educationLevels)
            adapterEducationLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerEducation) {
                adapter = adapterEducationLevels
                setSelection(0)
                educationLevels.forEachIndexed { i, e ->
                    if (e == patient.educationLevelReached) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerEducation.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setEducationCardViewWritingMode()
            }
        }
    }

    private fun setEducationCardViewWritingMode() {
        with(binding.itemEducation) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalEducationLevelReached = patient.educationLevelReached
                    patient.educationLevelReached = tvSpinnerEducation.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.education_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setEducationCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.educationLevelReached = originalEducationLevelReached
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }



    private fun setSocialAspectCardView() {
        with(binding.itemSocialAspect) {

            // Reading Mode Set

            tvDisplayedHomeType.text = if (patient.homeType != "") { patient.homeType } else { loadEmptyFieldText(tvDisplayedHomeType) }
            tvDisplayedWhoLiveWith.text = if (patient.whoLiveWith != "") { patient.whoLiveWith } else { loadEmptyFieldText(tvDisplayedWhoLiveWith) }

            // Writing Mode Set

            val homeTypes = arrayOf("", "Departamento", "Casilla / Rancho", "Geriátrico", "Pensión / Conventillo", "Casa")
            val adapterHomeTypes = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, homeTypes)
            adapterHomeTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerHomeType) {
                adapter = adapterHomeTypes
                setSelection(0)
                homeTypes.forEachIndexed { i, hT ->
                    if (hT == patient.homeType) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerHomeType.selectedItem.toString() }
                }
            }

            val whoLiveWithOptions = arrayOf("", "Solo", "Con familiar", "Con cuidador")
            val adapterWhoLiveWith = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, whoLiveWithOptions)
            adapterWhoLiveWith.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerWhoLiveWith) {
                adapter = adapterWhoLiveWith
                setSelection(0)
                whoLiveWithOptions.forEachIndexed { i, wlw ->
                    if (wlw == patient.whoLiveWith) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerWhoLiveWith.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setSocialAspectCardViewWritingMode()
            }

        }
    }

    private fun setSocialAspectCardViewWritingMode() {
        with(binding.itemSocialAspect) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalHomeType = patient.homeType
                    val originalWhoLiveWith = patient.whoLiveWith
                    patient.homeType = tvSpinnerHomeType.selectedItem.toString()
                    patient.whoLiveWith = tvSpinnerWhoLiveWith.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.socialaspect_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setSocialAspectCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.homeType = originalHomeType
                            patient.whoLiveWith = originalWhoLiveWith
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }



    private fun setCulturalAspectCardView() {
        with(binding.itemCulturalAspect) {

            // Reading Mode Set

            tvDisplayedReligion.text = if (patient.religion != "") { patient.religion } else { loadEmptyFieldText(tvDisplayedReligion) }

            // Writing Mode Set

            val religions = arrayOf("", "Budismo", "Confucionismo", "Illuminatti", "Cristianismo", "Druidisma", "Hinduismo", "Jainismo", "Judaismo", "Musulmanismo", "Taoismo", "Testigo de Jehova", "Vudu", "Yoruba", "Zoroastrismo", "Otros")
            val adapterReligions = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, religions)
            adapterReligions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerReligion) {
                adapter = adapterReligions
                setSelection(0)
                religions.forEachIndexed { i, r ->
                    if (r == patient.religion) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerReligion.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setCulturalAspectCardViewWritingMode()
            }
        }
    }

    private fun setCulturalAspectCardViewWritingMode() {
        with(binding.itemCulturalAspect) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalReligion = patient.religion
                    patient.religion = tvSpinnerReligion.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.culturalaspect_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setCulturalAspectCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.religion = originalReligion
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setContactPersonCardView() {
        with(binding.itemContactPerson) {

            // Reading Mode Set

            tvDisplayedNames.text = if (contactPerson.names != "") { contactPerson.names } else { loadEmptyFieldText(tvDisplayedNames) }
            tvDisplayedLastNames.text = if (contactPerson.lastNames != "") { contactPerson.lastNames } else { loadEmptyFieldText(tvDisplayedLastNames) }
            tvDisplayedRelationship.text = if (contactPerson.relationship != "") { contactPerson.relationship } else { loadEmptyFieldText(tvDisplayedRelationship) }
            tvDisplayedAddress.text = if (contactPerson.address != "") { contactPerson.address } else { loadEmptyFieldText(tvDisplayedAddress) }
            tvDisplayedPhone.text = if (contactPerson.phone != "") { contactPerson.phone } else { loadEmptyFieldText(tvDisplayedPhone) }

            // Writing Mode Set
            tvEditNames.setText(contactPerson.names)
            tvEditLastNames.setText(contactPerson.lastNames)
            tvEditRelationship.setText(contactPerson.relationship)
            tvEditAddress.setText(contactPerson.address)
            tvEditPhone.setText(contactPerson.phone)

            btnEdit.setOnClickListener {
                setContactPersonCardViewWritingMode()
            }
        }
    }

    private fun setContactPersonCardViewWritingMode() {
        with(binding.itemContactPerson) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalNames = contactPerson.names
                    val originalLastNames = contactPerson.lastNames
                    val originalRelationship = contactPerson.relationship
                    val originalAddress = contactPerson.address
                    val originalPhone = contactPerson.phone
                    contactPerson.names = tvEditNames.text.toString()
                    contactPerson.lastNames = tvEditLastNames.text.toString()
                    contactPerson.relationship = tvEditRelationship.text.toString()
                    contactPerson.address = tvEditAddress.text.toString()
                    contactPerson.phone = tvEditPhone.text.toString()
                    val editContactPersonInfo = dbPatientsPortal.editContactPerson(contactPerson)
                    withContext(Dispatchers.Main) {
                        if (editContactPersonInfo) {
                            clWritingView.visibility = View.GONE
                            setContactPersonCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            contactPerson.names = originalNames
                            contactPerson.lastNames = originalLastNames
                            contactPerson.relationship = originalRelationship
                            contactPerson.address = originalAddress
                            contactPerson.phone = originalPhone
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setDisabilityCardView() {
        with(binding.itemDisability) {

            // Reading Mode Set

            tvDisplayedDisability.text = if (patient.disability != "") { patient.disability } else { loadEmptyFieldText(tvDisplayedDisability) }

            // Writing Mode Set

            val disabilities = arrayOf("", "Visual", "Auditiva", "Motora", "Mental", "No posee")
            val adapterDisabilities = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, disabilities)
            adapterDisabilities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(tvSpinnerDisability) {
                adapter = adapterDisabilities
                setSelection(0)
                disabilities.forEachIndexed { i, d ->
                    if (d == patient.disability) {
                        setSelection(i)
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) { Toast.makeText(requireContext(), "NothingSelected", Toast.LENGTH_SHORT).show() }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { tvSpinnerDisability.selectedItem.toString() }
                }
            }

            btnEdit.setOnClickListener {
                setDisabilityCardViewWritingMode()
            }
        }
    }

    private fun setDisabilityCardViewWritingMode() {
        with(binding.itemDisability) {
            clReadingView.visibility = View.GONE
            clWritingView.visibility = View.VISIBLE
            btnCancel.setOnClickListener {
                clWritingView.visibility = View.GONE
                clReadingView.visibility = View.VISIBLE
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val originalDisability = patient.disability
                    patient.disability = tvSpinnerDisability.selectedItem.toString()
                    val editPatientInfo = dbPatientsPortal.editPatient(patient, getString(R.string.disability_tag))
                    withContext(Dispatchers.Main) {
                        if (editPatientInfo) {
                            clWritingView.visibility = View.GONE
                            setDisabilityCardView()
                            clReadingView.visibility = View.VISIBLE
                        } else {
                            patient.disability = originalDisability
                            Toast.makeText(requireContext(), "Hubo un error al actualizar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }



    private fun loadEmptyFieldText(tv: TextView): String {
        tv.setTypeface(null, Typeface.ITALIC)
        tv.setTextColor(requireContext().getColor(R.color.red))
        return getString(R.string.sin_cargar)
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        with(patient) {
            if (intImage != 0) {
                binding.profileImage.setImageResource(intImage)
                binding.profileBigImage.setImageResource(intImage)
            } else {
                Glide.with(this@ProfileFragment).load(uriImage).into(binding.profileImage)
                Glide.with(this@ProfileFragment).load(uriImage).into(binding.profileBigImage)
            }
            binding.tvName.text = "$name $lastName $mothersLastName"
            binding.tvIdNumber.text = "ID N°: $idClinicalHistory"
            binding.tvMail.text = email
        }
    }


    private fun notFoundBillLocality(profileCardViewsBillAddressBinding: ProfileCardViewsBillAddressBinding) {
        if (profileCardViewsBillAddressBinding.cbLocalityBillNotFound.isChecked) {
            setSpinnerAllProvinces(profileCardViewsBillAddressBinding.spinnerProvinceBillAddress)
            profileCardViewsBillAddressBinding.spinnerLocalityBillAddress.visibility = View.GONE
            profileCardViewsBillAddressBinding.localityBillAddress.setTextColor(resources.getColor(R.color.blue, null))
            profileCardViewsBillAddressBinding.localityBillAddress.visibility = View.VISIBLE
        } else {
            profileCardViewsBillAddressBinding.spinnerLocalityBillAddress.visibility = View.VISIBLE
            profileCardViewsBillAddressBinding.localityBillAddress.visibility = View.GONE
        }
    }
    private fun notFoundShippingLocality(profileCardViewsAddressBinding: ProfileCardViewsAddressBinding) {
        if (profileCardViewsAddressBinding.cbLocalityShippingNotFound.isChecked) {
            setSpinnerAllProvinces(profileCardViewsAddressBinding.spinnerProvinceShippingAddress)
            profileCardViewsAddressBinding.spinnerLocalityShippingAddress.visibility = View.GONE
            profileCardViewsAddressBinding.localityShippingAddress.setTextColor(resources.getColor(R.color.blue, null))
            profileCardViewsAddressBinding.localityShippingAddress.visibility = View.VISIBLE
        } else {
            profileCardViewsAddressBinding.spinnerLocalityShippingAddress.visibility = View.VISIBLE
            profileCardViewsAddressBinding.localityShippingAddress.visibility = View.GONE
        }
    }
    private fun setKnownProvinceSpinner(spinner: Spinner, provinceName: String) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf(provinceName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }
    private fun setKnownLocalitySpinner(spinner: Spinner, cityName: String) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf(cityName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }
    private fun setSpinnerAllProvinces(spinner: Spinner) {
        val list = ArrayList<String>()
        dbPatientsPortal.readAllProvinces().forEach { list.add(it.name) }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { spinner.selectedItem.toString() }
        }
    }
    private fun getArrayForAdapterProvinceSpinner(postalCode: String): ArrayList<String> {
        val list: ArrayList<String> = dbPatientsPortal.readProvinceForSpinner(postalCode)
        if (list.size == 0) { Toast.makeText(requireContext(), "No existe el codigo postal", Toast.LENGTH_SHORT).show() }
        return list
    }
    private fun getAdapterForProvinceSpinner(postalCode: String): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, getArrayForAdapterProvinceSpinner(postalCode))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    private fun setSpinnerProvince(postalCode: String, spinner: Spinner, profileCardViewsAddressBinding: ProfileCardViewsAddressBinding ?= null, profileCardViewsBillAddressBinding: ProfileCardViewsBillAddressBinding ?= null) {
        spinner.adapter = getAdapterForProvinceSpinner(postalCode)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (profileCardViewsBillAddressBinding != null) {
                    setLocalitySpinner(profileCardViewsBillAddressBinding.spinnerLocalityBillAddress, postalCode, spinner.selectedItem.toString())
                }
                if (profileCardViewsAddressBinding != null) {
                    setLocalitySpinner(profileCardViewsAddressBinding.spinnerLocalityShippingAddress, postalCode, spinner.selectedItem.toString())
                }
            }
        }
    }
    private fun getArrayForAdapterLocalitySpinner(postalCode: String, provinceName: String): ArrayList<String> {
        return dbPatientsPortal.readCitysForSpinner(postalCode, provinceName)
    }
    private fun setAdapterForLocalitySpinner(postalCode: String, provinceName: String): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, getArrayForAdapterLocalitySpinner(postalCode, provinceName))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    private fun setLocalitySpinner(spinner: Spinner, postalCode: String, provinceName: String) {
        spinner.adapter = setAdapterForLocalitySpinner(postalCode, provinceName)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { spinner.selectedItem.toString() }
        }
    }

}