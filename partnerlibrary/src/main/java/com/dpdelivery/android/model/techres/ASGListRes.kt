package com.dpdelivery.android.model.techres

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

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
    val workflowId: Int?,
    val installation: Installation?,
    val jobEndTime: Any?,
    val jobStartTime: Any?,
    val legacyJobId: Any,
    val notes: ArrayList<Note?>?,
    val priority: Priority?,
    val spareParts: List<Any>?,
    val status: Status?,
    val jobStatuses: ArrayList<JobStatuses?>?,
    val agentJobStatuses: ArrayList<AgentJobStatuses?>?,
    val type: Type?,
    val customerLatLong: String?,
    val zipColorName: String?,
    val zipColorCode: String?,
    val spareHistory: SpareHistory
)

@Keep
@Parcelize
data class SpareHistory(
    val spareConsumptions: ArrayList<SpareConsumption>
) : Parcelable

@Keep
@Parcelize
data class SpareConsumption(
    val date: String,
    val name: String,
    val reason: String
) : Parcelable

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
    val name: String,
    val phoneNumber: String
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
@Parcelize
data class Note(
    val createdBy: CreatedBy?,
    val createdOn: String?,
    val id: Int?,
    val note: String?
) : Parcelable

@Keep
@Parcelize
data class CreatedBy(
    val id: Int?,
    val name: String?
) : Parcelable

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
data class JobStatuses(
    val code: String?,
    val description: String?,
    val id: Int?
)

@Keep
data class AgentJobStatuses(
    val code: String?,
    val description: String?,
    val id: Int?,
    val reasons: ArrayList<String>?
)

@Keep
data class Type(
    val code: String?,
    val description: String?,
    val id: Int?
)


