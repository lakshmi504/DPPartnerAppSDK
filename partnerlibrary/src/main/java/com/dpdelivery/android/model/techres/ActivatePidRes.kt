package com.dpdelivery.android.model.techres

import androidx.annotation.Keep
@Keep
data class ActivatePidRes(
        val result: Result?
)
@Keep
data class Result(
        val purifierstatus: String?,
        val status: String?
)
