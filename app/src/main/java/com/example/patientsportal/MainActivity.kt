package com.example.patientsportal

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.patientsportal.adapters.appointmentsAdapters.AppointmentsAheadMainActivityAdapter
import com.example.patientsportal.adapters.listPatientTestAdapters.PatientTestLatestMainActivityAdapter
import com.example.patientsportal.auth.AuthActivity
import com.example.patientsportal.databinding.ActivityMainBinding
import com.example.patientsportal.databinding.CustomAlertDialogNewsBinding
import com.example.patientsportal.databinding.ItemAheadAppointmentBinding
import com.example.patientsportal.db.DbPatientsPortal
import com.example.patientsportal.db.arrays.ArrayPatients.arrayPatients
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.entities.dbEntities.Patient
import com.example.patientsportal.fragments.MedicalTestResultDisplayer
import com.example.patientsportal.fragmentsBottomMenu.CredentialActivity
import com.example.patientsportal.fragmentsBottomMenu.NotificationsFragment
import com.example.patientsportal.fragmentsBottomMenu.ProfileFragment
import com.example.patientsportal.fragmentsBottomMenu.RefreshBadgeNoti
import com.example.patientsportal.fragmentsDrawerMenu.CardViewsFragment
import com.example.patientsportal.fragmentsDrawerMenu.HelpFragment
import com.example.patientsportal.fragmentsDrawerMenu.TwoPagesFragment
import com.example.patientsportal.fragmentsDrawerMenu.appointmentsSteps.AppointmentsStep5
import com.example.patientsportal.objects.AppointmentForAYearGenerator.generateAYearAppointmentsForEachDoctorPlace
import com.example.patientsportal.objects.DateConverter
import com.example.patientsportal.objects.DateConverter.checkPassedPrescriptionDate
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RefreshBadgeNoti {

    private lateinit var binding: ActivityMainBinding
    private val doubleClickTimeDelta = 300 // Intervalo de tiempo en milisegundos para el doble clic
    private var lastClickTime: Long = 0 // Variable para almacenar el tiempo del último clic en el botón "back"
    private val imageNewsFever = "https://www1.hospitalitaliano.org.ar/multimedia/archivos/noticias_imagenes/53/imagenes/53_167862_Agregar%20un%20subtitulo.png"
    private val imageNewsGynecology = "https://www1.hospitalitaliano.org.ar/multimedia/archivos/noticias_imagenes/53/imagenes/53_158742_BannerControles%20Ginecologicos%20(1).png"
    private val imageNewsAppOnPhone = "https://www1.hospitalitaliano.org.ar/multimedia/archivos/noticias_imagenes/53/imagenes/53_160504_Rediseno%20portada%202.png"
    private val imageTransportableCenters = "https://www1.hospitalitaliano.org.ar/multimedia/archivos/noticias_imagenes/53/imagenes/53_187802_Banner%20centros.png"
    private lateinit var adapterAppointmentsAhead: AppointmentsAheadMainActivityAdapter
    private lateinit var adapterLatestPatientTests: PatientTestLatestMainActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAppointmentsIfNotExists()
        checkForNotiEnter()
        updateAppointments()
        updatePrescriptions()
        setupDrawerNavigationBar()
        setupBottomNavigation()
        setInfoCarousel()
        setAdaptersAppointmentAndPatientTest()
    }

    private fun createAppointmentsIfNotExists() {
        val prefs = getSharedPreferences("MY PREF", MODE_PRIVATE)
        val appointmentsCreated = prefs.getBoolean("appointmentsCreated", false)
        if (!appointmentsCreated) {
            lifecycleScope.launch(Dispatchers.IO) { generateAYearAppointmentsForEachDoctorPlace(this@MainActivity) }
            val editor = prefs.edit()
            editor.putBoolean("appointmentsCreated", true)
            editor.apply()
        }
    }

    private fun checkForNotiEnter() {
        val title = intent.getStringExtra(getString(R.string.title_tag))
        val idNotification = intent.getIntExtra(getString(R.string.idnotification_tag), 0)

        if (title == getString(R.string.recordatorio_turno_title)) {
            binding.bottomNavigationView.menu.findItem(R.id.item_noti).isChecked = true
            binding.bottomNavigationView.menu.findItem(R.id.item_noti).isCheckable = true
            showNotiFragment(idNotification)
        }
    }


    private fun setAdaptersAppointmentAndPatientTest() {
        val dbPatientsPortal = DbPatientsPortal(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val listAppointments = dbPatientsPortal.readPatientAppointmentsAhead(getPatient().idPatient)
            val listPatientTests = dbPatientsPortal.readAllPatientTests(getPatient().idPatient)
            withContext(Dispatchers.Main) {
                if (listAppointments.size > 0) {
                    adapterAppointmentsAhead = AppointmentsAheadMainActivityAdapter(listAppointments) {appointment, position ->
                        showAppointmentDialog(appointment, position)
                    }
                    binding.rvAppointmentsAhead.adapter = adapterAppointmentsAhead
                    binding.pbCardsViews.visibility = View.GONE
                    binding.cvAppointmentsAhead.visibility = View.VISIBLE
                } else {
                    binding.pbCardsViews.visibility = View.GONE
                    binding.cvAppointmentsAhead.visibility = View.GONE
                }
                if (listPatientTests.size > 0) {
                    adapterLatestPatientTests = PatientTestLatestMainActivityAdapter(listPatientTests) {
                        showFragment(MedicalTestResultDisplayer(), getString(R.string.medicaltestresultdisplayer), getString(R.string.medicaltestresultdisplayer), link = it.urlResult)
                    }
                    binding.rvLatestMedicalTest.adapter = adapterLatestPatientTests
                    binding.pbCardsViews.visibility = View.GONE
                    binding.cvLatestMedicalTest.visibility = View.VISIBLE
                } else {
                    binding.pbCardsViews.visibility = View.GONE
                    binding.cvLatestMedicalTest.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAppointmentDialog(appointment: Appointment, position: Int) {
        val binding = ItemAheadAppointmentBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this)
            .setView(binding.root)
            .create()
        val animUp: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate_up)
        val animDown: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate_down)
        binding.ivDoctor.setImageResource(appointment.doctorSpeciality.doctor.intImage)
        binding.tvDate.text = DateConverter.dateAppointmentConverter(appointment.date)
        binding.tvDoctor.text = "${appointment.doctorSpeciality.doctor.name} ${appointment.doctorSpeciality.doctor.lastName}"
        binding.tvSpeciality.text = appointment.doctorSpeciality.speciality.name
        binding.tvPlace.text = appointment.place.address
        binding.cvItem.setOnClickListener {
            if (appointment.isExpanded) { collapseMiniCards(binding, animDown) } else { expandMiniCards(binding, animUp) }
            appointment.isExpanded = !appointment.isExpanded
        }
        binding.delete.setOnClickListener { showDeleteAppointmentDialog(appointment, position); alertDialog.dismiss() }
        binding.addToCalendar.setOnClickListener { addToCalendar(appointment); alertDialog.dismiss() }

        // Actualizar la visibilidad de las miniCards
        binding.miniCards.visibility = if (appointment.isExpanded) View.VISIBLE else View.GONE
        val anim = if (appointment.isExpanded) animUp else animDown
        binding.btnShowCards.startAnimation(anim)
        alertDialog.show()
    }

    private fun addToCalendar(appointment: Appointment) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val startDate = dateFormat.parse(appointment.date)
        val endDate = Date(startDate!!.time + 3600000)

        // Crear un nuevo evento en el calendario del dispositivo
        val contentResolver: ContentResolver = this.contentResolver
        val contentValues = ContentValues()

        // Configurar los detalles del evento
        contentValues.put(CalendarContract.Events.DTSTART, startDate.time) // Fecha de inicio del evento en milisegundos
        contentValues.put(CalendarContract.Events.DTEND, endDate.time) // Fecha de fin del evento en milisegundos (1 hora después)
        contentValues.put(CalendarContract.Events.TITLE, "Turno con el Dr. ${appointment.doctorSpeciality.doctor.lastName}")
        contentValues.put(CalendarContract.Events.DESCRIPTION, "Presentarse en ${appointment.place.address}")
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1) // ID del calendario (puedes obtenerlo desde la base de datos de calendario)
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

        // Insertar el evento en el calendario
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

        if (uri != null) {
            Toast.makeText(this, "Evento agregado al calendario del dispositivo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al agregar el evento al calendario del dispositivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteAppointmentDialog(appointment: Appointment, position: Int) {
        val dbPatientsPortal = DbPatientsPortal(this)
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(getString(R.string.desea_eliminar_este_turno))
            setPositiveButton(getString(R.string.si)) { _, _ ->
                if (dbPatientsPortal.updateReleaseAppointment(appointment)) {
                    adapterAppointmentsAhead.notifyItemRemoved(position)
                    showFragment(AppointmentsStep5(), getString(R.string.appointmentsstep5_tag), getString(R.string.appointmentsstep5_tag), appointmentCreatedOrDeleted = false)

                }
            }
            setNegativeButton(getString(R.string.no), null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun expandMiniCards(binding: ItemAheadAppointmentBinding, animUp: Animation) {
        binding.miniCards.visibility = View.VISIBLE
        val anim = ValueAnimator.ofInt(binding.cvItem.height, binding.cvItem.height + resources.getDimensionPixelSize(R.dimen.mini_cards_height))
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.cvItem.layoutParams.height = value
            binding.cvItem.requestLayout()
        }
        anim.duration = 500
        anim.start()
        binding.btnShowCards.startAnimation(animUp)
    }

    private fun collapseMiniCards(binding: ItemAheadAppointmentBinding, animDown: Animation) {
        binding.btnShowCards.startAnimation(animDown)
        val anim = ValueAnimator.ofInt(binding.cvItem.height, binding.cvItem.height - resources.getDimensionPixelSize(R.dimen.mini_cards_height))
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.cvItem.layoutParams.height = value
            binding.cvItem.requestLayout()
        }
        anim.duration = 500
        anim.start()
        Handler().postDelayed({
            binding.miniCards.visibility = View.GONE
        }, 500)
    }

    private fun updatePrescriptions() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dbPatientsPortal = DbPatientsPortal(this@MainActivity)
            dbPatientsPortal.readAllPrescriptionsByPatient(getPatient().idPatient, getString(R.string.current), "").forEach {
                if (checkPassedPrescriptionDate(it.drug.expiredDate)) { dbPatientsPortal.updatePassedPrescriptions(it.idPrescription) }
            }
        }
    }

    private fun updateAppointments() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dbPatientsPortal = DbPatientsPortal(this@MainActivity)
            dbPatientsPortal.updatePassedAppointments(getPatient().idPatient)
        }
    }

    private fun setupBottomNavigation() {
        setBadgeNoti()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_bottom_home -> { refresh(); true }
                R.id.item_noti -> { showNotiFragment(); true }
                R.id.item_credential -> { startActivity(Intent(this, CredentialActivity()::class.java)); true }
                R.id.item_profile -> { showFullFragment(ProfileFragment(), getString(R.string.profile_fragment_tag), getString(R.string.mi_perfil_title)); true }
                else -> false
            }
        }
    }

    private fun showNotiFragment(idNotification: Int ?= null) {
        if (binding.toolbarHome.visibility == View.GONE) { binding.toolbarHome.visibility = View.VISIBLE }
        closeFragments()
        val notiFrag = NotificationsFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.containerid_tag), binding.mainContainer.id)
        if (idNotification != null) { bundle.putInt(getString(R.string.idnotification_tag), idNotification) }
        notiFrag.refreshBadgeNoti = this
        notiFrag.arguments = bundle
        supportFragmentManager.beginTransaction().replace(binding.mainContainer.id, notiFrag, getString(R.string.notificationfragment_tag)).addToBackStack(getString(R.string.notificationfragment_tag)).commit()
        val spannableString = SpannableString(getString(R.string.notificaciones))
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, getString(R.string.notificaciones).length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTitleFragment.text = spannableString
        binding.tvTitleFragment.visibility = View.VISIBLE
        allGone(getString(R.string.notificationfragment_tag))
        binding.mainContainer.visibility = View.VISIBLE
    }

    private fun setupDrawerNavigationBar() {
        //animFrag = AnimationUtils.loadAnimation(this, R.anim.left_in)
        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvToolbarWelcomeUser.text = setWelcomeUser()
        binding.tvToolbarWelcomeUser.setOnClickListener {
            binding.bottomNavigationView.menu.findItem(R.id.item_profile).isChecked = true
            binding.bottomNavigationView.menu.findItem(R.id.item_profile).isCheckable = true
            showFullFragment(ProfileFragment(), getString(R.string.profile_fragment_tag), getString(R.string.mi_perfil_title))
        }
        val drawerToggle = ActionBarDrawerToggle(this, binding.dlHome, binding.toolbarHome, R.string.abrir, R.string.cerrar)
        binding.dlHome.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        binding.nvHome.itemIconTintList = null
        binding.nvHome.itemBackground = null
        binding.nvHome.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> { refresh() }
                R.id.item_appointment -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.turnos)) }
                R.id.item_medical_test -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.estudios)) }
                R.id.item_health_coverage -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.mi_cobertura)) }
                R.id.item_diary -> { showFragment(TwoPagesFragment(), getString(R.string.my_diary_fragment_tag), getString(R.string.documentos_clinicos), arrayOf(getString(R.string.por_fecha), getString(R.string.por_documento)), task = getString(R.string.clinicdocumentslist_tag)) }
                R.id.item_drugs -> { showFragment(TwoPagesFragment(), getString(R.string.drugs_fragment_tag), getString(R.string.medicamentos), arrayOf(getString(R.string.vigentes), getString(R.string.vencidos)), task = getString(R.string.prescriptionlist)) }
                R.id.item_doctors -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.mis_medicos)) }
                R.id.item_community -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.comunidades)) }
                R.id.item_provider_directory -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.cartilla)) }
                R.id.item_health_control -> { showFragment(CardViewsFragment(), getString(R.string.card_views_fragment_tag), getString(R.string.controles_de_salud)) }
                R.id.item_help -> { showFragment(HelpFragment(), getString(R.string.help_fragment_tag), getString(R.string.ayuda_title)) }
            }
            binding.dlHome.closeDrawers()
            true
        }
        findViewById<MaterialCardView>(R.id.btnCloseSession).setOnClickListener { signOut() }
    }

    private fun signOut() {
        val prefs: SharedPreferences = getSharedPreferences("MY PREF", MODE_PRIVATE)
        val prefsEd = prefs.edit()
        prefsEd.clear()
        prefsEd.apply()
        startActivity(Intent(this, AuthActivity()::class.java))
        finish()
    }

    private fun showFullFragment(fragment: Fragment, tag: String, title: String) {
        closeFragments()
        val bundle = Bundle()
        bundle.putString(getString(R.string.title_tag), title)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(binding.fullContainer.id, fragment, tag).addToBackStack(tag).commit()
        allGone(tag)
        binding.fullContainer.visibility = View.VISIBLE
    }

    private fun refresh() {
        closeFragments()
        allVisible()
    }

    private fun closeFragments() {
        if (supportFragmentManager.backStackEntryCount >= 1) { for (i in 0 until supportFragmentManager.backStackEntryCount) { supportFragmentManager.popBackStack() } }
        binding.tvTitleFragment.visibility = View.GONE
        binding.mainContainer.visibility = View.GONE
        binding.fullContainer.visibility = View.GONE
    }

    private fun setWelcomeUser(): SpannableStringBuilder {
        val cal = Calendar.getInstance()

        val welcome = when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 7..12 -> getString(R.string.buenos_dias)
            in 13..19 -> getString(R.string.buenas_tardes)
            else -> getString(R.string.buenas_noches)
        }

        val nombre = getPatient().name

        val spannableString = SpannableString(nombre)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, nombre.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)


        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(welcome).append(", ").append(spannableString).append("!")
        return spannableStringBuilder
    }

    private fun showFragment(fragment: Fragment, tag: String, title: String, tabTitles: Array<String> ?= null, task: String ?= null, link: String ?= null, appointmentCreatedOrDeleted: Boolean ?= null) {
        if (binding.toolbarHome.visibility == View.GONE) { binding.toolbarHome.visibility = View.VISIBLE }
        closeFragments()
        val bundle = Bundle()
        bundle.putString(getString(R.string.title_tag), title)
        bundle.putInt(getString(R.string.containerid_tag), binding.mainContainer.id)
        if (tabTitles != null) { bundle.putStringArray(getString(R.string.tabtitles_tag), tabTitles) }
        if (task != null) { bundle.putString(getString(R.string.task_tag), task) }
        if (link != null) { bundle.putString(getString(R.string.link_tag), link) }
        if (appointmentCreatedOrDeleted != null) { bundle.putBoolean(getString(R.string.appointmentcreatedordeleted), appointmentCreatedOrDeleted) }
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(binding.mainContainer.id, fragment, tag).addToBackStack(tag).commit()
        val spannableString = SpannableString(title)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTitleFragment.text = spannableString
        binding.tvTitleFragment.visibility = View.VISIBLE
        allGone(tag)
        binding.mainContainer.visibility = View.VISIBLE
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.dlHome.isDrawerOpen(GravityCompat.START)) {
            binding.dlHome.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < doubleClickTimeDelta) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    /*
                    val animLeftOut: Animation = AnimationUtils.loadAnimation(this, R.anim.left_out)
                    binding.********.startAnimation(animLeftOut)
                    animLeftOut.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                        */
                            supportFragmentManager.popBackStack()
                         /*
                        }
                        override fun onAnimationRepeat(animation: Animation?) {}
                    })

                          */
                    allVisible()
                } else {
                    lastClickTime = currentTime
                    val prefs = getSharedPreferences("MY PREF", MODE_PRIVATE)
                    val isToastShown = prefs.getBoolean("isToastShown", false)
                    if (!isToastShown) {
                        Toast.makeText(this, "Presiona 2 veces para volver al inicio", Toast.LENGTH_SHORT).show()
                        val editor = prefs.edit()
                        editor.putBoolean("isToastShown", true)
                        editor.apply()
                    }
                    supportFragmentManager.popBackStack()
                }
            } else if (supportFragmentManager.backStackEntryCount == 1) {
                /*
                val animLeftOut: Animation = AnimationUtils.loadAnimation(this, R.anim.left_out)
                binding.*****.startAnimation(animLeftOut)
                animLeftOut.setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) { supportFragmentManager.popBackStack() }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                 */
                refresh()
            } else {
                super.onBackPressed()
                finish()
            }
        }
    }

    private fun setBadgeNoti() {
        val notificationsItem = binding.bottomNavigationView.menu.findItem(R.id.item_noti)
        val dbPatientsPortal = DbPatientsPortal(this)
        // Ejemplo: número de notificaciones sin leer (puedes obtener este valor de tu lógica)
        val notificationsCount = dbPatientsPortal.readAllUnReadedPatientsNotifications(getPatient().idPatient).size

        // Crea el BadgeDrawable para mostrar el número de notificaciones sin leer
        val badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(notificationsItem.itemId)
        badgeDrawable.apply {
            number = notificationsCount
            backgroundColor = ContextCompat.getColor(this@MainActivity, androidx.appcompat.R.color.material_grey_300)
            badgeTextColor = Color.RED
            horizontalOffset = 0 // Desplazamiento horizontal para ajustar el Badge
            verticalOffset = 4 // Desplazamiento vertical para ajustar el Badge
            isVisible = notificationsCount > 0
        }
    }
    private fun setInfoCarousel() {
        // Register lifecycle. For activity this will be lifecycle/getLifecycle() and for fragment it will be viewLifecycleOwner/getViewLifecycleOwner().
        binding.ivCarousel.registerLifecycle(lifecycle)
        binding.ivCarousel.autoPlay = true
        binding.ivCarousel.autoPlayDelay = 4000
        binding.ivCarousel.imageScaleType = ImageView.ScaleType.FIT_XY

        val list = mutableListOf<CarouselItem>()
        list.add(CarouselItem(imageUrl = imageTransportableCenters))
        list.add(CarouselItem(imageUrl = imageNewsAppOnPhone))
        list.add(CarouselItem(imageUrl = imageNewsGynecology))
        list.add(CarouselItem(imageUrl = imageNewsFever))
        list.shuffle()

        binding.ivCarousel.setData(list)
        binding.ivCarousel.carouselListener = object: CarouselListener {
            override fun onClick(position: Int, carouselItem: CarouselItem) {
                val urlImage: String = when(carouselItem.imageUrl) {
                    imageTransportableCenters -> "https://hiba.hospitalitaliano.org.ar/archivos/noticias_archivos/53/archivos/centros(1).png"
                    imageNewsFever -> "https://hiba.hospitalitaliano.org.ar/archivos/noticias_archivos/53/archivos/Flyer%20Guardia%20Pediatr%C3%ADa.png"
                    imageNewsGynecology -> "https://hiba.hospitalitaliano.org.ar/archivos/noticias_archivos/53/archivos/Flyer%20Web_Controles%20Ginecol%C3%B3gicos.png"
                    imageNewsAppOnPhone -> "https://hiba.hospitalitaliano.org.ar/archivos/noticias_archivos/53/archivos/1%20(2).png"
                    else -> { "" }
                }
                showNewsDialog(urlImage)
            }
        }
    }

    private fun showNewsDialog(urlImage: String) {
        val binding = CustomAlertDialogNewsBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this, R.style.CustomDialogStyle)
            .setView(binding.root)
            .create()
        Picasso.get().load(urlImage).into(binding.ivMain)
        binding.btnClose.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun allVisible() {
        binding.bottomNavigationView.menu.findItem(R.id.item_bottom_home).isChecked = true
        binding.bottomNavigationView.menu.findItem(R.id.item_bottom_home).isCheckable = true
        binding.toolbarHome.visibility = View.VISIBLE
        binding.tvToolbarWelcomeUser.visibility = View.VISIBLE
        binding.ivCarousel.visibility = View.VISIBLE
        binding.llCardsViews.visibility = View.VISIBLE
    }

    private fun allGone(tag: String) {
        binding.tvToolbarWelcomeUser.visibility = View.GONE
        binding.ivCarousel.visibility = View.GONE
        binding.pbCardsViews.visibility = View.GONE
        binding.llCardsViews.visibility = View.GONE
        if (tag != "Profile Fragment" && tag != getString(R.string.notificationfragment_tag)) {
            binding.bottomNavigationView.menu.findItem(R.id.item_bottom_home).isChecked = false
            binding.bottomNavigationView.menu.findItem(R.id.item_bottom_home).isCheckable = false
        } else {
            if (tag != getString(R.string.notificationfragment_tag)) {
                binding.toolbarHome.visibility = View.GONE
            }
        }
    }

    private fun getPatient(): Patient {
        lateinit var patient: Patient
        val prefs: SharedPreferences = getSharedPreferences("MY PREF", MODE_PRIVATE)
        val dni = prefs.getString("dni", null)
        if (dni != null) { arrayPatients.forEach { if (dni == it.documentNumber) { patient = it } } }
        return patient
    }

    override fun refreshBadgeNoti() {
        setBadgeNoti()
    }
}