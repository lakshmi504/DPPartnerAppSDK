package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class PartnerDetailsRes(
    val aadhaar: String,
    val active: Boolean,
    val address: Any,
    val altPhone: Any,
    val areas: List<Any>,
    val dateOfBirth: Any,
    val dateOfJoining: Any,
    val email: String,
    val firstname: String,
    val id: Int,
    val lastname: String,
    val legacyTechnicianId: Int,
    val pancard: String,
    val password: String,
    val phone: String,
    val role: String,
    val roles: List<Any>,
    val username: String,
    val zcreated: Long,
    val zcreator: Int,
    val zdeleted: Boolean,
    val zones: List<Zone>,
    val zupdated: Long,
    val zupdater: Int
) {
    data class Zone(
        val id: Int,
        val status: String,
        val zone: Zone
    ) {
        data class Zone(
            val areaList: Any,
            val cityId: Int,
            val id: Int,
            val name: String,
            val parentId: Int
        )
    }
}