package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class AddTextIp(
        val elementId: Int?,
        val jobId: Int?,
        val text: String?
)