package com.dpdelivery.android.technicianui.techjobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.StartJobRes
import com.dpdelivery.android.model.techres.SubmiPidRes
import okhttp3.Headers

interface TechJobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showJobsListRes(res: ASGListRes)
        fun showVoipRes(res: Headers)
        fun showUpdateJobRes(res: SubmiPidRes)
        fun showAddNoteRes(res: StartJobRes)
    }

    interface Presenter : BasePresenter<View> {
        //fun getAssignedJobsList()
        fun getFilterJobsList(status: String, appointmentDate: String)
        fun getAssignedJobsList(status: String, appointmentDate: String)
        fun getSearchJobsList(search: String)
        fun getVoipCall(caller: String, receiver: String)
        fun updateJob(jobId: Int, finishJobIp: FinishJobIp)
        fun addNote(jobId: Int, updateJobIp: UpdateJobIp)

    }
}