package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class SyncCommandsUpdatedRes(
    val body: String,
    val failCode: Any,
    val message: Any,
    val success: Boolean
)