package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class AddTextRes(
        val body: Any?,
        val message: String?,
        val success: Boolean?
)