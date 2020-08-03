package com.dpdelivery.android.model.input

import androidx.annotation.Keep

@Keep
data class UpdateAppointmentIp(
    val appt: String?,
    val jobId: String?,
    val note: String?
)