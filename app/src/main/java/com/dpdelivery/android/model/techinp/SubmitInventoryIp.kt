package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class SubmitInventoryIp(
    val employee_id: Int,
    val product_id: Int,
    val qr_code: Any? = null
)