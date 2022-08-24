package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class BIDStatusRes(
    val body: Any,
    val message: String,
    val failCode: Int,
    val success: Boolean
)