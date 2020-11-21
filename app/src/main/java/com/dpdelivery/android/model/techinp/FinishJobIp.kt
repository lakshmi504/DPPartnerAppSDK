package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class FinishJobIp(
        val amountCollected: Float? = null,
        val deviceCode: String? = null,
        val happyCode: String? = null,
        val inputTds: String? = null,
        val jobEndTime: String? = null,
        val status: String? = null,
        val latitude: String? = null,
        val longitude: String? = null,
        val outputTds: String? = null,
        val paymentType: String? = null,
        val spares: List<Int>? = null
)