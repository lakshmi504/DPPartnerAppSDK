package com.dpdelivery.android.model

import androidx.annotation.Keep

@Keep
data class GetAgentsRes(
        val id: Int?,
        val name: String?
) {
    override fun toString(): String = name!!
}