package com.dpdelivery.android.api

class ApiConstants {
    companion object {

        const val BASE_URL = "http://dev.waterwalaprime.in:8080/"
        const val LOGIN = "login"
        const val DELIVERY_JOBS_LIST = "delivery/getJobs"
        const val DELIVERY_JOB = "delivery/getJob"
        const val UPDATE_APPOINTMENT = "delivery/editJobAppt"
        const val UPDATE_STATUS = "delivery/editJobStatus"
        const val SEARCH = "delivery/getJobs"
        const val GET_AGENTS = "delivery/getAgents"
        const val ASSIGN_JOB = "delivery/assignJobs"
        const val UPLOAD_PHOTO = "delivery/uploadPhoto"

    }
}