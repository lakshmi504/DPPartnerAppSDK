package com.dpdelivery.android.model

import androidx.annotation.Keep

@Keep
data class UploadPhotoRes(
        val message: String,
        val success: Boolean
)