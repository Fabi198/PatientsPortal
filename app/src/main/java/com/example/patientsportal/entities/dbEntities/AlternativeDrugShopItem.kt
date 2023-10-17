package com.example.patientsportal.entities.dbEntities

data class AlternativeDrugShopItem(
    var alternativeDrug: AlternativeDrug = AlternativeDrug(),
    var isChecked: Boolean = false,
    var quantity: Int = 0
)
