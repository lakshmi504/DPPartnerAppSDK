package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class CommonRes(
    val body: Any,
    val message: String,
    val success: Boolean
)