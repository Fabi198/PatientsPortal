package com.example.patientsportal.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.patientsportal.entities.dbEntities.Address
import com.example.patientsportal.entities.dbEntities.AlternativeDrug
import com.example.patientsportal.entities.dbEntities.Appointment
import com.example.patientsportal.entities.dbEntities.ContactPerson
import com.example.patientsportal.entities.dbEntities.Coverage
import com.example.patientsportal.entities.dbEntities.CreditCard
import com.example.patientsportal.entities.dbEntities.Doctor
import com.example.patientsportal.entities.dbEntities.DoctorSpeciality
import com.example.patientsportal.entities.dbEntities.Drug
import com.example.patientsportal.entities.dbEntities.HCArterialPressure
import com.example.patientsportal.entities.dbEntities.HCBreathFrequency
import com.example.patientsportal.entities.dbEntities.HCBreathlessness
import com.example.patientsportal.entities.dbEntities.HCGlucose
import com.example.patientsportal.entities.dbEntities.HCHeartFrequency
import com.example.patientsportal.entities.dbEntities.HCOxygenSaturation
import com.example.patientsportal.entities.dbEntities.HCTemperature
import com.example.patientsportal.entities.dbEntities.HCWeightAndHeight
import com.example.patientsportal.entities.dbEntities.MedicalTest
import com.example.patientsportal.entities.dbEntities.Notification
import com.example.patientsportal.entities.dbEntities.NotificationsSettings
import com.example.patientsportal.entities.dbEntities.Patient
import com.example.patientsportal.entities.dbEntities.PatientCoveragePlan
import com.example.patientsportal.entities.dbEntities.PatientPregnancy
import com.example.patientsportal.entities.dbEntities.PatientTest
import com.example.patientsportal.entities.dbEntities.PhonesPatient
import com.example.patientsportal.entities.dbEntities.Place
import com.example.patientsportal.entities.dbEntities.PlanCoverage
import com.example.patientsportal.entities.dbEntities.PregnancyContraction
import com.example.patientsportal.entities.dbEntities.PregnancyContractions
import com.example.patientsportal.entities.dbEntities.PregnancyFeel
import com.example.patientsportal.entities.dbEntities.PregnancyNote
import com.example.patientsportal.entities.dbEntities.PregnancyPhoto
import com.example.patientsportal.entities.dbEntities.Prescription
import com.example.patientsportal.entities.dbEntities.Province
import com.example.patientsportal.entities.dbEntities.RequestedPatientTest
import com.example.patientsportal.entities.dbEntities.Speciality
import com.example.patientsportal.objects.DateConverter.calculateFinishPregnancyDate
import com.example.patientsportal.objects.DateConverter.dateConverter
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DbPatientsPortal(context: Context) : DbPatientsPortalHelper(context) {

    private fun setDatabase(): SQLiteDatabase { val db = DbPatientsPortalHelper(context); return db.writableDatabase }



    // Address CRUD
    /*
    idPatient INT
    street TEXT
    number TEXT
    floor TEXT
    department TEXT
    observations TEXT
    postalCode TEXT
    country TEXT
    province TEXT
    locality TEXT
     */

    fun editAddress(address: Address): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE address SET street = '${address.street}', number = '${address.number}', floor = '${address.floor}', department = '${address.department}', observations = '${address.observations}', postalCode = '${address.postalCode}', province = '${address.province}', locality = '${address.locality}' WHERE idPatient = '${address.patient.idPatient}'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readAddressFromPatient(idPatient: Int): Address {
        val address = Address()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM address WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            address.patient = readPatient(cursor.getInt(0))
            address.street = cursor.getString(1)
            address.number = cursor.getString(2)
            address.floor = cursor.getString(3)
            address.department = cursor.getString(4)
            address.observations = cursor.getString(5)
            address.postalCode = cursor.getString(6)
            address.country = cursor.getString(7)
            address.province = cursor.getString(8)
            address.locality = cursor.getString(9)
        }
        cursor.close()
        setDatabase().close()
        return address
    }


    // AlternativeDrug
    /*
    idAlternativeDrug INT
    idDrug INT
    name TEXT
    price DOUBLE
    stockAvailable INT
    maximumUnitsToBuy INT
     */

    fun readAlternativesDrugs(idDrug: Int): ArrayList<AlternativeDrug> {
        setDatabase()
        val alternativeDrugsList = ArrayList<AlternativeDrug>()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idAlternativeDrug FROM alternativeDrugs WHERE idDrug = '$idDrug'", null)
        if (cursor.moveToFirst()) {
            do {
                alternativeDrugsList.add(readAlternativeDrug(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return alternativeDrugsList
    }

    fun readAlternativeDrug(idAlternativeDrug: Int): AlternativeDrug {
        setDatabase()
        val alternativeDrug = AlternativeDrug()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM alternativeDrugs WHERE idAlternativeDrug = '$idAlternativeDrug'", null)
        if (cursor.moveToFirst()) {
            alternativeDrug.idAlternativeDrug = cursor.getInt(0)
            alternativeDrug.drug = readDrug(cursor.getInt(1))
            alternativeDrug.name = cursor.getString(2)
            alternativeDrug.price = cursor.getDouble(3)
            alternativeDrug.stockAvailable = cursor.getInt(4)
            alternativeDrug.maximumUnitsToBuy = cursor.getInt(5)
        }
        cursor.close()
        setDatabase().close()
        return alternativeDrug
    }

    // Appointments CRUD
    /*
    idAppointment INT
    idPatientCoveragePlan INT
    idPatient INT
    idDoctorSpeciality INT
    idPlace INT
    date TEXT
     */

    fun assignAppointmentToPatient(appointment: Appointment, idPCP: Int, idPatient: Int): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE appointments " +
                        "SET idPatientCoveragePlan = $idPCP, " +
                        "idPatient = $idPatient " +
                        "WHERE idAppointment = ${appointment.idAppointment}"
            )
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun createAppointment(idDoctorSpeciality: Int, idPlace: Int, date: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idDoctorSpeciality", idDoctorSpeciality)
            v.put("idPlace", idPlace)
            v.put("date", date)
            v.put("showUpPatient", false)
            id = setDatabase().insert("appointments", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    fun readPatientAppointmentsAhead(idPatient: Int): ArrayList<Appointment> {
        val datePattern = "yyyy/MM/dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val startDateSrt = dateFormat.format(calendar.timeInMillis)

        setDatabase()
        val listAppointments = ArrayList<Appointment>()

        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM appointments WHERE date > '$startDateSrt' AND idPatient = '$idPatient'", null)

        if (cursor.moveToFirst()) {
            do {
                val appointment = Appointment()
                appointment.idAppointment = cursor.getInt(0)
                appointment.patientCoveragePlan = PatientCoveragePlan()
                appointment.patient = Patient()
                appointment.doctorSpeciality = readDoctorSpeciality(cursor.getInt(3))
                appointment.place = readPlace(cursor.getInt(4))
                appointment.date = cursor.getString(5)
                appointment.showUpPatient = cursor.getString(6)
                listAppointments.add(appointment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        val sortedListAppointments = listAppointments.sortedBy { it.date }
        return ArrayList(sortedListAppointments)
    }

    fun readPatientHistoryAppointments(idPatient: Int, minDate: Calendar? = null, maxDate: Calendar? = null): ArrayList<Appointment> {
        val datePattern = "yyyy/MM/dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        val startDateSrt = minDate?.let {
            it.set(Calendar.HOUR_OF_DAY, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
            dateFormat.format(it.time)
        }

        val endDateStr = if (maxDate != null) {
            dateFormat.format(maxDate.time)
        } else {
            dateFormat.format(calendar.time)
        }

        setDatabase()
        val listAppointments = ArrayList<Appointment>()

        val query = if (minDate != null) {
            "SELECT * FROM appointments WHERE date BETWEEN '$startDateSrt' AND '$endDateStr' AND idPatient = '$idPatient'"
        } else {
            "SELECT * FROM appointments WHERE date < '$endDateStr' AND idPatient = '$idPatient'"
        }

        val cursor: Cursor = setDatabase().rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val appointment = Appointment()
                appointment.idAppointment = cursor.getInt(0)
                appointment.patientCoveragePlan = PatientCoveragePlan()
                appointment.patient = Patient()
                appointment.doctorSpeciality = readDoctorSpeciality(cursor.getInt(3))
                appointment.place = readPlace(cursor.getInt(4))
                appointment.date = cursor.getString(5)
                appointment.showUpPatient = cursor.getString(6)
                listAppointments.add(appointment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        val sortedListAppointments = listAppointments.sortedByDescending { it.date }
        return ArrayList(sortedListAppointments)
    }

    fun readPatientLessAppointments(idDoctorSpecialities: IntArray?, idPlace: IntArray?, minDays: Int, maxDays: Int): ArrayList<Appointment> {
        val datePattern = "yyyy/MM/dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())

        val calendarStart = Calendar.getInstance()
        calendarStart.add(Calendar.DAY_OF_MONTH, minDays)
        val startDate = calendarStart.time
        val startDateStr = dateFormat.format(startDate)

        val calendarEnd = Calendar.getInstance()
        calendarEnd.add(Calendar.DAY_OF_MONTH, maxDays)
        val endDate = calendarEnd.time
        val endDateStr = dateFormat.format(endDate)

        setDatabase()
        val listAppointments = ArrayList<Appointment>()

        val inClauseDoctorSpeciality = idDoctorSpecialities?.joinToString(", ") { it.toString() }
        val inClausePlace = idPlace?.joinToString(", ") { it.toString() }

        val query =
            "SELECT * FROM appointments WHERE date BETWEEN '$startDateStr' AND '$endDateStr' AND idDoctorSpeciality IN ($inClauseDoctorSpeciality) AND idPlace IN ($inClausePlace) AND idPatient IS NULL"

        val cursor: Cursor = setDatabase().rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val appointment = Appointment()
                appointment.idAppointment = cursor.getInt(0)
                appointment.patientCoveragePlan = PatientCoveragePlan()
                appointment.patient = Patient()
                appointment.doctorSpeciality = readDoctorSpeciality(cursor.getInt(3))
                appointment.place = readPlace(cursor.getInt(4))
                appointment.date = cursor.getString(5)
                appointment.showUpPatient = cursor.getString(6)
                listAppointments.add(appointment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        val sortedListAppointments = listAppointments.sortedBy { it.date }
        return ArrayList(sortedListAppointments)
    }

    fun updatePassedAppointments(idPatient: Int): Boolean {
        val datePattern = "yyyy/MM/dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())

        val calendarStart = Calendar.getInstance()
        val endDate = calendarStart.time
        val endDateStr = dateFormat.format(endDate)

        setDatabase()

        return try {
            setDatabase().execSQL("UPDATE appointments SET showUpPatient = CASE WHEN RANDOM() * 4 < 1 THEN 'Ausente' ELSE 'Atendido' END WHERE date < '$endDateStr' AND idPatient = $idPatient AND showUpPatient = 'A presentarse'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun updateReleaseAppointment(appointment: Appointment): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE appointments " +
                        "SET idPatientCoveragePlan = NULL, " +
                        "idPatient = NULL, " +
                        "showUpPatient = 'A presentarse' " +
                        "WHERE idAppointment = ${appointment.idAppointment}"
            )
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }



    // BillAddress CRUD
    /*
    idPatient INT
    street TEXT
    number TEXT
    floor TEXT
    department TEXT
    observations TEXT
    postalCode TEXT
    country TEXT
    province TEXT
    locality TEXT
     */

    fun editBillAddress(billAddress: Address): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE billAddress SET street = '${billAddress.street}', number = '${billAddress.number}', floor = '${billAddress.floor}', department = '${billAddress.department}', observations = '${billAddress.observations}', postalCode = '${billAddress.postalCode}', province = '${billAddress.province}', locality = '${billAddress.locality}' WHERE idPatient = '${billAddress.patient.idPatient}'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readBillAddressFromPatient(idPatient: Int): Address {
        val address = Address()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM billAddress WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            address.patient = readPatient(cursor.getInt(0))
            address.street = cursor.getString(1)
            address.number = cursor.getString(2)
            address.floor = cursor.getString(3)
            address.department = cursor.getString(4)
            address.observations = cursor.getString(5)
            address.postalCode = cursor.getString(6)
            address.country = cursor.getString(7)
            address.province = cursor.getString(8)
            address.locality = cursor.getString(9)
        }
        cursor.close()
        setDatabase().close()
        return address
    }



    // City CRUD
    /*
    id INT
    city_name TEXT
    cp INT
    id_province INT
     */

    fun readCitysForSpinner(postalCode: String, provinceName: String): ArrayList<String> {
        setDatabase()
        val idProvince = readProvinceByName(provinceName)
        val list = ArrayList<String>()
        val cursor: Cursor = setDatabase().rawQuery("SELECT city_name FROM city WHERE cp = '${Integer.parseInt(postalCode)}' AND id_province = '${idProvince.id}'", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return list
    }



    // ContactPersons CRUD
    /*
    idPatient INT
    names TEXT
    lastNames TEXT
    relationship TEXT
    address TEXT
    phone TEXT
     */

    fun editContactPerson(contactPerson: ContactPerson): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE contactPersons SET names = '${contactPerson.names}', lastNames = '${contactPerson.lastNames}', relationship = '${contactPerson.relationship}', address = '${contactPerson.address}', phone = '${contactPerson.phone}' WHERE idPatient = '${contactPerson.patient.idPatient}'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readContactPersonFromPatient(idPatient: Int): ContactPerson {
        val contactPerson = ContactPerson()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM contactPersons WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            contactPerson.patient = readPatient(cursor.getInt(0))
            contactPerson.names = cursor.getString(1)
            contactPerson.lastNames = cursor.getString(2)
            contactPerson.relationship = cursor.getString(3)
            contactPerson.address = cursor.getString(4)
            contactPerson.phone = cursor.getString(5)
        }
        cursor.close()
        setDatabase().close()
        return contactPerson
    }



    // Coverages CRUD
    /*
    idCoverage INT
    name TEXT
     */

    fun readAllCoverageNames(): ArrayList<String> {
        setDatabase()
        val arrayCoverageNames = ArrayList<String>()
        val cursorCoverageNames: Cursor = setDatabase().rawQuery("SELECT name FROM coverages", null)
        if (cursorCoverageNames.moveToFirst()) {
            do {
                arrayCoverageNames.add(cursorCoverageNames.getString(0))
            } while (cursorCoverageNames.moveToNext())
        }
        setDatabase().close()
        cursorCoverageNames.close()
        return arrayCoverageNames
    }

    private fun readCoverageByName(name: String): Coverage {
        setDatabase()
        val coverage = Coverage()
        val cursorCoverage: Cursor = setDatabase().rawQuery("SELECT * FROM coverages WHERE name = '$name'", null)
        if (cursorCoverage.moveToFirst()) {
            coverage.idCoverage = cursorCoverage.getInt(0)
            coverage.name = cursorCoverage.getString(1)
        }
        setDatabase().close()
        cursorCoverage.close()
        return coverage
    }

    private fun readCoverage(idCoverage: Int): Coverage {
        setDatabase()
        val coverage = Coverage()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM coverages WHERE idCoverage = $idCoverage", null)
        if (cursor.moveToFirst()) {
            coverage.idCoverage = cursor.getInt(0)
            coverage.name = cursor.getString(1)
        }
        setDatabase().close()
        cursor.close()
        return coverage
    }



    // Doctors CRUD
    /*
    idDoctor INT
    name TEXT
    lastName TEXT
    dni TEXT
    gender TEXT
    email TEXT
    birthday TEXT
    codarea TEXT
    numCelular TEXT
    urlImage TEXT
     */

    private fun readDoctor(idDoctor: Int): Doctor {
        setDatabase()
        val doctor = Doctor()
        val cursorDoctor: Cursor = setDatabase().rawQuery("SELECT * FROM doctors WHERE idDoctor = $idDoctor", null)
        if (cursorDoctor.moveToFirst()) {
            doctor.idDoctor = cursorDoctor.getInt(0)
            doctor.name = cursorDoctor.getString(1)
            doctor.lastName = cursorDoctor.getString(2)
            doctor.dni = cursorDoctor.getString(3)
            doctor.gender = cursorDoctor.getString(4)
            doctor.email = cursorDoctor.getString(5)
            doctor.birthday = cursorDoctor.getString(6)
            doctor.codArea = cursorDoctor.getString(7)
            doctor.numCelular = cursorDoctor.getString(8)
            doctor.intImage = cursorDoctor.getInt(9)
        }
        setDatabase().close()
        cursorDoctor.close()
        return doctor
    }



    // DoctorPatients CRUD
    /*
    idDoctorSpeciality INT
    idPatient INT
     */

    fun readAllDoctorsPatient(idPatient: Int): ArrayList<DoctorSpeciality> {
        val list = ArrayList<DoctorSpeciality>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idDoctorSpeciality FROM doctorPatients WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(readDoctorSpeciality(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        setDatabase().close()
        cursor.close()
        return list
    }

    fun readDoctorsBySpeciality(idPatient: Int, searchLetter: String?): Map<String, ArrayList<Doctor>> {
        val doctorsBySpeciality = mutableMapOf<String, ArrayList<Doctor>>()

        val query = """
        SELECT s.name AS specialityName, d.idDoctor
        FROM patients p
        INNER JOIN doctorPatients dp ON p.idPatient = dp.idPatient
        INNER JOIN doctorSpecialities ds ON dp.idDoctorSpeciality = ds.idDoctorSpeciality
        INNER JOIN doctors d ON ds.idDoctor = d.idDoctor
        INNER JOIN specialities s ON ds.idSpeciality = s.idSpeciality
        WHERE p.idPatient = ? ${if (!searchLetter.isNullOrBlank()) "AND (LOWER(d.name) LIKE LOWER(?) OR LOWER(d.lastName) LIKE LOWER(?))" else ""}
        ORDER BY specialityName
        """.trimIndent()

        val searchArgs = if (!searchLetter.isNullOrBlank()) {
            arrayOf(idPatient.toString(), "%$searchLetter%", "%$searchLetter%")
        } else {
            arrayOf(idPatient.toString())
        }

        val cursor = setDatabase().rawQuery(query, searchArgs)

        while (cursor.moveToNext()) {
            val specialityName = cursor.getString(0)
            val idDoctor = cursor.getInt(1)

            val doctor = readDoctor(idDoctor)

            if (doctorsBySpeciality.containsKey(specialityName)) {
                doctorsBySpeciality[specialityName]?.add(doctor)
            } else {
                doctorsBySpeciality[specialityName] = mutableListOf(doctor) as ArrayList<Doctor>
            }
        }
        cursor.close()
        setDatabase().close()
        return doctorsBySpeciality
    }



    // DoctorPlaces CRUD
    /*
    idDoctorPlace INT
    idDoctorSpeciality INT
    idPlace INT
     */

    fun readPlacesBySpeciality(idSpeciality: Int): ArrayList<Place> {
        val listPlaces = ArrayList<Place>()

        val addedItems = HashSet<String>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery(
            "SELECT idPlace FROM doctorPlaces dP INNER JOIN doctorSpecialities dS ON dP.idDoctorSpeciality = dS.idDoctorSpeciality WHERE dS.idSpeciality = '$idSpeciality'",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val place = readPlace(cursor.getInt(0))
                if (!addedItems.contains(place.name)) {
                    listPlaces.add(readPlace(cursor.getInt(0)))
                    addedItems.add(place.name)
                }
            } while (cursor.moveToNext())
        }
        setDatabase().close()
        cursor.close()
        return listPlaces
    }

    fun readPlacesByDoctor(idDoctorSpeciality: Int): ArrayList<Place> {
        val listPlaces = ArrayList<Place>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPlace FROM doctorPlaces WHERE idDoctorSpeciality = '$idDoctorSpeciality'", null)
        if (cursor.moveToFirst()) {
            do {
                listPlaces.add(readPlace(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        setDatabase().close()
        cursor.close()
        return listPlaces
    }



    // DoctorSpecialities CRUD
    /*
    idDoctorSpeciality INT
    idDoctor INT
    idSpeciality INT
     */

    fun readAllIDDoctorSpecialityBySpecialityAndPlace(idSpeciality: Int, idPlace: IntArray): IntArray {
        val listIDs = ArrayList<Int>()
        setDatabase()
        val inClausePlace = idPlace.joinToString(", ") { "'$it'" }
        val cursor: Cursor = setDatabase().rawQuery(
            "SELECT ds.idDoctorSpeciality FROM doctorSpecialities ds INNER JOIN doctorPlaces dp ON ds.idDoctorSpeciality = dp.idDoctorSpeciality WHERE ds.idSpeciality = '$idSpeciality' AND dp.idPlace IN ($inClausePlace)",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                listIDs.add(cursor.getInt(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listIDs.toIntArray()
    }

    private fun readDoctorSpeciality(idDoctorSpeciality: Int): DoctorSpeciality {
        val doctorSpeciality = DoctorSpeciality()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM doctorSpecialities WHERE idDoctorSpeciality = $idDoctorSpeciality", null)
        if (cursor.moveToFirst()) {
            doctorSpeciality.idDoctorSpeciality = cursor.getInt(0)
            doctorSpeciality.doctor = readDoctor(cursor.getInt(1))
            doctorSpeciality.speciality = readSpeciality(cursor.getInt(2))
        }
        setDatabase().close()
        cursor.close()
        return doctorSpeciality
    }



    // Drugs CRUD
    /*
    idDrug INT
    drugNameSubstance TEXT
    expiredDate TEXT
    drugNameProduct TEXT
    dosage TEXT
    price DOUBLE
    stockAvailable INT
    maximumUnitsToBuy INT
     */

    fun readDrug(idDrug: Int): Drug {
        val drug = Drug()
        setDatabase()
        val cursorDrug: Cursor = setDatabase().rawQuery("SELECT * FROM drugs WHERE idDrug = $idDrug", null)
        if (cursorDrug.moveToFirst()) {
            drug.idDrug = cursorDrug.getInt(0)
            drug.nameDrugSubstance = cursorDrug.getString(1)
            drug.expiredDate = cursorDrug.getString(2)
            drug.nameDrugProduct = cursorDrug.getString(3)
            drug.dosage = cursorDrug.getString(4)
            drug.price = cursorDrug.getDouble(5)
            drug.stockAvailable = cursorDrug.getInt(6)
            drug.maximumUnitsToBuy = cursorDrug.getInt(7)
        }
        setDatabase().close()
        cursorDrug.close()
        return drug
    }



    // HealthControls CRUD
    /*
    idHC INT
    idPatient INT
    date TEXT
        (HCWeightAndHeight)
            weight TEXT
            height TEXT
            imc TEXT
        (HCOxygenSaturation / HCGlucose)
            oxygenSaturation / glucose TEXT
            hasSupplementaryOxygen / eatLastTwoHours TEXT
        (else)
            temperature / heartFrequency / arterialPressure / breathFrequency / breathlessness
    loadedBy TEXT
     */

    fun createHealthControlForPatient(hc: Any, title: String): Long {
        setDatabase()
        var id: Long = 0
        when (title) {
            "WeightAndHeight" -> {
                if (hc is HCWeightAndHeight) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("weight", hc.weight)
                        v.put("height", hc.height)
                        v.put("imc", hc.imc)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcWeightAndHeight", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "Temperature" -> {
                if (hc is HCTemperature) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("temperature", hc.temperature)
                        v.put("bodyPart", hc.bodyPart)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcTemperature", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "HeartFrequency" -> {
                if (hc is HCHeartFrequency) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("heartFrequency", hc.heartFrequency)
                        v.put("method", hc.method)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcHeartFrequency", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "ArterialPressure" -> {
                if (hc is HCArterialPressure) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("lowArterialPressure", hc.lowArterialPressure)
                        v.put("highArterialPressure", hc.highArterialPressure)
                        v.put("bodyPart", hc.bodyPart)
                        v.put("bloodPressureMonitorType", hc.bloodPressureMonitorType)
                        v.put("personWhoTookTheTest", hc.personWhoTookTheTest)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcArterialPressure", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "Glucose" -> {
                if (hc is HCGlucose) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("glucose", hc.glucose)
                        v.put("eatLastTwoHours", hc.eatLastTwoHours)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcGlucose", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "BreathFrequency" -> {
                if (hc is HCBreathFrequency) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("breathFrequency", hc.breathFrequency)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcBreathFrequency", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "OxygenSaturation" -> {
                if (hc is HCOxygenSaturation) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("oxygenSaturation", hc.oxygenSaturation)
                        v.put("hasSupplementaryOxygen", hc.hasSupplementaryOxygen)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcOxygenSaturation", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            "Breathlessness" -> {
                if (hc is HCBreathlessness) {
                    try {
                        val v = ContentValues()
                        v.put("idPatient", hc.patient.idPatient.toString())
                        v.put("date", hc.date)
                        v.put("breathlessness", hc.breathlessness)
                        v.put("loadedBy", hc.loadedBy)
                        id = setDatabase().insert("hcBreathlessness", null, v)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    } finally {
                        setDatabase().close()
                    }
                }
            }
            else -> {}
        }
        return id
    }

    fun readAllHealthControlsRegistersByHC(patient: Patient, title: String): ArrayList<out Any> {
        setDatabase()
        lateinit var registers: ArrayList<out Any>
        when (title) {
            "WeightAndHeight" -> {
                registers = ArrayList<HCWeightAndHeight>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcWeightAndHeight WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCWeightAndHeight()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.weight = cursor.getString(3)
                        register.height = cursor.getString(4)
                        register.imc = cursor.getString(5)
                        register.loadedBy = cursor.getString(6)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "Temperature" -> {
                registers = ArrayList<HCTemperature>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcTemperature WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCTemperature()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.temperature = cursor.getString(3)
                        register.bodyPart = cursor.getString(4)
                        register.loadedBy = cursor.getString(5)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "HeartFrequency" -> {
                registers = ArrayList<HCHeartFrequency>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcHeartFrequency WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCHeartFrequency()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.heartFrequency = cursor.getString(3)
                        register.method = cursor.getString(4)
                        register.loadedBy = cursor.getString(5)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "ArterialPressure" -> {
                registers = ArrayList<HCArterialPressure>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcArterialPressure WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCArterialPressure()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.lowArterialPressure = cursor.getString(3)
                        register.highArterialPressure = cursor.getString(4)
                        register.bodyPart = cursor.getString(5)
                        register.bloodPressureMonitorType = cursor.getString(6)
                        register.personWhoTookTheTest = cursor.getString(7)
                        register.loadedBy = cursor.getString(8)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "Glucose" -> {
                registers = ArrayList<HCGlucose>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcGlucose WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCGlucose()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.glucose = cursor.getString(3)
                        register.eatLastTwoHours = cursor.getString(4)
                        register.loadedBy = cursor.getString(5)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "BreathFrequency" -> {
                registers = ArrayList<HCBreathFrequency>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcBreathFrequency WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCBreathFrequency()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.breathFrequency = cursor.getString(3)
                        register.loadedBy = cursor.getString(4)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "OxygenSaturation" -> {
                registers = ArrayList<HCOxygenSaturation>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcOxygenSaturation WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCOxygenSaturation()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.oxygenSaturation = cursor.getString(3)
                        register.hasSupplementaryOxygen = cursor.getString(4)
                        register.loadedBy = cursor.getString(5)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            "Breathlessness" -> {
                registers = ArrayList<HCBreathlessness>()
                val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM hcBreathlessness WHERE idPatient = '${patient.idPatient}'", null)
                if (cursor.moveToFirst()) {
                    do {
                        val register = HCBreathlessness()
                        register.idHC = cursor.getInt(0)
                        register.patient = readPatient(cursor.getInt(1))
                        register.date = cursor.getString(2)
                        register.breathlessness = cursor.getString(3)
                        register.loadedBy = cursor.getString(4)
                        registers.add(register)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            else -> {}
        }
        setDatabase().close()
        return registers
    }



    // MedicalTest CRUD
    /*
    idMedicalTest INT
    name TEXT
    preparation TEXT
     */

    private fun readMedicalTest(idMedicalTest: Int): MedicalTest {
        val medicalTest = MedicalTest()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM medicalTests WHERE idMedicalTest = $idMedicalTest", null)
        if (cursor.moveToFirst()) {
            medicalTest.idMedicalTest = cursor.getInt(0)
            medicalTest.name = cursor.getString(1)
            medicalTest.preparation = cursor.getString(2)
        }
        setDatabase().close()
        cursor.close()
        return medicalTest
    }

    fun readMedicalTestPreparations(searchLetter: String?): ArrayList<MedicalTest> {
        val listMedicalTests = ArrayList<MedicalTest>()
        setDatabase()
        val cursor: Cursor = if (!searchLetter.isNullOrBlank()) {
            val selectionArgs = arrayOf("%$searchLetter%")
            val query = "SELECT * FROM medicalTests WHERE LOWER(name) LIKE LOWER(?)"
            setDatabase().rawQuery(query, selectionArgs)
        } else {
            setDatabase().rawQuery("SELECT * FROM medicalTests", null)
        }
        if (cursor.moveToFirst()) {
            do {
                val medicalTest = MedicalTest()
                medicalTest.idMedicalTest = cursor.getInt(0)
                medicalTest.name = cursor.getString(1)
                medicalTest.preparation = cursor.getString(2)
                listMedicalTests.add(medicalTest)
            } while (cursor.moveToNext())
        }
        setDatabase().close()
        cursor.close()
        return listMedicalTests
    }



    // Notifications CRUD
    /*
    idNotification INT
    idPatient INT
    date TEXT
    title TEXT
    description TEXT
    readed TEXT
     */

    fun readPatientNotification(idNotification: Int): Notification {
        setDatabase()
        val notification = Notification()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM notifications WHERE idNotification = '$idNotification'", null)
        if (cursor.moveToFirst()) {
            notification.idNotification = cursor.getInt(0)
            notification.patient = readPatient(cursor.getInt(1))
            notification.date = cursor.getString(2)
            notification.title = cursor.getString(3)
            notification.description = cursor.getString(4)
            notification.readed = cursor.getString(5)
        }
        cursor.close()
        setDatabase().close()
        return notification
    }

    fun createPatientNotification(notification: Notification): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatient", notification.patient.idPatient.toString())
            v.put("date", notification.date)
            v.put("title", notification.title)
            v.put("description", notification.description)
            v.put("readed", notification.readed)
            id = setDatabase().insert("notifications", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    fun updatePatientNotification(idNotification: Int): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE notifications SET readed = 'Si' WHERE idNotification = '$idNotification'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readAllUnReadedPatientsNotifications(idPatient: Int): ArrayList<Notification> {
        val notifications = ArrayList<Notification>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idNotification FROM notifications WHERE idPatient = '$idPatient' AND readed = 'No' ORDER BY idNotification DESC", null)
        if (cursor.moveToFirst()) {
            do {
                notifications.add(readPatientNotification(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return notifications
    }



    // NotificationsSettings CRUD
    /*
    idNotificationSettings INT
    idPatient INT
    medicalTestEnabled TEXT
    medicalTestPush TEXT
    medicalTestEmail TEXT
    medicalTestReminderOne INT
    medicalTestReminderTwo INT
    medicalTestReminderThree INT
    medicalTestReminderFour INT
    medicalTestReminderFive INT
    drugsEnabled TEXT
    drugsPush TEXT
    drugsEmail TEXT
    drugsReminderOne INT
    drugsReminderTwo INT
    drugsReminderThree INT
    drugsReminderFour INT
    drugsReminderFive INT
    derivationsEnabled TEXT
    derivationsPush TEXT
    derivationsEmail TEXT
    patientsPortalEnabled TEXT
    patientsPortalPush TEXT
    patientsPortalEmail TEXT
     */

    fun updatePatientNotificationSettings(idNotiSettings: Int, switcherActivated: String, b: Boolean, days: Int): Boolean {
        setDatabase()
        val query = when (switcherActivated) {
            "medicalTestEnabled" -> { "medicalTestEnabled = ${if (b) {"'$b'"} else {"'$b', medicalTestPush = '$b', medicalTestEmail = '$b'"} }" }
            "medicalTestPush" -> { "medicalTestPush = '$b'" }
            "medicalTestEmail" -> { "medicalTestEmail = '$b'" }
            "medicalTestReminderOne" -> { "medicalTestReminderOne = ${if (b) {"'$days'"} else {"NULL"}}" }
            "medicalTestReminderTwo" -> { "medicalTestReminderTwo = ${if (b) {"'$days'"} else {"NULL"}}" }
            "medicalTestReminderThree" -> { "medicalTestReminderThree = ${if (b) {"'$days'"} else {"NULL"}}" }
            "medicalTestReminderFour" -> { "medicalTestReminderFour = ${if (b) {"'$days'"} else {"NULL"}}" }
            "medicalTestReminderFive" -> { "medicalTestReminderFive = ${if (b) {"'$days'"} else {"NULL"}}" }
            "drugsEnabled" -> { "drugsEnabled = ${if (b) {"'$b'"} else {"'$b', drugsPush = '$b', drugsEmail = '$b'"} }" }
            "drugsPush" -> { "drugsPush = '$b'" }
            "drugsEmail" -> { "drugsEmail = '$b'" }
            "drugsReminderOne" -> { "drugsReminderOne = ${if (b) {"'$days'"} else {"NULL"}}" }
            "drugsReminderTwo" -> { "drugsReminderTwo = ${if (b) {"'$days'"} else {"NULL"}}" }
            "drugsReminderThree" -> { "drugsReminderThree = ${if (b) {"'$days'"} else {"NULL"}}" }
            "drugsReminderFour" -> { "drugsReminderFour = ${if (b) {"'$days'"} else {"NULL"}}" }
            "drugsReminderFive" -> { "drugsReminderFive = ${if (b) {"'$days'"} else {"NULL"}}" }
            "derivationsEnabled" -> { "derivationsEnabled = ${if (b) {"'$b'"} else {"'$b', derivationsPush = '$b', derivationsEmail = '$b'"} }" }
            "derivationsPush" -> { "derivationsPush = '$b'" }
            "derivationsEmail" -> { "derivationsEmail = '$b'" }
            "patientsPortalEnabled" -> { "patientsPortalEnabled = ${if (b) {"'$b'"} else {"'$b', patientsPortalPush = '$b', patientsPortalEmail = '$b'"} }" }
            "patientsPortalPush" -> { "patientsPortalPush = '$b'" }
            "patientsPortalEmail" -> { "patientsPortalEmail = '$b'" }
            else -> {""}
        }
        return try {
            setDatabase().execSQL(
                "UPDATE notificationsSettings SET $query WHERE idNotificationSettings = '$idNotiSettings'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readPatientNotificationSettings(idPatient: Int): NotificationsSettings {
        setDatabase()
        val notiSet = NotificationsSettings()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM notificationsSettings WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            notiSet.idNotificationSettings = cursor.getInt(0)
            notiSet.patient = readPatient(cursor.getInt(1))
            notiSet.medicalTestEnabled = cursor.getString(2)
            notiSet.medicalTestPush = cursor.getString(3)
            notiSet.medicalTestEmail = cursor.getString(4)
            notiSet.medicalTestReminderOne = cursor.getInt(5)
            notiSet.medicalTestReminderTwo = cursor.getInt(6)
            notiSet.medicalTestReminderThree = cursor.getInt(7)
            notiSet.medicalTestReminderFour = cursor.getInt(8)
            notiSet.medicalTestReminderFive = cursor.getInt(9)
            notiSet.drugsEnabled = cursor.getString(10)
            notiSet.drugsPush = cursor.getString(11)
            notiSet.drugsEmail = cursor.getString(12)
            notiSet.drugsReminderOne = cursor.getInt(13)
            notiSet.drugsReminderTwo = cursor.getInt(14)
            notiSet.drugsReminderThree = cursor.getInt(15)
            notiSet.drugsReminderFour = cursor.getInt(16)
            notiSet.drugsReminderFive = cursor.getInt(17)
            notiSet.derivationsEnabled = cursor.getString(18)
            notiSet.derivationsPush = cursor.getString(19)
            notiSet.derivationsEmail = cursor.getString(20)
            notiSet.patientsPortalEnabled = cursor.getString(21)
            notiSet.patientsPortalPush = cursor.getString(22)
            notiSet.patientsPortalEmail = cursor.getString(23)
        }
        cursor.close()
        setDatabase().close()
        return notiSet
    }



    // Patients CRUD
    /*
    idPatient INT
    name TEXT
    lastName TEXT
    mothersLastName TEXT
    biologicalSex TEXT
    gender TEXT
    email TEXT
    alternativeEmail TEXT
    password TEXT
    birthday TEXT
    documentType TEXT
    documentNumber TEXT
    idClinicalHistory TEXT
    observations TEXT
    intImage INTEGER
    language TEXT
    countryBirth TEXT
    educationLevelReached TEXT
    homeType TEXT
    whoLiveWith TEXT
    religion TEXT
    disability TEXT
     */

    fun editPatient(patient: Patient, tag: String): Boolean {
        setDatabase()
        val query = when (tag) {
            "Photo" -> { "intImage = 0, uriImage = '${patient.uriImage}'" }
            "PersonalData" -> { "name = '${patient.name}', lastName = '${patient.lastName}', mothersLastName = '${patient.mothersLastName}', documentType = '${patient.documentType}', documentNumber = '${patient.documentNumber}', biologicalSex = '${patient.biologicalSex}', gender = '${patient.gender}'" }
            "Nationality" -> { "language = '${patient.language}', countryBirth = '${patient.countryBirth}'" }
            "Mail" -> { "email = '${patient.email}', alternativeEmail = '${patient.alternativeEmail}'" }
            "Education" -> { "educationLevelReached = '${patient.educationLevelReached}'" }
            "SocialAspect" -> { "homeType = '${patient.homeType}', whoLiveWith = '${patient.whoLiveWith}'" }
            "CulturalAspect" -> { "religion = '${patient.religion}'" }
            "Disability" -> { "disability = '${patient.disability}'" }
            else -> { "" }
        }
        return try {
            setDatabase().execSQL(
                "UPDATE patients SET $query WHERE idPatient = '${patient.idPatient}'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readPatient(id: Int): Patient {
        setDatabase()
        val patient = Patient()
        val cursorPatient: Cursor = setDatabase().rawQuery("SELECT * FROM patients WHERE idPatient = $id", null)
        if (cursorPatient.moveToFirst()) {
            with(patient) {
                idPatient = cursorPatient.getInt(0)
                name = cursorPatient.getString(1)
                lastName = cursorPatient.getString(2)
                mothersLastName = cursorPatient.getString(3)
                biologicalSex = cursorPatient.getString(4)
                gender = cursorPatient.getString(5)
                email = cursorPatient.getString(6)
                alternativeEmail = cursorPatient.getString(7)
                password = cursorPatient.getString(8)
                birthday = cursorPatient.getString(9)
                documentType = cursorPatient.getString(10)
                documentNumber = cursorPatient.getString(11)
                idClinicalHistory = cursorPatient.getString(12)
                observations = cursorPatient.getString(13)
                intImage = cursorPatient.getInt(14)
                uriImage = cursorPatient.getString(15)
                language = cursorPatient.getString(16)
                countryBirth = cursorPatient.getString(17)
                educationLevelReached = cursorPatient.getString(18)
                homeType = cursorPatient.getString(19)
                whoLiveWith = cursorPatient.getString(20)
                religion = cursorPatient.getString(21)
                disability = cursorPatient.getString(22)
            }
        }
        setDatabase().close()
        cursorPatient.close()
        return patient
    }

    fun readPatientByDocumentNumber(docNumber: String): Patient {
        setDatabase()
        val patient = Patient()
        val cursorPatient: Cursor = setDatabase().rawQuery("SELECT * FROM patients WHERE documentNumber = $docNumber", null)
        if (cursorPatient.moveToFirst()) {
            with(patient) {
                idPatient = cursorPatient.getInt(0)
                name = cursorPatient.getString(1)
                lastName = cursorPatient.getString(2)
                mothersLastName = cursorPatient.getString(3)
                biologicalSex = cursorPatient.getString(4)
                gender = cursorPatient.getString(5)
                email = cursorPatient.getString(6)
                alternativeEmail = cursorPatient.getString(7)
                password = cursorPatient.getString(8)
                birthday = cursorPatient.getString(9)
                documentType = cursorPatient.getString(10)
                documentNumber = cursorPatient.getString(11)
                idClinicalHistory = cursorPatient.getString(12)
                observations = cursorPatient.getString(13)
                intImage = cursorPatient.getInt(14)
                uriImage = cursorPatient.getString(15)
                language = cursorPatient.getString(16)
                countryBirth = cursorPatient.getString(17)
                educationLevelReached = cursorPatient.getString(18)
                homeType = cursorPatient.getString(19)
                whoLiveWith = cursorPatient.getString(20)
                religion = cursorPatient.getString(21)
                disability = cursorPatient.getString(22)
            }
        }
        setDatabase().close()
        cursorPatient.close()
        return patient
    }



    // PatientCoveragePlans CRUD
    /*
    idPatientCoveragePlan INT
    idPatient INT
    idCoverage INT
    idPlan INT
    affiliateNumber TEXT
     */

    fun editPatientCoveragePlan(pcp: PatientCoveragePlan, idPatient: Int, nameCoverage: String, planType: String, affiliateNumber: String): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE patientCoveragePlans " +
                        "SET idPatient = ${idPatient}, " +
                        "idCoverage = ${readCoverageByName(nameCoverage).idCoverage}, " +
                        "idPlan = ${readPlanByNameAndCoverage(planType, nameCoverage).idPlan}, " +
                        "affiliateNumber = '${affiliateNumber}'" +
                        "WHERE idPatientCoveragePlan = ${pcp.idPCP}"
            )
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun deletePatientCoveragePlan(pcp: PatientCoveragePlan): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL("DELETE FROM patientCoveragePlans WHERE idPatientCoveragePlan = ${pcp.idPCP}")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun createPatientCoveragePlan(idPatient: Int, coverageName: String, planType: String, affiliateNumber: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatient", idPatient)
            v.put("idCoverage", readCoverageByName(coverageName).idCoverage)
            v.put("idPlan", readPlanByNameAndCoverage(planType, coverageName).idPlan)
            v.put("affiliateNumber", affiliateNumber)
            id = setDatabase().insert("patientCoveragePlans", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    private fun readPatientCoveragePlan(idPCP: Int): PatientCoveragePlan {
        setDatabase()
        val pcp = PatientCoveragePlan()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM patientCoveragePlans WHERE idPatientCoveragePlan = $idPCP", null)
        if (cursor.moveToFirst()) {
            pcp.idPCP = cursor.getInt(0)
            pcp.patient = readPatient(cursor.getInt(1))
            pcp.coverage = readCoverage(cursor.getInt(2))
            pcp.planType = readPlanCoverage(cursor.getInt(3))
            pcp.affiliateNumber = cursor.getString(4)
        }
        setDatabase().close()
        cursor.close()
        return pcp
    }

    fun readCoveragesPlansByPatient(idPatient: Int): ArrayList<PatientCoveragePlan> {
        val arrayCoveragesPlans = ArrayList<PatientCoveragePlan>()
        setDatabase()
        val cursorCoverages: Cursor =
            setDatabase().rawQuery("SELECT idPatientCoveragePlan FROM patientCoveragePlans WHERE idPatient = $idPatient", null)
        if (cursorCoverages.moveToFirst()) {
            do {
                arrayCoveragesPlans.add(readPatientCoveragePlan(cursorCoverages.getInt(0)))
            } while (cursorCoverages.moveToNext())
        }
        setDatabase().close()
        cursorCoverages.close()
        return arrayCoveragesPlans
    }

    // PatientCreditCards CRUD
    /*
    idCreditCard INT
    idPatient INT
    cardBrand TEXT
    cardOwner TEXT
    cardNumber TEXT
    cardExpiration TEXT
    cardCVV TEXT
    cardDocNumber TEXT
     */

    fun readAllCreditCardsFromPatient(idPatient: Int): ArrayList<CreditCard> {
        val listCreditCards = ArrayList<CreditCard>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idCreditCard FROM patientCreditCards WHERE idPatient = $idPatient", null)
        if (cursor.moveToFirst()) {
            do {
                listCreditCards.add(readCreditCard(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listCreditCards
    }

    private fun readCreditCard(idCreditCard: Int): CreditCard {
        val creditCard = CreditCard()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM patientCreditCards WHERE idCreditCard = '$idCreditCard'", null)
        if (cursor.moveToFirst()) {
            creditCard.idCreditCard = cursor.getInt(0)
            creditCard.patient = readPatient(cursor.getInt(1))
            creditCard.cardBrand = cursor.getString(2)
            creditCard.cardOwner = cursor.getString(3)
            creditCard.cardNumber = cursor.getString(4)
            creditCard.cardExpiration = cursor.getString(5)
            creditCard.cardCVV = cursor.getString(6)
            creditCard.cardDocNumber = cursor.getString(7)
        }
        cursor.close()
        setDatabase().close()
        return creditCard
    }

    fun createPatientCreditCard(idPatient: Int, cardBrand: String, cardOwner: String, cardNumber: String, cardExpiration: String, cardCVV: String, cardDocNumber: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatient", idPatient)
            v.put("cardBrand", cardBrand)
            v.put("cardOwner", cardOwner)
            v.put("cardNumber", cardNumber)
            v.put("cardExpiration", cardExpiration)
            v.put("cardCVV", cardCVV)
            v.put("cardDocNumber", cardDocNumber)
            id = setDatabase().insert("patientCreditCards", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }



    // PatientPregnancy CRUD
    /*
    idPatientPregnancy INT
    idPatient INT
    startPregnancyDate TEXT
    positivePregnancyDate TEXT
    finishPregnancyDate TEXT
     */

    fun updateFinishPregnancyDate(idPatientPregnancy: Int, finishPregnancyDate: String): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE patientPregnancy SET finishPregnancyDate = '$finishPregnancyDate' WHERE idPatientPregnancy = $idPatientPregnancy")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun createPatientPregnancy(idPatient: Int, startPregnancyDate: String, positivePregnancyDate: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatient", idPatient)
            v.put("startPregnancyDate", startPregnancyDate)
            v.put("positivePregnancyDate", positivePregnancyDate)
            v.put("finishPregnancyDate", calculateFinishPregnancyDate(startPregnancyDate))
            id = setDatabase().insert("patientPregnancy", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    fun readPregnancyFromPatient(idPatient: Int): Int {
        var idPatientPregnancy = 0
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPatientPregnancy FROM patientPregnancy WHERE idPatient = $idPatient", null)
        if (cursor.moveToFirst()) {
            idPatientPregnancy = cursor.getInt(0)
        }
        cursor.close()
        setDatabase().close()
        return idPatientPregnancy
    }

    fun readPatientPregnancy(idPatientPregnancy: Int): PatientPregnancy {
        val patientPregnancy = PatientPregnancy()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM patientPregnancy WHERE idPatientPregnancy = '$idPatientPregnancy'", null)
        if (cursor.moveToFirst()) {
            patientPregnancy.idPatientPregnancy = cursor.getInt(0)
            patientPregnancy.patient = readPatient(cursor.getInt(1))
            patientPregnancy.startPregnancyDate = cursor.getString(2)
            patientPregnancy.positivePregnancyDate = cursor.getString(3)
            patientPregnancy.finishPregnancyDate = cursor.getString(4)
        }
        cursor.close()
        setDatabase().close()
        return patientPregnancy
    }

    fun deletePatientPregnancy(idPatientPregnancy: Int): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL("DELETE FROM patientPregnancy WHERE idPatientPregnancy = $idPatientPregnancy")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }


    // PatientTests CRUD
    /*
    idPatientTest INT
    idPatient INT
    idMedicalTest INT
    date TEXT
    urlResult TEXT
     */

    fun readAllPatientTests(idPatient: Int): ArrayList<PatientTest> {
        val listPatientTests = ArrayList<PatientTest>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM patientTests WHERE idPatient = '$idPatient' ORDER BY date DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val patientTest = PatientTest()
                patientTest.idPatientTest = cursor.getInt(0)
                patientTest.patient = readPatient(cursor.getInt(1))
                patientTest.medicalTest = readMedicalTest(cursor.getInt(2))
                patientTest.date = cursor.getString(3)
                patientTest.urlResult = cursor.getString(4)
                listPatientTests.add(patientTest)
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPatientTests
    }

    fun readAllPatientTestsByDateOrName(idPatient: Int, areWeLookingForDate: Boolean): Map<String, ArrayList<PatientTest>> {
        val listPatientTest = mutableMapOf<String, ArrayList<PatientTest>>()

        val query: String = if (areWeLookingForDate) {
            """
            SELECT pT.date AS date, pT.idPatientTest AS idPatientTest
            FROM patientTests pT
            WHERE pT.idPatient = ?
            ORDER BY pT.date DESC
            """.trimIndent()
        } else {
            """
            SELECT mT.name AS medicalTestName, pT.idPatientTest AS idPatientTest 
            FROM patientTests pT
            INNER JOIN medicalTests mT ON pT.idMedicalTest = mT.idMedicalTest
            WHERE pT.idPatient = ?
            ORDER BY mT.name
            """.trimIndent()
        }

        val searchTags = arrayOf(idPatient.toString())

        val cursor = setDatabase().rawQuery(query, searchTags)

        if (cursor.moveToFirst()) {
            do {
                val superText = if (areWeLookingForDate) {
                    dateConverter(cursor.getString(0))
                } else {
                    cursor.getString(0)
                }
                val idPatientTest = cursor.getInt(1)

                val patientTest = readPatientTest(idPatientTest)

                if (listPatientTest.containsKey(superText)) {
                    listPatientTest[superText]?.add(patientTest)
                } else {
                    listPatientTest[superText] = mutableListOf(patientTest) as ArrayList<PatientTest>
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        setDatabase().close()
        return listPatientTest
    }

    private fun readPatientTest(idPatientTest: Int): PatientTest {
        val patientTest = PatientTest()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM patientTests WHERE idPatientTest = $idPatientTest", null)
        if (cursor.moveToFirst()) {
            patientTest.idPatientTest = cursor.getInt(0)
            patientTest.patient = readPatient(cursor.getInt(1))
            patientTest.medicalTest = readMedicalTest(cursor.getInt(2))
            patientTest.date = dateConverter(cursor.getString(3))
            patientTest.urlResult = cursor.getString(4)
        }
        setDatabase().close()
        cursor.close()
        return patientTest
    }



    // PhonesPatients CRUD
    /*
    idPatient INT
    phoneHome TEXT
    alternativePhoneHome TEXT
    particularPhone TEXT
    cellphone TEXT
    company TEXT
    fax TEXT
    phoneWork TEXT
     */

    fun editPhones(phonesPatient: PhonesPatient): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL(
                "UPDATE phonesPatients SET phoneHome = '${phonesPatient.phoneHome}', alternativePhoneHome = '${phonesPatient.alternativePhoneHome}', particularPhone = '${phonesPatient.particularPhone}', cellphone = '${phonesPatient.cellphone}', company = '${phonesPatient.company}', fax = '${phonesPatient.fax}', phoneWork = '${phonesPatient.phoneWork}' WHERE idPatient = '${phonesPatient.patient.idPatient}'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readPhonesFromPatient(idPatient: Int): PhonesPatient {
        val phonesPatient = PhonesPatient()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM phonesPatients WHERE idPatient = '$idPatient'", null)
        if (cursor.moveToFirst()) {
            phonesPatient.patient = readPatient(cursor.getInt(0))
            phonesPatient.phoneHome = cursor.getString(1)
            phonesPatient.alternativePhoneHome = cursor.getString(2)
            phonesPatient.particularPhone = cursor.getString(3)
            phonesPatient.cellphone = cursor.getString(4)
            phonesPatient.company = cursor.getString(5)
            phonesPatient.fax = cursor.getString(6)
            phonesPatient.phoneWork = cursor.getString(7)
        }
        cursor.close()
        setDatabase().close()
        return phonesPatient
    }



    // Places CRUD
    /*
    idPlace INT
    name TEXT
    address TEXT
    phone TEXT
    openingHours TEXT
     */

    fun readPlaceByLocality(locality: String): Place {
        var place = Place()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPlace FROM places WHERE name LIKE '%$locality%'", null)
        if (cursor.moveToFirst()) {
            place = readPlace(cursor.getInt(0))
        }
        cursor.close()
        setDatabase().close()
        return place
    }

    private fun readPlace(idPlace: Int): Place {
        val place = Place()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM places WHERE idPlace = '$idPlace'", null)
        if (cursor.moveToFirst()) {
            place.idPlace = cursor.getInt(0)
            place.name = cursor.getString(1)
            place.address = cursor.getString(2)
            place.phone = cursor.getString(3)
            place.openingHours = cursor.getString(4)
        }
        setDatabase().close()
        cursor.close()
        return place
    }



    // PlanCoverages CRUD
    /*
    idPlan INT
    idCoverage INT
    planType TEXT
     */

    private fun readPlanCoverage(idPlan: Int): PlanCoverage {
        setDatabase()
        val planCoverage = PlanCoverage()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM planCoverages WHERE idPlan = $idPlan", null)
        if (cursor.moveToFirst()) {
            planCoverage.idPlan = cursor.getInt(0)
            planCoverage.coverage = readCoverage(cursor.getInt(1))
            planCoverage.planType = cursor.getString(2)
        }
        setDatabase().close()
        cursor.close()
        return planCoverage
    }

    private fun readPlanByNameAndCoverage(planType: String, nameCoverage: String): PlanCoverage {
        setDatabase()
        val planCoverage = PlanCoverage()
        val cursorPlans: Cursor = setDatabase().rawQuery(
            "SELECT * FROM planCoverages pC INNER JOIN coverages c ON pC.idCoverage = c.idCoverage WHERE pC.idCoverage = '${
                readCoverageByName(nameCoverage).idCoverage
            }' AND pC.planType = '$planType'", null
        )
        if (cursorPlans.moveToFirst()) {
            planCoverage.idPlan = cursorPlans.getInt(0)
            planCoverage.coverage.idCoverage = cursorPlans.getInt(3)
            planCoverage.coverage.name = cursorPlans.getString(4)
            planCoverage.planType = cursorPlans.getString(2)
        }
        setDatabase().close()
        cursorPlans.close()
        return planCoverage
    }

    fun readPlansByCoverage(nameCoverage: String): ArrayList<PlanCoverage> {
        setDatabase()
        val arrayPlans = ArrayList<PlanCoverage>()
        val cursorPlans: Cursor = setDatabase().rawQuery(
            "SELECT * FROM planCoverages pC INNER JOIN coverages c ON pC.idCoverage = c.idCoverage WHERE pC.idCoverage = '${
                readCoverageByName(nameCoverage).idCoverage
            }'", null
        )
        if (cursorPlans.moveToFirst()) {
            do {
                val pC = PlanCoverage()
                pC.idPlan = cursorPlans.getInt(0)
                pC.coverage.idCoverage = cursorPlans.getInt(3)
                pC.coverage.name = cursorPlans.getString(4)
                pC.planType = cursorPlans.getString(2)
                arrayPlans.add(pC)
            } while (cursorPlans.moveToNext())
        }
        setDatabase().close()
        cursorPlans.close()
        return arrayPlans
    }



    // PregnancyContraction
    /*
    idPregnancyContraction INT
    idPregnancyContractions INT
    duration TEXT
    interval TEXT
    startAndFinish TEXT
     */

    private fun readAllPregnancyContraction(idPregnancyContractions: Int): ArrayList<PregnancyContraction> {
        val listPregnancyContraction = ArrayList<PregnancyContraction>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPregnancyContraction FROM pregnancyContraction WHERE idPregnancyContractions = $idPregnancyContractions", null)
        if (cursor.moveToFirst()) {
            do {
                listPregnancyContraction.add(readPregnancyContraction(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPregnancyContraction
    }

    private fun readPregnancyContraction(idPregnancyContraction: Int): PregnancyContraction {
        val pregnancyContraction = PregnancyContraction()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM pregnancyContraction WHERE idPregnancyContraction = '$idPregnancyContraction'", null)
        if (cursor.moveToFirst()) {
            pregnancyContraction.idContraction = cursor.getInt(0)
            pregnancyContraction.idPregnancyContraction = cursor.getInt(1)
            pregnancyContraction.duration = cursor.getString(2)
            pregnancyContraction.interval = cursor.getString(3)
            pregnancyContraction.startAndFinish = cursor.getString(4)
        }
        cursor.close()
        setDatabase().close()
        return pregnancyContraction
    }

    fun createPregnancyContraction(idPregnancyContractions: Int, duration: String, interval: String, startAndFinish: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPregnancyContractions", idPregnancyContractions)
            v.put("duration", duration)
            v.put("interval", interval)
            v.put("startAndFinish", startAndFinish)
            id = setDatabase().insert("pregnancyContraction", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }



    // PregnancyContractions
    /*
    idPregnancyContractions INT
    idPatientPregnancy INT
    date TEXT
     */

    fun readAllPregnancyContractions(idPatientPregnancy: Int): ArrayList<PregnancyContractions> {
        val listPregnancyContractions = ArrayList<PregnancyContractions>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPregnancyContractions FROM pregnancyContractions WHERE idPatientPregnancy = $idPatientPregnancy", null)
        if (cursor.moveToFirst()) {
            do {
                listPregnancyContractions.add(readPregnancyContractions(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPregnancyContractions
    }

    private fun readPregnancyContractions(idPregnancyContractions: Int): PregnancyContractions {
        val pregnancyContractions = PregnancyContractions()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM pregnancyContractions WHERE idPregnancyContractions = '$idPregnancyContractions'", null)
        if (cursor.moveToFirst()) {
            pregnancyContractions.idPregnancyContraction = cursor.getInt(0)
            pregnancyContractions.patientPregnancy = readPatientPregnancy(cursor.getInt(1))
            pregnancyContractions.date = cursor.getString(2)
            pregnancyContractions.contractions = readAllPregnancyContraction(cursor.getInt(0))
        }
        cursor.close()
        setDatabase().close()
        return pregnancyContractions
    }

    fun createPregnancyContractions(idPatientPregnancy: Int, date: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatientPregnancy", idPatientPregnancy)
            v.put("date", date)
            id = setDatabase().insert("pregnancyContractions", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }


    // PregnancyFeels
    /*
    idPregnancyFeel INT
    idPatientPregnancy INT
    feel TEXT
    date TEXT
     */

    fun createPregnancyFeels(idPatientPregnancy: Int, feel: String, date: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatientPregnancy", idPatientPregnancy)
            v.put("feel", feel)
            v.put("date", date)
            id = setDatabase().insert("pregnancyFeels", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    private fun readPregnancyFeel(idPregnancyFeel: Int): PregnancyFeel {
        val pregnancyFeel = PregnancyFeel()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM pregnancyFeels WHERE idPregnancyFeel = '$idPregnancyFeel'", null)
        if (cursor.moveToFirst()) {
            pregnancyFeel.idPregnancyFeel = cursor.getInt(0)
            pregnancyFeel.patientPregnancy = readPatientPregnancy(cursor.getInt(1))
            pregnancyFeel.feel = cursor.getString(2)
            pregnancyFeel.date = cursor.getString(3)
        }
        cursor.close()
        setDatabase().close()
        return pregnancyFeel
    }

    fun readAllPregnancyFeelsFromPatient(idPatientPregnancy: Int): ArrayList<PregnancyFeel> {
        val listPregnancyFeel = ArrayList<PregnancyFeel>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPregnancyFeel FROM pregnancyFeels WHERE idPatientPregnancy = $idPatientPregnancy ORDER BY date", null)
        if (cursor.moveToFirst()) {
            do {
                listPregnancyFeel.add(readPregnancyFeel(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPregnancyFeel
    }

    // PregnancyNotes
    /*
    idPregnancyNote INT
    idPatientPregnancy INT
    title TEXT
    note TEXT
    date TEXT
     */

    fun createPregnancyNotes(idPatientPregnancy: Int, title: String, note: String, date: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatientPregnancy", idPatientPregnancy)
            v.put("title", title)
            v.put("note", note)
            v.put("date", date)
            id = setDatabase().insert("pregnancyNotes", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    private fun readPregnancyNote(idPregnancyNote: Int): PregnancyNote {
        val pregnancyNote = PregnancyNote()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM pregnancyNotes WHERE idPregnancyNote = '$idPregnancyNote'", null)
        if (cursor.moveToFirst()) {
            pregnancyNote.idPregnancyNote = cursor.getInt(0)
            pregnancyNote.patientPregnancy = readPatientPregnancy(cursor.getInt(1))
            pregnancyNote.title = cursor.getString(2)
            pregnancyNote.note = cursor.getString(3)
            pregnancyNote.date = cursor.getString(4)
        }
        cursor.close()
        setDatabase().close()
        return pregnancyNote
    }

    fun readAllPregnancyNotesFromPatient(idPatientPregnancy: Int): ArrayList<PregnancyNote> {
        val listPregnancyNote = ArrayList<PregnancyNote>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPregnancyNote FROM pregnancyNotes WHERE idPatientPregnancy = $idPatientPregnancy", null)
        if (cursor.moveToFirst()) {
            do {
                listPregnancyNote.add(readPregnancyNote(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPregnancyNote
    }



    // PregnancyPhotos
    /*
    idPregnancyPhoto INT
    idPatientPregnancy INT
    title TEXT
    imagePath TEXT
    date TEXT
     */

    fun createPregnancyPhotos(idPatientPregnancy: Int, title: String, imagePath: String, date: String): Long {
        setDatabase()
        var id: Long = 0
        try {
            val v = ContentValues()
            v.put("idPatientPregnancy", idPatientPregnancy)
            v.put("title", title)
            v.put("imagePath", imagePath)
            v.put("date", date)
            id = setDatabase().insert("pregnancyPhotos", null, v)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            setDatabase().close()
        }
        return id
    }

    private fun readPregnancyPhoto(idPregnancyPhoto: Int): PregnancyPhoto {
        val pregnancyPhoto = PregnancyPhoto()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM pregnancyPhotos WHERE idPregnancyPhoto = '$idPregnancyPhoto'", null)
        if (cursor.moveToFirst()) {
            pregnancyPhoto.idPregnancyPhoto = cursor.getInt(0)
            pregnancyPhoto.patientPregnancy = readPatientPregnancy(cursor.getInt(1))
            pregnancyPhoto.title = cursor.getString(2)
            pregnancyPhoto.imagePath = cursor.getString(3)
            pregnancyPhoto.date = cursor.getString(4)
        }
        cursor.close()
        setDatabase().close()
        return pregnancyPhoto
    }

    fun readAllPregnancyPhotosFromPatient(idPatientPregnancy: Int): ArrayList<PregnancyPhoto> {
        val listPregnancyPhoto = ArrayList<PregnancyPhoto>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT idPregnancyPhoto FROM pregnancyPhotos WHERE idPatientPregnancy = $idPatientPregnancy", null)
        if (cursor.moveToFirst()) {
            do {
                listPregnancyPhoto.add(readPregnancyPhoto(cursor.getInt(0)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listPregnancyPhoto
    }



    // Prescriptions CRUD
    /*
    idPrescription INT
    idPatient INT
    idDoctor INT
    idDrug INT
    currentOrExpired TEXT
     */

    fun updatePassedPrescriptions(idPrescription: Int): Boolean {
        setDatabase()
        return try {
            setDatabase().execSQL("UPDATE drugs SET expiredDate = '-' WHERE drugs.idDrug IN ( SELECT prescriptions.idDrug FROM prescriptions WHERE idPrescription = '$idPrescription')")
            setDatabase().execSQL("UPDATE prescriptions SET currentOrExpired = 'Expired' WHERE idPrescription = '$idPrescription'")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            setDatabase().close()
        }
    }

    fun readAllPrescriptionsByPatient(idPatient: Int, currentOrExpired: String, searchLetter: String?): ArrayList<Prescription> {
        setDatabase()
        val listPrescription = ArrayList<Prescription>()
        val query = """
        SELECT p.idPrescription AS pID, d.idDoctor AS dID, d.name AS dName, d.lastName AS dLastName, dr.idDrug AS drID, dr.nameDrugSubstance AS drDrugSubstance, dr.nameDrugProduct AS drDrugProduct, p.currentOrExpired AS pCORE
        FROM prescriptions p
        INNER JOIN doctors d ON p.idDoctor = d.idDoctor
        INNER JOIN drugs dr ON p.idDrug = dr.idDrug
        WHERE p.idPatient = ? ${if (!searchLetter.isNullOrBlank()) "AND ((LOWER(d.name) LIKE LOWER(?) OR LOWER(d.lastName) LIKE LOWER(?)) OR (LOWER(dr.nameDrugSubstance) LIKE LOWER(?) OR LOWER(dr.nameDrugProduct) LIKE LOWER(?)))" else ""} AND p.currentOrExpired = '$currentOrExpired'
        """.trimIndent()


        val searchArgs = if (!searchLetter.isNullOrBlank()) {
            arrayOf(idPatient.toString(), "%$searchLetter%", "%$searchLetter%", "%$searchLetter%", "%$searchLetter%")
        } else {
            arrayOf(idPatient.toString())
        }

        val cursor = setDatabase().rawQuery(query, searchArgs)

        if (cursor.moveToFirst()) {
            do {
                val prescription = Prescription()
                prescription.idPrescription = cursor.getInt(0)
                prescription.patient = readPatient(idPatient)
                prescription.doctor = readDoctor(cursor.getInt(1))
                prescription.drug = readDrug(cursor.getInt(4))
                listPrescription.add(prescription)
            } while (cursor.moveToNext())
        }

        setDatabase().close()
        cursor.close()
        return listPrescription
    }



    // Province CRUD
    /*
    idProvince INT
    name TEXT
     */

    fun readAllProvinces(): ArrayList<Province> {
        val listProvince = ArrayList<Province>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM province", null)
        if (cursor.moveToFirst()) {
            do {
                val province = Province()
                province.id = cursor.getInt(0)
                province.name = cursor.getString(1)
                listProvince.add(province)
            }while (cursor.moveToNext())
        }
        cursor.close()
        setDatabase().close()
        return listProvince
    }

    fun readProvinceForSpinner(cp: String): ArrayList<String> {
        val listProvinces = ArrayList<String>()
        val listPreProvince = ArrayList<Int>()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT id_province FROM city WHERE cp = '$cp' ORDER BY id_province", null)
        if (cursor.moveToFirst()) { do { val idProvince = cursor.getInt(0); listPreProvince.add(idProvince) } while (cursor.moveToNext()) }
        var i = 0
        val listFinal = ArrayList<Int>()
        listPreProvince.forEach { if (it > i) { i = it; listFinal.add(it) } }
        listFinal.forEach { val cursorP: Cursor = setDatabase().rawQuery("SELECT province_name FROM province WHERE id = '$it'", null); if (cursorP.moveToFirst()) { listProvinces.add(cursorP.getString(0)) }; cursorP.close() }
        cursor.close()
        setDatabase().close()
        return listProvinces
    }

    private fun readProvinceByName(provinceName: String): Province {
        setDatabase()
        val province = Province()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM province WHERE province_name = '$provinceName'", null)
        if (cursor.moveToFirst()) {
            province.id = cursor.getInt(0)
            province.name = cursor.getString(1)
        }
        setDatabase().close()
        cursor.close()
        return province
    }


    // RequestedPatientTests CRUD
    /*
    idPatient INT
    idSpeciality INT
    date TEXT
    urlResult TEXT
     */

    fun readAllRequestedPatientTests(idPatient: Int, areWeLookingForDate: Boolean): Map<String, ArrayList<RequestedPatientTest>> {
        val listRequestedPatientTest = mutableMapOf<String, ArrayList<RequestedPatientTest>>()

        val query: String = if (areWeLookingForDate) {
            """
            SELECT rPT.date AS date, rPT.idRequestedPatientTest AS idRequestedPatientTest
            FROM requestedPatientTests rPT
            WHERE rPT.idPatient = ?
            ORDER BY rPT.date DESC
            """.trimIndent()
        } else {
            """
            SELECT s.name AS specialityName, rPT.idRequestedPatientTest AS idRequestedPatientTest 
            FROM requestedPatientTests rPT
            INNER JOIN specialities s ON rPT.idSpeciality = s.idSpeciality
            WHERE rPT.idPatient = ?
            ORDER BY s.name
            """.trimIndent()
        }

        val searchTags = arrayOf(idPatient.toString())

        val cursor = setDatabase().rawQuery(query, searchTags)

        if (cursor.moveToFirst()) {
            do {
                val superText = if (areWeLookingForDate) {
                    dateConverter(cursor.getString(0))
                } else {
                    cursor.getString(0)
                }
                val idRequestedPatientTest = cursor.getInt(1)

                val requestedPatientTest = readRequestedPatientTest(idRequestedPatientTest)

                if (listRequestedPatientTest.containsKey(superText)) {
                    listRequestedPatientTest[superText]?.add(requestedPatientTest)
                } else {
                    listRequestedPatientTest[superText] = mutableListOf(requestedPatientTest) as ArrayList<RequestedPatientTest>
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        setDatabase().close()
        return listRequestedPatientTest
    }

    private fun readRequestedPatientTest(idRequestedPatientTest: Int): RequestedPatientTest {
        val requestedPatientTest = RequestedPatientTest()
        setDatabase()
        val cursor: Cursor =
            setDatabase().rawQuery("SELECT * FROM requestedPatientTests WHERE idRequestedPatientTest = $idRequestedPatientTest", null)
        if (cursor.moveToFirst()) {
            requestedPatientTest.idRequestedPatientTest = cursor.getInt(0)
            requestedPatientTest.patient = readPatient(cursor.getInt(1))
            requestedPatientTest.speciality = readSpeciality(cursor.getInt(2))
            requestedPatientTest.date = dateConverter(cursor.getString(3))
            requestedPatientTest.urlResult = cursor.getString(4)
        }
        setDatabase().close()
        cursor.close()
        return requestedPatientTest
    }



    // Specialities CRUD
    /*
    idSpeciality INT
    name TEXT
     */

    private fun readSpeciality(idSpeciality: Int): Speciality {
        val speciality = Speciality()
        setDatabase()
        val cursor: Cursor = setDatabase().rawQuery("SELECT * FROM specialities WHERE idSpeciality = '$idSpeciality'", null)
        if (cursor.moveToFirst()) {
            speciality.idSpeciality = cursor.getInt(0)
            speciality.name = cursor.getString(1)
        }
        setDatabase().close()
        cursor.close()
        return speciality
    }
}