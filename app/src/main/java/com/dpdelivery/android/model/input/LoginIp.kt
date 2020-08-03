package com.dpdelivery.android.model.input

import androidx.annotation.Keep

@Keep
data class LoginIp(
        val password: String?,
        val username: String?
)