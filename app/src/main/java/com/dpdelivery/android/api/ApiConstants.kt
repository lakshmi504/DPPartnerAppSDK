package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        const val BASE_URL = "http://staging.waterwalaprime.in:8080/"
        //const val TEST_BASE_URL = "http://8d00335057d7.ngrok.io/"
        const val TEST_BASE_URL = "http://test.waterwalaprime.in:8080/"

        const val LOGIN = "http://auth-dev.waterwalaprime.in:8084/login"
       // const val LOGIN = "login"
        const val JOBS_LIST = "workforce/job/list/v2"
        const val JOB_BY_ID = "workforce/job/"
        const val SUBMIT_PID = "device/add"
        const val PURIFIER_STATUS = "device/code/"
        const val SPARE_PARTS = "inventory/spareParts"
        const val FINISH_JOB = "workforce/job/"
        const val SUMMARY = "employee/monthlyReport"
        const val SYNC = "https://waterwalaprime.com/controller/sync1.php"
        const val GET_BLE_DETAILS = "https://waterwalaprime.com/controller/getdetailsBLE.php"
        const val GET_WORK_FLOW_DATA = "workflow/getAppData"
        const val ADD_IMAGE = "workflow/addImage"
        const val ADD_WORK_FLOW = "workflow/addData"
        const val VOIP_CALL = "https://api-voice.kaleyra.com/v1/"
        const val UPDATE_TOKEN = "notification/user"
        const val PARTNER_DETAILS = "auth/me"

        //employee earnings
        const val EARNINGS = "earnings/entries"

        //inventory Api's
        const val INVENTORY = "/employee/inventory/"
        const val INVENTORY_COUNT = "/employee/app/inventory/"
        const val INVENTORY_PICKED_UP = "employee/Inventory/item/picked"
        const val INVENTORY_TO_BE_PICKED_UP = "employee/Inventory/item/notPicked"
        const val INVENTORY_TO_BE_RETURNED = "employee/returnInventory/item"
        const val SUBMIT_INVENTORY = "inventory/submitQrDetails"
        const val CANCEL_INVENTORY = "inventory/cancelQr"
    }
}