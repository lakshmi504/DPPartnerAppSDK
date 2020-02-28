package com.dpdelivery.android.model

data class GetAgentsRes(
        val id: Int?,
        val name: String?
) {
    override fun toString(): String = name!!
}