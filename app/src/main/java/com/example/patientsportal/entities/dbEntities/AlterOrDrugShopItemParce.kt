package com.example.patientsportal.entities.dbEntities

import android.os.Parcel
import android.os.Parcelable

data class AlterOrDrugShopItemParce(
    val alterOrDrug: String,
    val id: Int,
    val quantity: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alterOrDrug)
        parcel.writeInt(id)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlterOrDrugShopItemParce> {
        override fun createFromParcel(parcel: Parcel): AlterOrDrugShopItemParce {
            return AlterOrDrugShopItemParce(parcel)
        }

        override fun newArray(size: Int): Array<AlterOrDrugShopItemParce?> {
            return arrayOfNulls(size)
        }
    }
}

