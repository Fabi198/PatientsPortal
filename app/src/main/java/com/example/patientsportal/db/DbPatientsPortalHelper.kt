package com.example.patientsportal.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.patientsportal.db.arrays.ArrayCoverages.arrayCoverages
import com.example.patientsportal.db.arrays.ArrayCoverages.arrayPlanCoverages
import com.example.patientsportal.db.arrays.ArrayDoctorRelations.arrayDoctorPatients
import com.example.patientsportal.db.arrays.ArrayDoctorRelations.arrayDoctorPlaces
import com.example.patientsportal.db.arrays.ArrayDoctorRelations.arrayDoctorSpecialities
import com.example.patientsportal.db.arrays.ArrayDoctors.arrayDoctors
import com.example.patientsportal.db.arrays.ArrayDrugs.arrayAlternativeDrugs
import com.example.patientsportal.db.arrays.ArrayDrugs.arrayDrugs
import com.example.patientsportal.db.arrays.ArrayHealthControls.arrayHCArterialPressure
import com.example.patientsportal.db.arrays.ArrayHealthControls.arrayHCBreathFrequency
import com.example.patientsportal.db.arrays.ArrayHealthControls.arrayHCHeartFrequency
import com.example.patientsportal.db.arrays.ArrayHealthControls.arrayHCTemperature
import com.example.patientsportal.db.arrays.ArrayHealthControls.arrayHCWeightAndHeight
import com.example.patientsportal.db.arrays.ArrayMedicalTests.arrayMedicalTest
import com.example.patientsportal.db.arrays.ArrayMedicalTests.arrayPatientTest
import com.example.patientsportal.db.arrays.ArrayMedicalTests.arrayRequestedMedicalTest
import com.example.patientsportal.db.arrays.ArrayNotifications.arrayNotifications
import com.example.patientsportal.db.arrays.ArrayPatientRelations.arrayAddresses
import com.example.patientsportal.db.arrays.ArrayPatientRelations.arrayBillAddresses
import com.example.patientsportal.db.arrays.ArrayPatientRelations.arrayContactPersons
import com.example.patientsportal.db.arrays.ArrayPatientRelations.arrayNotificationsSettings
import com.example.patientsportal.db.arrays.ArrayPatientRelations.arrayPhonesPatients
import com.example.patientsportal.db.arrays.ArrayPatients.arrayPatients
import com.example.patientsportal.db.arrays.ArrayPlaces.arrayPlaces
import com.example.patientsportal.db.arrays.ArrayPrescriptions.arrayPrescriptions
import com.example.patientsportal.db.arrays.ArrayProvinces.arrayProvinces
import com.example.patientsportal.db.arrays.ArraySpecialities.arraySpecialities
import com.example.patientsportal.db.provinces.AllProvinces.arrayAllProvinces

open class DbPatientsPortalHelper(var context: Context, dbName: String = "PatientsPortal", dbVersion: Int = 11): SQLiteOpenHelper(context, dbName, null, dbVersion) {

    private val createTablePatients = "CREATE TABLE IF NOT EXISTS patients (idPatient INTEGER PRIMARY KEY, name TEXT NOT NULL, lastName TEXT NOT NULL, mothersLastName TEXT, biologicalSex TEXT NOT NULL, gender TEXT NOT NULL, email TEXT NOT NULL, alternativeEmail TEXT NOT NULL, password TEXT NOT NULL, birthday TEXT, documentType TEXT NOT NULL, documentNumber TEXT NOT NULL, idClinicalHistory TEXT NOT NULL, observations TEXT, intImage INTEGER, uriImage TEXT, language TEXT NOT NULL, countryBirth TEXT NOT NULL, educationLevelReached TEXT NOT NULL, homeType TEXT NOT NULL, whoLiveWith TEXT NOT NULL, religion TEXT NOT NULL, disability TEXT NOT NULL)"
    private val createTablePatientCreditCards = "CREATE TABLE IF NOT EXISTS patientCreditCards (idCreditCard INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), cardBrand TEXT NOT NULL, cardOwner TEXT NOT NULL, cardNumber TEXT NOT NULL, cardExpiration TEXT NOT NULL, cardCVV TEXT NOT NULL, cardDocNumber TEXT NOT NULL)"
    private val createTablePatientPregnancy = "CREATE TABLE IF NOT EXISTS patientPregnancy (idPatientPregnancy INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), startPregnancyDate TEXT NOT NULL, positivePregnancyDate TEXT NOT NULL, finishPregnancyDate TEXT NOT NULL)"
    private val createTablePregnancyFeels = "CREATE TABLE IF NOT EXISTS pregnancyFeels (idPregnancyFeel INTEGER PRIMARY KEY AUTOINCREMENT, idPatientPregnancy REFERENCES patientPregnancy(idPatientPregnancy), feel TEXT NOT NULL, date TEXT NOT NULL)"
    private val createTablePregnancyNotes = "CREATE TABLE IF NOT EXISTS pregnancyNotes (idPregnancyNote INTEGER PRIMARY KEY AUTOINCREMENT, idPatientPregnancy REFERENCES patientPregnancy(idPatientPregnancy), title TEXT NOT NULL, note TEXT NOT NULL, date TEXT NOT NULL)"
    private val createTablePregnancyPhotos = "CREATE TABLE IF NOT EXISTS pregnancyPhotos (idPregnancyPhoto INTEGER PRIMARY KEY AUTOINCREMENT, idPatientPregnancy REFERENCES patientPregnancy(idPatientPregnancy), title TEXT NOT NULL, imagePath TEXT NOT NULL, date TEXT NOT NULL)"
    private val createTablePregnancyContractions = "CREATE TABLE IF NOT EXISTS pregnancyContractions (idPregnancyContractions INTEGER PRIMARY KEY AUTOINCREMENT, idPatientPregnancy REFERENCES patientPregnancy(idPatientPregnancy), date TEXT NOT NULL)"
    private val createTablePregnancyContraction = "CREATE TABLE IF NOT EXISTS pregnancyContraction (idPregnancyContraction INTEGER PRIMARY KEY AUTOINCREMENT, idPregnancyContractions REFERENCES pregnancyContractions(idPregnancyContractions), duration TEXT NOT NULL, interval TEXT NOT NULL, startAndFinish TEXT NOT NULL)"
    private val createTableAddress = "CREATE TABLE IF NOT EXISTS address (idPatient INTEGER PRIMARY KEY REFERENCES patients(idPatient), street TEXT NOT NULL, number TEXT NOT NULL, floor TEXT NOT NULL, department TEXT NOT NULL, observations TEXT, postalCode TEXT NOT NULL, country TEXT NOT NULL, province TEXT NOT NULL, locality TEXT NOT NULL)"
    private val createTableBillAddress = "CREATE TABLE IF NOT EXISTS billAddress (idPatient INTEGER PRIMARY KEY REFERENCES patients(idPatient), street TEXT NOT NULL, number TEXT NOT NULL, floor TEXT NOT NULL, department TEXT NOT NULL, observations TEXT, postalCode TEXT NOT NULL, country TEXT NOT NULL, province TEXT NOT NULL, locality TEXT NOT NULL)"
    private val createTableMedicalTest = "CREATE TABLE IF NOT EXISTS medicalTests (idMedicalTest INTEGER NOT NULL, name TEXT NOT NULL, preparation TEXT NOT NULL)"
    private val createTablePatientTest = "CREATE TABLE IF NOT EXISTS patientTests (idPatientTest INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), idMedicalTest REFERENCES medicalTests(id), date TEXT NOT NULL, urlResult TEXT)"
    private val createTableRequestedPatientTest = "CREATE TABLE IF NOT EXISTS requestedPatientTests (idRequestedPatientTest INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), idSpeciality REFERENCES specialities(idSpeciality), date TEXT NOT NULL, urlResult TEXT)"
    private val createTableAppointment = "CREATE TABLE IF NOT EXISTS appointments (idAppointment INTEGER PRIMARY KEY, idPatientCoveragePlan REFERENCES patientCoveragePlans(idPatientCoveragePlan), idPatient REFERENCES patients(idPatient), idDoctorSpeciality REFERENCES doctorSpecialities(idDoctorSpeciality), idPlace REFERENCES places(idPlace), date TEXT NOT NULL, showUpPatient TEXT NOT NULL)"
    private val createTableCoverages = "CREATE TABLE IF NOT EXISTS coverages (idCoverage INTEGER PRIMARY KEY, name TEXT NOT NULL)"
    private val createTablePlanCoverage = "CREATE TABLE IF NOT EXISTS planCoverages (idPlan INTEGER PRIMARY KEY, idCoverage REFERENCES coverages(idCoverage), planType TEXT NOT NULL)"
    private val createTablePatientCoveragePlan = "CREATE TABLE IF NOT EXISTS patientCoveragePlans (idPatientCoveragePlan INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), idCoverage REFERENCES coverages(idCoverage), idPlan REFERENCES planCoverages(idPlan), affiliateNumber TEXT NOT NULL)"
    private val createTableDoctors = "CREATE TABLE IF NOT EXISTS doctors (idDoctor INTEGER PRIMARY KEY, name TEXT NOT NULL, lastName TEXT NOT NULL, dni TEXT NOT NULL, gender TEXT NOT NULL, email TEXT NOT NULL, birthday TEXT NOT NULL, codarea TEXT, numCelular TEXT, intImage INTEGER)"
    private val createTablePlaces = "CREATE TABLE IF NOT EXISTS places (idPlace INTEGER PRIMARY KEY, name TEXT NOT NULL, address TEXT NOT NULL, phone TEXT NOT NULL, openingHours TEXT NOT NULL)"
    private val createTableSpecialities = "CREATE TABLE IF NOT EXISTS specialities (idSpeciality PRIMARY KEY, name TEXT NOT NULL)"
    private val createTableDoctorSpeciality = "CREATE TABLE IF NOT EXISTS doctorSpecialities (idDoctorSpeciality INTEGER PRIMARY KEY, idDoctor REFERENCES doctors(idDoctor), idSpeciality REFERENCES specialities(idSpeciality))"
    private val createTableDoctorPatient = "CREATE TABLE IF NOT EXISTS doctorPatients (idDoctorSpeciality REFERENCES doctorSpecialities(idDoctorSpeciality), idPatient REFERENCES patients(idPatient))"
    private val createTableDrugs = "CREATE TABLE IF NOT EXISTS drugs (idDrug INTEGER PRIMARY KEY, nameDrugSubstance TEXT NOT NULL, expiredDate TEXT NOT NULL, nameDrugProduct TEXT NOT NULL, dosage TEXT NOT NULL, price DOUBLE NOT NULL, stockAvailable INTEGER NOT NULL, maximumUnitsToBuy INTEGER NOT NULL)"
    private val createTableAlternativeDrugs = "CREATE TABLE IF NOT EXISTS alternativeDrugs (idAlternativeDrug INTEGER PRIMARY KEY, idDrug REFERENCES drugs(idDrug), name TEXT NOT NULL, price DOUBLE NOT NULL, stockAvailable INTEGER NOT NULL, maximumUnitsToBuy INTEGER NOT NULL)"
    private val createTablePrescriptions = "CREATE TABLE IF NOT EXISTS prescriptions (idPrescription INTEGER PRIMARY KEY, idPatient REFERENCES patients(idPatient), idDoctor REFERENCES doctors(idDoctor), idDrug REFERENCES drugs(idDrug), currentOrExpired TEXT NOT NULL)"
    private val createTableDoctorPlaces = "CREATE TABLE IF NOT EXISTS doctorPlaces (idDoctorPlace INTEGER PRIMARY KEY, idDoctorSpeciality REFERENCES doctorSpecialities(idDoctorSpeciality), idPlace REFERENCES places(idPlace))"
    private val createTableContactPersons = "CREATE TABLE IF NOT EXISTS contactPersons (idPatient REFERENCES patients(idPatient), names TEXT NOT NULL, lastNames TEXT NOT NULL, relationship TEXT NOT NULL, address TEXT NOT NULL, phone TEXT NOT NULL)"
    private val createTablePhonesPatients = "CREATE TABLE IF NOT EXISTS phonesPatients (idPatient REFERENCES patients(idPatient), phoneHome TEXT NOT NULL, alternativePhoneHome TEXT NOT NULL, particularPhone TEXT NOT NULL, cellphone TEXT NOT NULL, company TEXT NOT NULL, fax TEXT NOT NULL, phoneWork TEXT NOT NULL)"
    private val createTableProvinces = "CREATE TABLE IF NOT EXISTS province (id INTEGER PRIMARY KEY, province_name TEXT NOT NULL)"
    private val createTableCitys = "CREATE TABLE IF NOT EXISTS city (id INTEGER NOT NULL, city_name TEXT NOT NULL, cp INTEGER NOT NULL, id_province INTEGER REFERENCES province(id))"
    private val createTableHCWeightAndHeight = "CREATE TABLE IF NOT EXISTS hcWeightAndHeight (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, weight TEXT NOT NULL, height TEXT NOT NULL, imc TEXT NOT NULL, loadedBy TEXT NOT NULL)"
    private val createTableHCTemp = "CREATE TABLE IF NOT EXISTS hcTemperature (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, temperature TEXT NOT NULL, bodyPart TEXT, loadedBy TEXT NOT NULL)"
    private val createTableHCHeartFrequency = "CREATE TABLE IF NOT EXISTS hcHeartFrequency (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, heartFrequency TEXT NOT NULL, method TEXT, loadedBy TEXT NOT NULL)"
    private val createTableHCArterialPressure = "CREATE TABLE IF NOT EXISTS hcArterialPressure (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, lowArterialPressure TEXT NOT NULL, highArterialPressure TEXT NOT NULL, bodyPart TEXT, bloodPressureMonitorType TEXT, personWhoTookTheTest TEXT, loadedBy TEXT NOT NULL)"
    private val createTableHCGlucose = "CREATE TABLE IF NOT EXISTS hcGlucose (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, glucose TEXT NOT NULL, eatLastTwoHours TEXT NOT NULL, loadedBy TEXT NOT NULL)"
    private val createTableHCBreathFrequency = "CREATE TABLE IF NOT EXISTS hcBreathFrequency (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, breathFrequency TEXT NOT NULL, loadedBy TEXT NOT NULL)"
    private val createTableHCOxygenSaturation = "CREATE TABLE IF NOT EXISTS hcOxygenSaturation (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, oxygenSaturation TEXT NOT NULL, hasSupplementaryOxygen TEXT NOT NULL, loadedBy TEXT NOT NULL)"
    private val createTableHCBreathlessness = "CREATE TABLE IF NOT EXISTS hcBreathlessness (idHC INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, breathlessness TEXT NOT NULL, loadedBy TEXT NOT NULL)"
    private val createTableNotifications = "CREATE TABLE IF NOT EXISTS notifications (idNotification INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), date TEXT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, readed TEXT NOT NULL)"
    private val createTableNotificationsSettings = "CREATE TABLE IF NOT EXISTS notificationsSettings (idNotificationSettings INTEGER PRIMARY KEY AUTOINCREMENT, idPatient REFERENCES patients(idPatient), medicalTestEnabled TEXT NOT NULL, medicalTestPush TEXT NOT NULL, medicalTestEmail TEXT NOT NULL, medicalTestReminderOne INTEGER, medicalTestReminderTwo INTEGER, medicalTestReminderThree INTEGER, medicalTestReminderFour INTEGER, medicalTestReminderFive INTEGER, drugsEnabled TEXT NOT NULL, drugsPush TEXT NOT NULL, drugsEmail TEXT NOT NULL, drugsReminderOne INTEGER, drugsReminderTwo INTEGER, drugsReminderThree INTEGER, drugsReminderFour INTEGER, drugsReminderFive INTEGER, derivationsEnabled TEXT NOT NULL, derivationsPush TEXT NOT NULL, derivationsEmail TEXT NOT NULL, patientsPortalEnabled TEXT NOT NULL, patientsPortalPush TEXT NOT NULL, patientsPortalEmail TEXT NOT NULL)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTablePatients)
        db?.execSQL(createTablePatientCreditCards)
        db?.execSQL(createTableAddress)
        db?.execSQL(createTableBillAddress)
        db?.execSQL(createTableMedicalTest)
        db?.execSQL(createTablePatientTest)
        db?.execSQL(createTableRequestedPatientTest)
        db?.execSQL(createTableAppointment)
        db?.execSQL(createTableCoverages)
        db?.execSQL(createTablePlanCoverage)
        db?.execSQL(createTablePatientCoveragePlan)
        db?.execSQL(createTableDoctors)
        db?.execSQL(createTablePlaces)
        db?.execSQL(createTableSpecialities)
        db?.execSQL(createTableDoctorSpeciality)
        db?.execSQL(createTableDoctorPatient)
        db?.execSQL(createTableDrugs)
        db?.execSQL(createTableAlternativeDrugs)
        db?.execSQL(createTablePrescriptions)
        db?.execSQL(createTableDoctorPlaces)
        db?.execSQL(createTableContactPersons)
        db?.execSQL(createTablePhonesPatients)
        db?.execSQL(createTableProvinces)
        db?.execSQL(createTableCitys)
        db?.execSQL(createTableHCWeightAndHeight)
        db?.execSQL(createTableHCTemp)
        db?.execSQL(createTableHCHeartFrequency)
        db?.execSQL(createTableHCArterialPressure)
        db?.execSQL(createTableHCGlucose)
        db?.execSQL(createTableHCBreathFrequency)
        db?.execSQL(createTableHCOxygenSaturation)
        db?.execSQL(createTableHCBreathlessness)
        db?.execSQL(createTableNotifications)
        db?.execSQL(createTableNotificationsSettings)
        db?.execSQL(createTablePatientPregnancy)
        db?.execSQL(createTablePregnancyFeels)
        db?.execSQL(createTablePregnancyNotes)
        db?.execSQL(createTablePregnancyPhotos)
        db?.execSQL(createTablePregnancyContraction)
        db?.execSQL(createTablePregnancyContractions)
        loadData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE patients")
        db?.execSQL("DROP TABLE patientCreditCards")
        db?.execSQL("DROP TABLE address")
        db?.execSQL("DROP TABLE billAddress")
        db?.execSQL("DROP TABLE medicalTests")
        db?.execSQL("DROP TABLE patientTests")
        db?.execSQL("DROP TABLE requestedPatientTests")
        db?.execSQL("DROP TABLE appointments")
        db?.execSQL("DROP TABLE coverages")
        db?.execSQL("DROP TABLE planCoverages")
        db?.execSQL("DROP TABLE patientCoveragesPlans")
        db?.execSQL("DROP TABLE doctors")
        db?.execSQL("DROP TABLE places")
        db?.execSQL("DROP TABLE specialities")
        db?.execSQL("DROP TABLE doctorSpecialities")
        db?.execSQL("DROP TABLE doctorPatients")
        db?.execSQL("DROP TABLE drugs")
        db?.execSQL("DROP TABLE alternativeDrugs")
        db?.execSQL("DROP TABLE prescriptions")
        db?.execSQL("DROP TABLE doctorPlaces")
        db?.execSQL("DROP TABLE contactPersons")
        db?.execSQL("DROP TABLE phonesPatients")
        db?.execSQL("DROP TABLE province")
        db?.execSQL("DROP TABLE city")
        db?.execSQL("DROP TABLE hcWeightAndHeight")
        db?.execSQL("DROP TABLE hcTemperature")
        db?.execSQL("DROP TABLE hcHeartFrequency")
        db?.execSQL("DROP TABLE hcArterialPressure")
        db?.execSQL("DROP TABLE hcGlucose")
        db?.execSQL("DROP TABLE hcBreathFrequency")
        db?.execSQL("DROP TABLE hcOxygenSaturation")
        db?.execSQL("DROP TABLE hcBreathlessness")
        db?.execSQL("DROP TABLE notifications")
        db?.execSQL("DROP TABLE notificationsSettings")
        db?.execSQL("DROP TABLE patientPregnancy")
        db?.execSQL("DROP TABLE pregnancyFeels")
        db?.execSQL("DROP TABLE pregnancyNotes")
        db?.execSQL("DROP TABLE pregnancyPhotos")
        db?.execSQL("DROP TABLE pregnancyContraction")
        db?.execSQL("DROP TABLE pregnancyContractions")
        onCreate(db)
    }

    private fun loadData(db: SQLiteDatabase?) {
        arrayPatients.forEach { db?.execSQL("INSERT INTO patients VALUES('${it.idPatient}', '${it.name}', '${it.lastName}', '${it.mothersLastName}', '${it.biologicalSex}', '${it.gender}', '${it.email}', '${it.alternativeEmail}', '${it.password}', '${it.birthday}', '${it.documentType}', '${it.documentNumber}', '${it.idClinicalHistory}', '${it.observations}', '${it.intImage}', '${it.uriImage}', '${it.language}', '${it.countryBirth}', '${it.educationLevelReached}', '${it.homeType}', '${it.whoLiveWith}', '${it.religion}', '${it.disability}')") }
        arrayAddresses.forEach { db?.execSQL("INSERT INTO address VALUES('${it.patient.idPatient}', '${it.street}', '${it.number}', '${it.floor}', '${it.department}', '${it.observations}', '${it.postalCode}', '${it.country}', '${it.province}', '${it.locality}')") }
        arrayBillAddresses.forEach { db?.execSQL("INSERT INTO billAddress VALUES('${it.patient.idPatient}', '${it.street}', '${it.number}', '${it.floor}', '${it.department}', '${it.observations}', '${it.postalCode}', '${it.country}', '${it.province}', '${it.locality}')") }
        arrayPhonesPatients.forEach { db?.execSQL("INSERT INTO phonesPatients VALUES('${it.patient.idPatient}', '${it.phoneHome}', '${it.alternativePhoneHome}', '${it.particularPhone}', '${it.cellphone}', '${it.company}', '${it.fax}', '${it.phoneWork}')") }
        arrayContactPersons.forEach { db?.execSQL("INSERT INTO contactPersons VALUES('${it.patient.idPatient}', '${it.names}', '${it.lastNames}', '${it.relationship}', '${it.address}', '${it.phone}')") }
        arrayDoctors.forEach { db?.execSQL("INSERT INTO doctors (idDoctor, name, lastName, dni, gender, email, birthday, codArea, numCelular, intImage) VALUES('${it.idDoctor}', '${it.name}', '${it.lastName}', '${it.dni}', '${it.gender}', '${it.email}', '${it.birthday}', '${it.codArea}', '${it.numCelular}', '${it.intImage}')") }
        arraySpecialities.forEach { db?.execSQL("INSERT INTO specialities VALUES('${it.idSpeciality}', '${it.name}')") }
        arrayDoctorSpecialities.forEach { db?.execSQL("INSERT INTO doctorSpecialities VALUES('${it.idDoctorSpeciality}', '${it.doctor.idDoctor}', '${it.speciality.idSpeciality}')") }
        arrayDoctorPatients.forEach { db?.execSQL("INSERT INTO doctorPatients VALUES('${it.doctorSpeciality.idDoctorSpeciality}', '${it.patient.idPatient}')") }
        arrayCoverages.forEach { db?.execSQL("INSERT INTO coverages VALUES('${it.idCoverage}', '${it.name}')") }
        arrayPlanCoverages.forEach { db?.execSQL("INSERT INTO planCoverages VALUES('${it.idPlan}', '${it.coverage.idCoverage}', '${it.planType}')") }
        arrayPlaces.forEach { db?.execSQL("INSERT INTO places VALUES('${it.idPlace}', '${it.name}', '${it.address}', '${it.phone}', '${it.openingHours}')") }
        arrayDrugs.forEach { db?.execSQL("INSERT INTO drugs VALUES('${it.idDrug}', '${it.nameDrugSubstance}', '${it.expiredDate}', '${it.nameDrugProduct}', '${it.dosage}', '${it.price}', '${it.stockAvailable}', '${it.maximumUnitsToBuy}')") }
        arrayAlternativeDrugs.forEach { db?.execSQL("INSERT INTO alternativeDrugs VALUES('${it.idAlternativeDrug}', '${it.drug.idDrug}', '${it.name}', '${it.price}', '${it.stockAvailable}', '${it.maximumUnitsToBuy}')") }
        arrayPrescriptions.forEach { db?.execSQL("INSERT INTO prescriptions VALUES('${it.idPrescription}', '${it.patient.idPatient}', '${it.doctor.idDoctor}', '${it.drug.idDrug}', '${it.currentOrExpired}')") }
        arrayMedicalTest.forEach { db?.execSQL("INSERT INTO medicalTests (idMedicalTest, name, preparation) VALUES('${it.idMedicalTest}', '${it.name}', '${it.preparation}')") }
        arrayPatientTest.forEach { db?.execSQL("INSERT INTO patientTests (idPatient, idMedicalTest, date, urlResult) VALUES('${it.patient.idPatient}', '${it.medicalTest.idMedicalTest}', '${it.date}', '${it.urlResult}')") }
        arrayRequestedMedicalTest.forEach { db?.execSQL("INSERT INTO requestedPatientTests (idPatient, idSpeciality, date, urlResult) VALUES('${it.patient.idPatient}', '${it.speciality.idSpeciality}', '${it.date}', '${it.urlResult}')") }
        arrayDoctorPlaces.forEach { db?.execSQL("INSERT INTO doctorPlaces VALUES('${it.idDoctorPlace}', '${it.doctorSpeciality.idDoctorSpeciality}', '${it.place.idPlace}')") }
        arrayProvinces.forEach { db?.execSQL("INSERT INTO province VALUES('${it.id}', '${it.name}')") }
        arrayAllProvinces.forEach { province -> province.forEach { db?.execSQL("INSERT INTO city VALUES('${it.id}', '${it.name}', '${it.postalCode}', '${it.idProvince}')") } }
        arrayHCWeightAndHeight.forEach { db?.execSQL("INSERT INTO hcWeightAndHeight (idPatient, date, weight, height, imc, loadedBy) VALUES('${it.patient.idPatient}', '${it.date}', '${it.weight}', '${it.height}', '${it.imc}', '${it.loadedBy}')") }
        arrayHCTemperature.forEach { db?.execSQL("INSERT INTO hcTemperature (idPatient, date, temperature, bodyPart, loadedBy) VALUES('${it.patient.idPatient}', '${it.date}', '${it.temperature}', '${it.bodyPart}', '${it.loadedBy}')") }
        arrayHCHeartFrequency.forEach { db?.execSQL("INSERT INTO hcHeartFrequency (idPatient, date, heartFrequency, method, loadedBy) VALUES('${it.patient.idPatient}', '${it.date}', '${it.heartFrequency}', '${it.method}', '${it.loadedBy}')") }
        arrayHCArterialPressure.forEach { db?.execSQL("INSERT INTO hcArterialPressure (idPatient, date, lowArterialPressure, highArterialPressure, bodyPart, bloodPressureMonitorType, personWhoTookTheTest, loadedBy) VALUES('${it.patient.idPatient}', '${it.date}', '${it.lowArterialPressure}', '${it.highArterialPressure}', '${it.bodyPart}', '${it.bloodPressureMonitorType}', '${it.personWhoTookTheTest}', '${it.loadedBy}')") }
        arrayHCBreathFrequency.forEach { db?.execSQL("INSERT INTO hcBreathFrequency (idPatient, date, breathFrequency, loadedBy) VALUES('${it.patient.idPatient}', '${it.date}', '${it.breathFrequency}', '${it.loadedBy}')") }
        arrayNotifications.forEach { db?.execSQL("INSERT INTO notifications (idPatient, date, title, description, readed) VALUES('${it.patient.idPatient}', '${it.date}', '${it.title}', '${it.description}', '${it.readed}')") }
        arrayNotificationsSettings.forEach { db?.execSQL("INSERT INTO notificationsSettings (idPatient, medicalTestEnabled, medicalTestPush, medicalTestEmail, drugsEnabled, drugsPush, drugsEmail, derivationsEnabled, derivationsPush, derivationsEmail, patientsPortalEnabled, patientsPortalPush, patientsPortalEmail) VALUES ('${it.patient.idPatient}', '${it.medicalTestEnabled}', '${it.medicalTestPush}', '${it.medicalTestEmail}', '${it.drugsEnabled}', '${it.drugsPush}', '${it.drugsEmail}', '${it.derivationsEnabled}', '${it.derivationsPush}', '${it.derivationsEmail}', '${it.patientsPortalEnabled}', '${it.patientsPortalPush}', '${it.patientsPortalEmail}')") }
    }
}