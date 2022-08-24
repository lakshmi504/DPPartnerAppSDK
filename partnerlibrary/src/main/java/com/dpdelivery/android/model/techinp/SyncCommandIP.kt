package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class SyncCommandIP(
    val deviceCode: String,
    val sequenceNos: List<Int>
)