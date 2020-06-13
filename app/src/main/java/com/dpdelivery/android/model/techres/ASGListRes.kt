package com.dpdelivery.android.model.techres

data class ASGListRes(
        val jobs: ArrayList<Job?>?,
        val total: Int?
)

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
        val deliveryStatus: Any?,
        val description: String?,
        val deviceStatus: Any?,
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

data class CustomerAddress(
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
        val status: Any?,
        val zipCode: String?,
        val state: String?,
        val city: City?
)

data class AssignedTo(
        val id: Int,
        val name: String
)

data class Installation(
        val deviceCode: String? = "",
        val id: Int?,
        val plan: Plan?,
        val deviceStatus: String?
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

data class City(
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

