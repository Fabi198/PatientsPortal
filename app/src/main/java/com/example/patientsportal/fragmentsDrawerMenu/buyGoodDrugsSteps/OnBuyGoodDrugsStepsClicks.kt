package com.example.patientsportal.fragmentsDrawerMenu.buyGoodDrugsSteps

interface OnBuyGoodDrugsStepsClicks {

    fun onClickNextStep2To3(sot: Boolean, place: String, cellphone: String, hour: String, date: String)

    fun onQueryForShoppingCartItemsQuantity(size: Int)

    fun onDeleteDrugShopItem(newSize: Int)

    fun onClickSelectPlace()

    fun onClickAddMoreProducts()

    fun onClickStartPayment()
    fun onClickCancelOrder()
    fun onClickConfirmPayment()
}