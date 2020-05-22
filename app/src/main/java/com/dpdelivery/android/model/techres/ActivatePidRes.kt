package com.dpdelivery.android.model.techres

data class ActivatePidRes(
        val result: Result?
)

data class Result(
        val purifierstatus: String?,
        val status: String?
)
