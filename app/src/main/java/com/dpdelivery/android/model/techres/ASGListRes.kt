package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class ASGListRes(
        val jobs: ArrayList<Job?>?,
        val total: Int?
)
@Keep
data class Job(
        val appointmentEndTime: String?,
        val appointmentStartTime: String?,
        val assignedTo: AssignedTo?,
        val customerAddress: CustomerAddress?,
        val customerAltPhone: String?,
        val customerEmail: String?,
        val customerId: Int?,
        val customerName: String?,
        val customerPhone: String?,
        val deliveryStatus: String?,
        val description: String?,
        val deviceStatus: String?,
        val connectivity: String?,
        val bid: String?,
        val financial: Any?,
        val id: Int?,
        val installation: Installation?,
        val jobEndTime: Any?,
        val jobStartTime: Any?,
        val legacyJobId: Any,
        val notes: ArrayList<TechNote?>?,
        val priority: Priority?,
        val spareParts: List<Any>?,
        val status: Status?,
        val type: Type?
)
@Keep
data class CustomerAddress(
        val area: Area?,
        val city: String?,
        val id: Int?,
        val line1: String?,
        val line2: String?,
        val state: String?,
        val zip: String?,
        val leadId: String?
)
@Keep
data class Area(
        val description: String?,
        val code: String?
)
@Keep
data class AssignedTo(
        val id: Int,
        val name: String
)
@Keep
data class Installation(
        val deviceCode: String? = "",
        val id: Int?,
        val plan: Plan?,
        val deviceStatus: String?,
        val bId: String?,
        val connectivity: String?
)
@Keep
data class Plan(
        val code: String?,
        val description: String?,
        val id: Int?,
        val status: Any?
)
@Keep
data class TechNote(
        val createdBy: CreatedBy?,
        val createdOn: String?,
        val id: Int?,
        val note: String?
)
@Keep
data class CreatedBy(
        val id: Int?,
        val name: String?
)

@Keep
data class Priority(
        val code: String?,
        val description: String?,
        val id: Int?
)
@Keep
data class Status(
        val code: String?,
        val description: String?,
        val id: Int?
)
@Keep
data class Type(
        val code: String?,
        val description: String?,
        val id: Int?
)

