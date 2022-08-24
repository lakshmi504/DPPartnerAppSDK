package com.dpdelivery.android.model.techres

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
@Keep
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
