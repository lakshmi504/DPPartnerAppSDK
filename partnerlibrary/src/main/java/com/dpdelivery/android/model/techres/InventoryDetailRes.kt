package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class InventoryDetailRes(
    val body: InventoryDetailResBody,
    val message: String,
    val success: Boolean
)

@Keep
data class InventoryDetailResBody(
    val item_code: String,
    val item_id: Int,
    val picked: Int,
    val not_picked: Int,
    val to_be_returned: Int,
    val item_name: String,
    val product_info: ArrayList<DetailsProductInfo>,
    val returnable: Boolean,
    val serializable: Boolean
)

@Keep
data class DetailsProductInfo(
    val id: Int,
    val product_code: String
)

