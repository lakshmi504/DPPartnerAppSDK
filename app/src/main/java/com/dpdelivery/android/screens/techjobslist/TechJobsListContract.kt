package com.dpdelivery.android.screens.techjobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techinp.UpdateTokenIP
import com.dpdelivery.android.model.techres.*
import okhttp3.Headers

interface TechJobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showJobsListRes(res: ASGListRes)
        fun showVoipRes(res: Headers)
        fun showUpdateJobRes(res: SubmiPidRes)
        fun showAddNoteRes(res: StartJobRes)
        fun showUpdateTokenRes(res: CommonRes)
        fun showPartnerDetails(res: PartnerDetailsRes)
    }

    interface Presenter : BasePresenter<View> {

        fun getAssignedJobsList(status: String, appointmentDate: String)
        fun getSearchJobsList(search: String)
        fun getVoipCall(caller: String, receiver: String)
        fun updateJob(jobId: Int, finishJobIp: FinishJobIp)
        fun addNote(jobId: Int, updateJobIp: UpdateJobIp)
        fun updateToken(updateTokenIP: UpdateTokenIP)
        fun getPartnerDetails()

    }
}