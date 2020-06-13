package com.dpdelivery.android.model.techres

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SparePartsData(
        val code: String,
        val cost: Double,
        val itemid: Int,
        val itemname: String,
        var selected: Boolean = false
) : Parcelable {
    override fun toString(): String = itemname
}
