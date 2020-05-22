package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        const val BASE_URL = "http://dev.waterwalaprime.in:8080/"
        const val PRD_BASE_URL = "http://prd.waterwalaprime.in:8090/"

        // Delivery App Apis
        const val BASE_IMAGE_URL = "http://dev.waterwalaprime.in/fileUploads/"
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
        const val JOBS_LIST = "workforce/job/list"
        const val JOB_BY_ID = "workforce/job/"
        const val ACTIVATE_PID = "http://waterwalaprime.in/techapp/controller/activatePurifier.php"
        const val PURIFIER_STATUS = "http://waterwalaprime.in/techapp/controller/getPurifierStatus.php"
    }
}