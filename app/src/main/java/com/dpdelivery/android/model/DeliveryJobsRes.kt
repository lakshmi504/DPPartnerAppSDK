package com.dpdelivery.android.model

data class DeliveryJobsRes(
        val appointmentAt: String?,
        val assignedAt: String?,
        val assignedTo: AssignedToJob?,
        val custAddress: String?,
        val custAltPhone: String?,
        val custArea: String?,
        val custEmail: String?,
        val custName: String?,
        val custPhone: String?,
        val customerId: Int?,
        val deliveredAt: Any?,
        val extOrderId: String?,
        val payImage: String?,
        val deliveryImage: String?,
        val id: Int?,
        val notes: ArrayList<Note?>?,
        val orderId: Int?,
        val payAmount: Double?,
        val payType: PayType?,
        val pickedUpAt: Any?,
        val status: DeliveryJobsResStatus?,
        val type: PayType?
)

data class Note(
        val createdBy: JobCreatedBy?,
        val createdOn: String?,
        val id: Int?,
        val note: String?
)

data class JobCreatedBy(
        val id: Int?,
        val name: String?
)

data class AssignedToJob(
        val id: Int?,
        val name: String?
)

data class PayType(
        val code: String?,
        val description: String?,
        val id: Int?
)

data class DeliveryJobsResStatus(
        val code: String?,
        val description: String?,
        val id: Int?
)
