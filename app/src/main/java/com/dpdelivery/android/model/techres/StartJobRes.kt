package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class StartJobRes(
    val error: Any?,
    val success: Boolean?
)