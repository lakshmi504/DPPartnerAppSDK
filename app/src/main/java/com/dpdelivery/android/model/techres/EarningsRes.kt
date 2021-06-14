package com.dpdelivery.android.model.techres

data class EarningsRes(
    val count: Int,
    val entryWMList: ArrayList<EntryWM>
)

data class EntryWM(
    val amount: Double,
    val date: String,
    val description: String,
    val credit: Boolean,
    val id: Int
)
