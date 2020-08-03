package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class SubmitPidIp(
    val deviceCode: String,
    val jobId: Int
)