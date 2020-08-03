package com.dpdelivery.android.model.input

import androidx.annotation.Keep

@Keep
data class AssignJobsIp(
    val assignTo: String?,
    val jobIds: List<String?>?
)