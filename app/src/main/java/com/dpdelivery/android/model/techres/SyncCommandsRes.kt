package com.dpdelivery.android.model

import androidx.annotation.Keep

@Keep
data class SyncCommandsRes(
    val body: List<SyncCommandsResBody>,
    val failCode: Any,
    val message: Any,
    val success: Boolean
)

@Keep
data class SyncCommandsResBody(
    val command: String,
    val deviceCode: String,
    val sequenceNo: Int,
    val status: String
)
