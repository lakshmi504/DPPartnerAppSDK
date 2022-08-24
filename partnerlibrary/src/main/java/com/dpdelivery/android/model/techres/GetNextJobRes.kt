package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class GetNextJobRes(
    val body: GetNextJobResBody,
    val message: String,
    val success: Boolean
)

@Keep
data class GetNextJobResBody(
    val agentJobStatuses: List<AgentJobStatuse>,
    val appointmentEndTime: String,
    val appointmentStartTime: String,
    val assignedTo: AssignedTo,
    val bid: String,
    val connectivity: String,
    val customerAddress: CustomerAddress,
    val customerAltPhone: String,
    val customerEmail: String,
    val customerId: Int,
    val customerLatLong: Any,
    val customerName: String,
    val customerPhone: String,
    val deliveryStatus: Any,
    val description: Any,
    val deviceStatus: Any,
    val financial: Any,
    val freshdeskTicketId: Any,
    val freshsalesTicketId: Any,
    val happyCode: Any,
    val id: Int,
    val installation: Installation,
    val issueId: Any,
    val jobEndTime: Any,
    val jobStartTime: Any,
    val jobStatuses: List<JobStatuse>,
    val legacyJobId: Any,
    val legacyJobTag: Any,
    val newSystemUpdated: Any,
    val notes: List<Any>,
    val oldJobTag: String,
    val priority: Any,
    val spareHistory: SpareHistory,
    val spareParts: List<Any>,
    val status: Status,
    val type: Type,
    val workflowData: WorkflowData,
    val workflowId: Int,
    val zcreated: String,
    val zipColorCode: String?,
    val zipColorName: String?,
    val distanceToBeTravelled: String?,
    val durationForTechnician: String?,
    val zone: String,
    val projectedEarning: Int
) {
    @Keep
    data class AgentJobStatuse(
        val code: String,
        val description: String,
        val id: Int,
        val isDelivery: Boolean,
        val isOperations: Boolean,
        val reasons: List<String>
    )

    @Keep
    data class AssignedTo(
        val emailId: String,
        val id: Int,
        val name: String,
        val phoneNumber: String,
        val vehicleNo: Any
    )

    @Keep
    data class CustomerAddress(
        val area: Area?,
        val city: String,
        val id: Int,
        val leadId: Int,
        val line1: String,
        val line2: String,
        val oldArea: String,
        val state: String,
        val zip: String,
        val zone: Zone
    ) {
        @Keep
        data class Area(
            val description: String?,
            val code: String?
        )

        @Keep
        data class Zone(
            val areaList: Any,
            val cityId: Int,
            val id: Int,
            val name: String,
            val parentId: Int
        )
    }

    @Keep
    data class Installation(
        val deviceCode: String,
        val deviceStatus: String,
        val id: Int,
        val plan: Plan
    ) {
        @Keep
        data class Plan(
            val code: String,
            val description: String,
            val id: Int,
            val status: Any
        )
    }

    @Keep
    data class JobStatuse(
        val code: String,
        val description: String,
        val id: Int,
        val isDelivery: Boolean,
        val isOperations: Boolean,
        val reasons: Any
    )

    @Keep
    data class SpareHistory(
        val spareConsumptions: List<SpareConsumption>
    ) {
        @Keep
        data class SpareConsumption(
            val date: String,
            val name: String,
            val reason: String
        )
    }

    @Keep
    data class Status(
        val code: String,
        val description: String,
        val id: Int,
        val isDelivery: Boolean,
        val isOperations: Boolean,
        val reasons: Any
    )

    @Keep
    data class Type(
        val code: String,
        val description: String,
        val id: Int
    )

    @Keep
    data class WorkflowData(
        val activationElementId: Int,
        val happyCode: Any,
        val sparePartId: Int,
        val statusElementId: Int,
        val steps: List<Step>,
        val submissionField: String,
        val syncElementId: Int,
        val wifiBotId: Int,
        val wifiConfigID: Int
    ) {
        @Keep
        data class Step(
            val name: String,
            val templates: List<Template>
        ) {
            @Keep
            data class Template(
                val elements: List<Element>,
                val getDataUrl: Any,
                val id: Int,
                val name: String,
                val postDataUrl: String
            ) {
                @Keep
                data class Element(
                    val dropdownContents: Any,
                    val functionName: Any,
                    val id: Int,
                    val inputApi: String,
                    val name: String,
                    val optional: Boolean,
                    val showType: String,
                    val value: String,
                    val workflowElementType: String
                )
            }
        }
    }
}
