package com.example.patientsportal.db.arrays

import com.example.patientsportal.db.arrays.ArrayDoctors.arrayDoctors
import com.example.patientsportal.db.arrays.ArrayDrugs.arrayDrugs
import com.example.patientsportal.db.arrays.ArrayPatients.arrayPatients
import com.example.patientsportal.entities.dbEntities.Prescription

object ArrayPrescriptions {

    val arrayPrescriptions = arrayOf(
        Prescription(1, arrayPatients[3], arrayDoctors[11], arrayDrugs[0], "Expired"),
        Prescription(2, arrayPatients[3], arrayDoctors[7], arrayDrugs[1], "Expired"),
        Prescription(3, arrayPatients[3], arrayDoctors[26], arrayDrugs[2], "Expired"),
        Prescription(4, arrayPatients[3], arrayDoctors[26], arrayDrugs[3], "Expired"),
        Prescription(5, arrayPatients[3], arrayDoctors[14], arrayDrugs[4], "Expired"),
        Prescription(6, arrayPatients[3], arrayDoctors[7], arrayDrugs[5], "Expired"),
        Prescription(7, arrayPatients[3], arrayDoctors[42], arrayDrugs[6], "Expired"),
        Prescription(8, arrayPatients[3], arrayDoctors[42], arrayDrugs[7], "Expired"),
        Prescription(9, arrayPatients[3], arrayDoctors[42], arrayDrugs[8], "Expired"),
        Prescription(10, arrayPatients[3], arrayDoctors[42], arrayDrugs[9], "Expired"),
        Prescription(11, arrayPatients[3], arrayDoctors[42], arrayDrugs[10], "Expired"),
        Prescription(12, arrayPatients[3], arrayDoctors[42], arrayDrugs[11], "Expired"),
        Prescription(13, arrayPatients[3], arrayDoctors[42], arrayDrugs[12], "Expired")
    )
}