package com.example.patientsportal.db.arrays

import com.example.patientsportal.db.arrays.ArrayPatients.arrayPatients
import com.example.patientsportal.entities.dbEntities.Address
import com.example.patientsportal.entities.dbEntities.ContactPerson
import com.example.patientsportal.entities.dbEntities.NotificationsSettings
import com.example.patientsportal.entities.dbEntities.PhonesPatient

object ArrayPatientRelations {

    val arrayAddresses = arrayOf(
        Address(arrayPatients[0], "Gorriti", "2511", "", "", "", "1651", "Argentina", "Buenos Aires", "SAN ANDRES"),
        Address(arrayPatients[1], "Corrientes", "1358", "", "", "Porton verde", "1414", "Argentina", "Ciudad Autónoma de Buenos Aires", "PALERMO"),
        Address(arrayPatients[2], "Cordoba", "1358", "", "", "Porton verde", "1414", "Argentina", "Ciudad Autónoma de Buenos Aires", "PALERMO"),
        Address(arrayPatients[3], "Dorrego", "212", "4", "C", "", "1206", "Argentina", "Ciudad Autónoma de Buenos Aires", "ALMAGRO"),
    )

    val arrayBillAddresses = arrayOf(
        Address(arrayPatients[0], "", "", "", "", "", "", "", "", ""),
        Address(arrayPatients[1], "", "", "", "", "", "", "", "", ""),
        Address(arrayPatients[2], "", "", "", "", "", "", "", "", ""),
        Address(arrayPatients[3], "", "", "", "", "", "", "", "", ""),
    )

    val arrayContactPersons = arrayOf(
        ContactPerson(arrayPatients[0], "Fernanda", "Ruper", "Novia", "Corrientes 1360", "1134686915"),
        ContactPerson(arrayPatients[1], "", "", "", "", ""),
        ContactPerson(arrayPatients[2], "Ricardo", "Andujar", "Novio", "Gorriti 2511", "1134729578"),
        ContactPerson(arrayPatients[3], "Lucas", "Montenegro", "Esposo", "Cordoba 212", "")
    )

    val arrayPhonesPatients = arrayOf(
        PhonesPatient(arrayPatients[0], "44742298", "", "", "1134269118", "MOVISTAR", "48724698", ""),
        PhonesPatient(arrayPatients[1], "", "", "", "1131788955", "MOVISTAR", "", ""),
        PhonesPatient(arrayPatients[2], "", "", "", "", "", "", ""),
        PhonesPatient(arrayPatients[3], "22607742", "", "", "1131559702", "CLARO", "", ""),
    )

    val arrayNotificationsSettings = arrayOf(
        NotificationsSettings(0, arrayPatients[0], "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", "true", "true", "false"),
        NotificationsSettings(0, arrayPatients[1], "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", "true", "true", "false"),
        NotificationsSettings(0, arrayPatients[2], "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", "true", "true", "false"),
        NotificationsSettings(0, arrayPatients[3], "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", 0, 0, 0, 0, 0, "true", "true", "true", "true", "true", "false"),
    )
}