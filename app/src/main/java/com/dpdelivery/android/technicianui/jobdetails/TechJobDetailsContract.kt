package com.dpdelivery.android.technicianui.jobdetails

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.StartJobIP
import com.dpdelivery.android.model.techinp.SubmitPidIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.*

interface TechJobDetailsContract {
    interface View : BaseView {
        fun showAssignedJobRes(res: Job)
        fun showStartJobRes(startJobRes: StartJobRes)
        fun showSubmittedPidRes(submiPidRes: SubmiPidRes)
        fun showRefreshPidRes(res: PIdStatusRes)
        fun showAddNoteRes(res: StartJobRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJob(jobId: Int)
        fun startJob(jobId: Int, startJobIP: StartJobIP)
        fun submitPid(submitPidIp: SubmitPidIp)
        fun refreshPidStatus(purifierId: String)
        fun addNote(jobId: Int, updateJobIp: UpdateJobIp)
    }
}