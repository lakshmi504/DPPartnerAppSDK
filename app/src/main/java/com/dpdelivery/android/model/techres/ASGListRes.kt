package com.dpdelivery.android.model.techres

import com.dpdelivery.android.model.Note

data class ASGListRes(
        val jobs: ArrayList<Job?>?,
        val total: Int?
)

data class Job(
        val appointmentEndTime: String?,
        val appointmentStartTime: String?,
        val assignedTo: Any?,
        val customerAddresses: List<CustomerAddresse?>?,
        val customerAltPhone: String?,
        val customerEmail: String?,
        val customerId: Int?,
        val customerName: String?,
        val customerPhone: String?,
        val description: String?,
        val id: Int?,
        val installation: Installation?,
        val jobEndTime: Any?,
        val jobStartTime: Any?,
        val notes: ArrayList<Note?>?,
        val priority: Priority?,
        val status: Status?,
        val type: Type?
)

data class CustomerAddresse(
        val area: Area?,
        val city: String?,
        val id: Int?,
        val line1: String?,
        val line2: String?,
        val state: String?,
        val zip: String?
)

data class Area(
        val description: Any?,
        val id: Int?,
        val code: String?,
        val status: Any?
)

data class Installation(
        val device: Any?,
        val id: Int?,
        val plan: Plan?,
        val status: Any?
)

data class Plan(
        val code: String?,
        val description: String?,
        val id: Int?,
        val status: Any?
)

data class TechNote(
        val createdBy: CreatedBy?,
        val createdOn: String?,
        val id: Int?,
        val note: String?
)

data class CreatedBy(
        val id: Int?,
        val name: String?
)


data class Priority(
        val code: String?,
        val description: String?,
        val id: Int?
)

data class Status(
        val code: String?,
        val description: String?,
        val id: Int?
)

data class Type(
        val code: String?,
        val description: String?,
        val id: Int?
)

