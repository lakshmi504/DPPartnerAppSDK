package com.dpdelivery.android.model.input

import androidx.annotation.Keep

@Keep
data class UpdateStatusIp(
        val jobId: Int?,
        val status: String?,
        val note: String?,
        val payAmount: String? = null,
        val payType: String? = null,
        val latLong: String? = null,
        val payImage: String? = null,
        val deliveryImage: String? = null
)