package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        const val BASE_URL = "https://api.drinkprime.in/workforce/"
        const val TEST_BASE_URL = "https://api.staging.drinkprime.in/workforce/"
        const val DEV_BASE_URL = "http://test.waterwalaprime.in:8080/"

        const val LOGIN = "http://ec2-15-206-189-98.ap-south-1.compute.amazonaws.com:8084/login"
        const val PARTNER_DETAILS =
            "http://ec2-15-206-189-98.ap-south-1.compute.amazonaws.com:8084/auth/me"
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

        //employee earnings
        const val EARNINGS = "earnings/entries"

        //inventory Api's
        const val INVENTORY_COUNT = "employee/app/inventory/"
        const val INVENTORY_PICKED_UP = "employee/Inventory/item/picked"
        const val INVENTORY_TO_BE_PICKED_UP = "employee/Inventory/item/notPicked"
        const val INVENTORY_TO_BE_RETURNED = "employee/returnInventory/item"
        const val SUBMIT_INVENTORY = "inventory/submitQrDetails"
        const val CANCEL_INVENTORY = "inventory/cancelQr"
        const val SUBMIT_TECH_INVENTORY = "inventory/submitQrDetailsForTechnician"
    }
}