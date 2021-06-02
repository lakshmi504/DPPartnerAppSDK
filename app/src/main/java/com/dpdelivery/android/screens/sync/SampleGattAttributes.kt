package com.dpdelivery.android.screens.sync

import java.util.*

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
object SampleGattAttributes {
    private val attributes: HashMap<String?, String?> = HashMap<String?, String?>()
    var HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    const val FLOW_LIMIT = "72ab5fa5-6d72-4104-9c2a-364d256064c3"
    const val CURRENT_LITERS = "545439a9-3b86-449a-a15a-adc14155c4f3"
    const val PPL = "c2e911ad-4c0b-4564-a6fe-8a5d0b09fc8f"
    const val VALIDITY = "fd0706e2-3c2a-4fed-9470-a6b35c14b90d"
    const val PRIME_SERVICE = "68caa2fb-aee8-46b8-84fc-ff2791b391ba"
    const val NOTIFICATION = "fccd365c-5fd5-4b66-b350-d8b6fdfd6b1d"
    const val COMMAND = "51578d5e-779a-4e25-983d-6645bb44e743"
    fun lookup(uuid: String?, defaultName: String): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    init {
        // Sample Services.
        attributes["0000180d-0000-1000-8000-00805f9b34fb"] = "Heart Rate Service"
        attributes["0000180a-0000-1000-8000-00805f9b34fb"] = "Device Information Service"
        // Sample Characteristics.
        attributes[HEART_RATE_MEASUREMENT] = "Heart Rate Measurement"
        attributes["00002a29-0000-1000-8000-00805f9b34fb"] = "Manufacturer Name String"
        attributes["68caa2fb-aee8-46b8-84fc-ff2791b391ba"] = "Prime Service"
        attributes["545439a9-3b86-449a-a15a-adc14155c4f3"] = "Current Liters"
        attributes["72ab5fa5-6d72-4104-9c2a-364d256064c3"] = "Add Liters"
        attributes["c2e911ad-4c0b-4564-a6fe-8a5d0b09fc8f"] = "PPL"
        attributes["fd0706e2-3c2a-4fed-9470-a6b35c14b90d"] = "Validity"
    }
}