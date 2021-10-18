package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class WorkFlowDataRes(
        val body: WorkFlowDataResBody?,
        val message: String?,
        val success: Boolean?
) {
    @Keep
    data class WorkFlowDataResBody(
            val steps: ArrayList<Step>?,
            val statusElementId: Int,
            val submissionField: String,
            val activationElementId: Int,
            val syncElementId: Int,
            val sparePartId: Int
    ) {
        @Keep
        data class Step(
                val name: String?,
                val templates: ArrayList<Template>?
        ) {
            @Keep
            data class Template(
                    val elements: ArrayList<Element>?,
                    val name: String?
            ) {
                @Keep
                data class Element(
                        val dropdownContents: ArrayList<String?>?,
                        val functionName: Any?,
                        val id: Int,
                        val inputApi: String,
                        val name: String?,
                        val optional: Boolean? = null,
                        val showType: String?,
                        val value: String? = "",
                        val workflowElementType: String?
                )
            }
        }
    }
}