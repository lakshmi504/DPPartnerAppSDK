package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class InventoryRes(
    val employee_first_name: String,
    val employee_last_name: String,
    val ongoing_return: Int,
    val part_info: ArrayList<PartInfo>,
    val parts_quantity: Int,
    val return_info: List<ReturnInfo>,
    val returned: Int,
    val total_parts: Int
)

@Keep
data class PartInfo(
    val item_code: String,
    val item_id: Int,
    val picked: Int,
    val not_picked: Int,
    val to_be_returned: Int,
    val item_name: String,
    val product_info: ArrayList<ProductInfo>,
    val returnable: Boolean,
    val serializable: Boolean,
    var mycart: Int = 0,
    var tempMyCart: Int = -1
)

@Keep
data class ProductInfo(
    val id: Int,
    val product_code: Any
)

@Keep
data class ReturnInfo(
    val item_code: String,
    val item_id: Int,
    val item_name: String,
    val product_info: List<ProductInfo>,
    val returnable: Boolean,
    val serializable: Boolean
)


