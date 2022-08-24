package com.dpdelivery.android.screens.jobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.StartJobRes
import com.dpdelivery.android.model.techres.SubmiPidRes
import okhttp3.Headers

interface JobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showMoreAsgJobsListRes(res: ASGListRes)
        fun showVoipRes(res: Headers)
        fun showUpdateJobRes(res: SubmiPidRes)
        fun showAddNoteRes(res: StartJobRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJobsList(status: String, appointmentDate: String)
        fun getJobsList(status: String)
        fun getMoreJobsList(page: Int, status: String)
        fun getMoreAsgJobsList(page: Int, status: String, appointmentDate: String)
        fun getVoipCall(caller: String, receiver: String)
        fun updateJob(jobId: Int, finishJobIp: FinishJobIp)
        fun addNote(jobId: Int, updateJobIp: UpdateJobIp)

    }
}