package com.dpdelivery.android.model.techinp

data class FinishJobIp(
        val amountCollected: Float,
        val deviceCode: String,
        val happyCode: String,
        val inputTds: String,
        val jobEndTime: String,
        val status: String,
        val latitude: String,
        val longitude: String,
        val outputTds: String,
        val paymentType: String,
        val spares: List<Int>
)