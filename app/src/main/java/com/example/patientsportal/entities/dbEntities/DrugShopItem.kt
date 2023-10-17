package com.example.patientsportal.entities.dbEntities

data class DrugShopItem(
    var drug: Drug = Drug(),
    var isChecked: Boolean = false,
    var quantity: Int = 0
)
