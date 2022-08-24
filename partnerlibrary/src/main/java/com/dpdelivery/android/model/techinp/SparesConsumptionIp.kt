package com.dpdelivery.android.model.techinp

class SparesConsumptionIp : ArrayList<SparesConsumptionIpItem>()
data class SparesConsumptionIpItem(
    val item_id: Int,
    val product_ids: List<Int>? = null,
    val quantity: Int,
    val serializable: Boolean
)
