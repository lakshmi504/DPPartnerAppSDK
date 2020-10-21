package com.dpdelivery.android.model.techres

data class WorkFlowRes(
    val body: Body?,
    val message: String?,
    val success: Boolean?
) {
    data class Body(
        val id: Int?,
        val name: String?,
        val stepsOrder: StepsOrder?,
        val zcreated: String?,
        val zcreator: Int?,
        val zdeleted: Boolean?,
        val zupdated: String?,
        val zupdater: Int?
    ) {
        data class StepsOrder(
            val `1`: X1?,
            val `2`: X2?
        ) {
            data class X1(
                val id: Int?,
                val name: String?,
                val templateOrder: TemplateOrder?
            ) {
                data class TemplateOrder(
                    val `1`: X1?
                ) {
                    data class X1(
                        val elementOrder: ElementOrder?,
                        val getDataUrl: Any?,
                        val id: Int?,
                        val name: String?,
                        val postDataUrl: Any?
                    ) {
                        data class ElementOrder(
                            val `1`: X1?,
                            val `2`: X2?
                        ) {
                            data class X1(
                                val dropdownContents: List<String?>?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )

                            data class X2(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )
                        }
                    }
                }
            }

            data class X2(
                val id: Int?,
                val name: String?,
                val templateOrder: TemplateOrder?
            ) {
                data class TemplateOrder(
                    val `1`: X1?,
                    val `2`: X2?
                ) {
                    data class X1(
                        val elementOrder: ElementOrder?,
                        val getDataUrl: Any?,
                        val id: Int?,
                        val name: String?,
                        val postDataUrl: Any?
                    ) {
                        data class ElementOrder(
                            val `1`: X1?,
                            val `2`: X2?
                        ) {
                            data class X1(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )

                            data class X2(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )
                        }
                    }

                    data class X2(
                        val elementOrder: ElementOrder?,
                        val getDataUrl: Any?,
                        val id: Int?,
                        val name: String?,
                        val postDataUrl: Any?
                    ) {
                        data class ElementOrder(
                            val `1`: X1?,
                            val `2`: X2?,
                            val `3`: X3?,
                            val `4`: X4?
                        ) {
                            data class X1(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: Any?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )

                            data class X2(
                                val dropdownContents: List<String?>?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )

                            data class X3(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )

                            data class X4(
                                val dropdownContents: Any?,
                                val functionName: Any?,
                                val id: Int?,
                                val inputApi: String?,
                                val name: String?,
                                val optional: Boolean?,
                                val showType: String?,
                                val value: Any?,
                                val workflowElementType: String?
                            )
                        }
                    }
                }
            }
        }
    }
}