package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class SummaryRes(
        val body: Body?,
        val message: String?,
        val success: Boolean?
)
@Keep
data class Body(
        val employeeId: Int?,
        val result: SummaryResult?
)
@Keep
data class SummaryResult(
        val lastMonth: ArrayList<LastMonth?>?,
        val thisMonth: ArrayList<ThisMonth?>?
)
@Keep
data class LastMonth(
        val count: Int?,
        val name: String?
)
@Keep
data class ThisMonth(
        val count: Int?,
        val name: String?
)

