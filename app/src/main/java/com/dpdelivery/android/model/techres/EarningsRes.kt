package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class EarningsRes(
    val count: Int,
    val credit: Double,
    val debit: Double,
    val totalAmount: Double,
    val entryWMList: ArrayList<EntryWM>
)

@Keep
data class EntryWM(
    val amount: Double,
    val date: String,
    val description: String,
    val credit: Boolean,
    val id: Int
)
