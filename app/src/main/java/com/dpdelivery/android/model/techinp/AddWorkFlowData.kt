package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class AddWorkFlowData(
        val data: ArrayList<Data>?,
        val jobId: Int?
) {
    @Keep
    data class Data(
            val elementId: Int?,
            val value: String?
    )
}