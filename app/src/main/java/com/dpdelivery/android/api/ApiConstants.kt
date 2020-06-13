package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        //const val BASE_URL = "http://ec2-13-127-87-159.ap-south-1.compute.amazonaws.com:8080/"
        const val BASE_URL = "http://dev.waterwalaprime.in:8080/"
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
        const val JOBS_LIST = "workforce/job/list"
        const val JOB_BY_ID = "workforce/job/"
        const val SUBMIT_PID = "device/add"
        const val PURIFIER_STATUS = "device/code/"
        const val SPARE_PARTS = "inventory/spareParts"
        const val FINISH_JOB = "workforce/job/"
    }
}