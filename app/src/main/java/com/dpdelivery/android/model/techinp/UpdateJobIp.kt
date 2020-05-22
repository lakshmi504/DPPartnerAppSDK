package com.dpdelivery.android.model.techinp

data class UpdateJobIp(
        val appointmentEndTime: String? = null,
        val appointmentStartTime: String? = null,
        val assignedTo: AssignedTo? = null,
        val description: String? = null,
        val note: String? = null,
        val jobEndTime: String? = null,
        val jobPriority: JobPriority? = null,
        val jobStartTime: String? = null,
        val jobStatus: JobStatus? = null,
        val jobType: JobType? = null
) {
    data class AssignedTo(
            val id: Int=0,
            val name: String? = null
    )

    data class JobPriority(
            val code: String? = null
    )

    data class JobStatus(
            val code: String? = null
    )

    data class JobType(
            val code: String? = null
    )
}