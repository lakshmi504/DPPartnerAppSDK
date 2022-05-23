package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        const val BASE_URL = "https://api.drinkprime.in/workforce/"
        const val TEST_BASE_URL = "https://api.staging.drinkprime.in/workforce/"
        const val DEV_BASE_URL = "http://test.waterwalaprime.in:8080/"

        const val LOGIN = "https://api.drinkprime.in/auth/login"
        const val PARTNER_DETAILS = "https://api.drinkprime.in/auth/auth/me"
        const val JOBS_LIST = "workforce/job/list/v2"
        const val JOB_BY_ID = "workforce/job/"
        const val SUBMIT_PID = "device/add"
        const val PURIFIER_STATUS = "device/code/"
        const val SPARE_PARTS = "inventory/spareParts"
        const val FINISH_JOB = "workforce/job/"
        const val SUMMARY = "employee/monthlyReport"

        //const val SYNC = "https://waterwalaprime.com/controller/sync1.php"
        const val SYNC = "https://api.drinkprime.in/payments/command/syncdevice"

        //const val GET_BLE_DETAILS = "https://waterwalaprime.com/controller/getdetailsBLE.php"
        const val GET_BLE_DETAILS = "https://api.drinkprime.in/payments/command/getdetails"
        const val GET_WORK_FLOW_DATA = "workflow/getAppData"
        const val ADD_IMAGE = "workflow/addImage"
        const val ADD_WORK_FLOW = "workflow/addData"
        const val VOIP_CALL = "https://api-voice.kaleyra.com/v1/"
        const val UPDATE_TOKEN = "notification/user"
        const val LAST_3_JOBS = "workforce/getLast3JobsByInstallation/"
        const val CONN_CHECK = "https://api.drinkprime.in/sponsor/device/life/conn-check"


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

        //Auto assignment and routing
        const val GET_NEXT_JOB = "workforce/getNextJob"
        const val SAVE_JOB_RESPONSE = "workforce/saveResponseToJob"
    }
}