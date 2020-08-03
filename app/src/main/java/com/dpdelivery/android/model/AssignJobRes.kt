package com.dpdelivery.android.model

import androidx.annotation.Keep

@Keep
data class AssignJobRes(
    val error: Any?,
    val success: Boolean?
)