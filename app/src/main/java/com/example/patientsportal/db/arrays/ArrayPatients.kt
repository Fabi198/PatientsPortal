package com.example.patientsportal.db.arrays

import com.example.patientsportal.R
import com.example.patientsportal.entities.dbEntities.Patient

object ArrayPatients {

    val arrayPatients = arrayListOf(
        Patient(338456, "Tomas Alberto", "Messi", "Cuccittini", "M", "HOMBRE", "tomiMessi@gmail.com", "messiT@yahoo.com.ar", "PomeloRockAndRoll", "1996/04/13","DNI", "39610102", "338456", "", R.drawable.profile_img_fabi, "", "", "Argentina", "Terciario completo", "Casa", "Con familiar", "", ""),
        Patient(896214, "Minerva Bolt", "Pontevedra", "Rodriguez", "F", "MUJER", "minervalabreve21@gmail.com", "", "LaBreve", "2021/08/22", "DNI", "99365125", "896214", "Es muy boluda", R.drawable.profile_img_minerva, "", "", "Argentina", "", "Casa", "Con familiar", "", ""),
        Patient(425877, "Juana", "Repetto", "Miranda", "F", "MUJER", "juanaR@gmail.com", "Rjuana@gmail.com", "kolu88", "1994/09/14", "DNI", "38386533", "425877", "", R.drawable.profile_img_ari, "", "", "Argentina", "Universitario completo", "Casa", "", "CRISTIANISMO", ""),
        Patient(568905, "Lucia", "Frederick", "Lux", "F", "MUJER", "lucifrede@yahoo.com.ar", "", "logitech", "1963/03/29", "DNI", "16374601", "568905", "", R.drawable.profile_img_josefina, "", "", "Argentina", "Secundario completo", "Departamento", "Con familiar", "CRISTIANISMO", "")
    )
}