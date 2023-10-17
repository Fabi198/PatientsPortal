package com.example.patientsportal.entities

import com.example.patientsportal.entities.dbEntities.PregnancyFeel
import com.example.patientsportal.entities.dbEntities.PregnancyNote
import com.example.patientsportal.entities.dbEntities.PregnancyPhoto

data class PregnancyWeek(
    var numberWeek: Int = 0,
    var pregnancyTestDate: String = "",
    var pregnancyTestText: String = "",
    var pregnancyTestImageUrl: String = "",
    var motherChangesDate: String = "",
    var motherChangesText: String = "",
    var motherChangesImageUrl: String = "",
    var babyDevelopmentDate: String = "",
    var babyDevelopmentText: String = "",
    var babyDevelopmentImageUrl: String = "",
    var listFeels: ArrayList<PregnancyFeel> = ArrayList(),
    var listNotes: ArrayList<PregnancyNote> = ArrayList(),
    var listPhotos: ArrayList<PregnancyPhoto> = ArrayList()
)
