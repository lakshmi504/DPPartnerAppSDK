package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class UpdateTokenIP(
    val deviceToken: String,
    val userName: String
)