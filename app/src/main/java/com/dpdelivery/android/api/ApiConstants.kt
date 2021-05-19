package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        //const val BASE_URL = "http://f75175664e67.ngrok.io/"
        const val BASE_URL = "http://staging.waterwalaprime.in:8080/"

        //const val BASE_URL = "http://test.waterwalaprime.in:8080/"
        const val PRD_BASE_URL = "http://prd.waterwalaprime.in:8090/"

        // Delivery App Apis
        const val BASE_IMAGE_URL = "http://staging.waterwalaprime.in/fileUploads/"
        const val LOGIN = "login"
        const val DELIVERY_JOBS_LIST = "delivery/getJobs"
        const val DELIVERY_JOB = "delivery/getJob"
        const val UPDATE_APPOINTMENT = "delivery/editJobAppt"
        const val UPDATE_STATUS = "delivery/editJobStatus"
        const val SEARCH = "delivery/getJobs"
        const val GET_AGENTS = "delivery/getAgents"
        const val ASSIGN_JOB = "delivery/assignJobs"
        const val UPLOAD_PHOTO = "delivery/uploadPhoto"

        //Technician App Apis
        const val JOBS_LIST = "workforce/job/list/v2"
        const val JOB_BY_ID = "workforce/job/"
        const val SUBMIT_PID = "device/add"
        const val PURIFIER_STATUS = "device/code/"
        const val SPARE_PARTS = "inventory/spareParts"
        const val FINISH_JOB = "workforce/job/"
        const val SEND_HAPPY_CODE = "workforce/sendHappyCode"
        const val SUMMARY = "employee/monthlyReport"
        const val SYNC = "https://waterwalaprime.com/controller/sync1.php"
        const val GET_BLE_DETAILS = "https://waterwalaprime.com/controller/getdetailsBLE.php"
        const val GET_WORK_FLOW_DATA = "workflow/getAppData"
        const val ADD_TEXT = "workflow/addText"
        const val ADD_IMAGE = "workflow/addImage"
        const val ADD_WORK_FLOW = "workflow/addData"
        const val VOIP_CALL = "https://api-voice.kaleyra.com/v1/"
    }
}