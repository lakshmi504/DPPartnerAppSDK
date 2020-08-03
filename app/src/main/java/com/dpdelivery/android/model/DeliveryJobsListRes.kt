package com.dpdelivery.android.model

import androidx.annotation.Keep

@Keep
data class DeliveryJobsListRes(
        val `data`: ArrayList<Data?>?,
        val total: Int?
)
@Keep
data class Data(
        val appointmentAt: String?,
        val assignedAt: String?,
        val assignedTo: AssignedTo?,
        val custAddress: String?,
        val custAltPhone: String?,
        val custArea: String?,
        val custEmail: String?,
        val custName: String?,
        val custPhone: String?,
        val customerId: Int?,
        val deliveredAt: Any?,
        val extOrderId: String?,
        val id: Int?,
        val notes: ArrayList<DeliNote?>?,
        val orderId: Int?,
        val payAmount: Double?,
        val payType: PayTypeList?,
        val pickedUpAt: Any?,
        val status: Status?,
        var selected: Boolean = false,
        val type: Any?
)
@Keep
data class AssignedTo(
        val id: Int?,
        val name: String?
)
@Keep
data class DeliNote(
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
data class PayTypeList(
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

