package com.example.patientsportal.db.arrays

import com.example.patientsportal.db.arrays.ArrayPatients.arrayPatients
import com.example.patientsportal.entities.dbEntities.Notification

object ArrayNotifications {

    val arrayNotifications = arrayOf(
        Notification(0, arrayPatients[3], "2021/03/05", "Estudios", "Tu solicitud de ENDOSCOPIA DIGESTIVA BAJA está proxima a vencer", "No"),
        Notification(0, arrayPatients[3], "2021/03/13", "Estudios", "Tu solicitud de LABORATORIO CENTRAL está proxima a vencer", "No"),
        Notification(0, arrayPatients[3], "2020/09/12", "Estudios", "Tu solicitud de CENTRAL DE EMERGENCIAS DE ADULTOS está proxima a vencer", "No"),
        Notification(0, arrayPatients[3], "2023/08/29", "Estudios", "Nuevo resultado de SERVICIO DE CARDIOLOGIA", "No"),
        Notification(0, arrayPatients[3], "2021/04/16", "Portal de Salud", "Durante el fin de semana estaremos actualizando el sistema de teleconsultas que afectara principalmente a dispositivos moviles. Te recomendamos que utilices una computadora para acceder a tu portal", "No"),
        Notification(0, arrayPatients[3], "2020/09/23", "Portal de Salud", "Nuevo rediseño de teleconsultas para version escritorio y android, accede a tu postal web y mira el instructivo. Actualiza la aplicacion para ver los cambios", "No"),
        Notification(0, arrayPatients[3], "2021/04/13", "Medicamentos", "Se renovó exitosamente tu receta de HYPERSOL", "No"),
        Notification(0, arrayPatients[3], "2021/03/16", "Medicamentos", "Se renovó exitosamente tu receta de NOVOPREP", "No"),
        Notification(0, arrayPatients[3], "2021/03/16", "Medicamentos", "Se renovó exitosamente tu receta de ANEMIDOX-FERRUM", "No"),
    )
}