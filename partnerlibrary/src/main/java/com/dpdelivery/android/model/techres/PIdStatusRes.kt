package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class PIdStatusRes(
    val code: String,
    val description: String,
    val id: Int
)