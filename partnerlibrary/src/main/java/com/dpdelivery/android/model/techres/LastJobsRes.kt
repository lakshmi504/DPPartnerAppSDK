package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
class LastJobsRes : ArrayList<LastJobsRes.LastJobsResItem>() {
    @Keep
    data class LastJobsResItem(
        val agentJobStatuses: Any,
        val appointmentEndTime: String,
        val appointmentStartTime: String,
        val assignedTo: AssignedTo,
        val bid: String,
        val connectivity: String,
        val customerAddress: CustomerAddress,
        val customerAltPhone: String,
        val customerEmail: String,
        val customerId: Int,
        val customerLatLong: String,
        val customerName: String,
        val customerPhone: String,
        val deliveryStatus: Any,
        val description: String,
        val deviceStatus: Any,
        val financial: Any,
        val freshdeskTicketId: Int,
        val freshsalesTicketId: Any,
        val happyCode: String,
        val id: Int,
        val installation: Installation,
        val issueId: Any,
        val jobEndTime: String,
        val jobStartTime: String,
        val jobStatuses: Any,
        val legacyJobId: Any,
        val legacyJobTag: Any,
        val newSystemUpdated: Any,
        val notes: List<Note>,
        val oldJobTag: String,
        val priority: Any,
        val spareHistory: SpareHistory,
        val spareParts: List<SparePart>,
        val status: Status,
        val type: Type,
        val workflowData: WorkflowData,
        val workflowId: Any,
        val zcreated: String,
        val zone: String
    ) {
        @Keep
        data class WorkflowData(
            val activationElementId: Int,
            val happyCode: Any,
            val statusElementId: Int,
            val steps: List<Step>,
            val submissionField: String,
            val syncElementId: Int
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
                    val postDataUrl: Any
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

        @Keep
        data class AssignedTo(
            val emailId: String,
            val id: Int,
            val name: String,
            val phoneNumber: String
        )

        @Keep
        data class CustomerAddress(
            val area: Any,
            val city: Any,
            val id: Int,
            val leadId: Any,
            val line1: String,
            val line2: Any,
            val oldArea: String,
            val state: Any,
            val zip: String,
            val zone: Any
        )

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
        data class Note(
            val createdBy: CreatedBy,
            val createdOn: String,
            val id: Int,
            val note: String
        ) {
            @Keep
            data class CreatedBy(
                val emailId: Any,
                val id: Int,
                val name: String,
                val phoneNumber: Any
            )
        }

        @Keep
        data class SpareHistory(
            val spareConsumptions: List<SpareConsumption>
        ) {
            @Keep
            data class SpareConsumption(
                val date: String,
                val name: String,
                val reason: String
            ) {
                override fun toString(): String = name
            }
        }

        @Keep
        data class SparePart(
            val category: String,
            val code: String,
            val cost: Double,
            val customerType: String,
            val id: Int,
            val name: String,
            val status: String
        ) {
            override fun toString(): String = name
        }

        @Keep
        data class Status(
            val code: String,
            val description: String,
            val id: Int,
            val isDelivery: Any
        )

        @Keep
        data class Type(
            val code: String,
            val description: String,
            val id: Int
        )
    }

}