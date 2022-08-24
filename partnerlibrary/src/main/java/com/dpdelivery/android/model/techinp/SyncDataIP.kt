package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class SyncDataIP(
    val consumedLiters: Int,
    val deviceCode: String,
    val inputTDS: Int,
    val latitude: String,
    val longitude: String,
    val outputTDS: Int,
    val status: Int,
    val totalLiters: Int,
    val validUpto: String
)