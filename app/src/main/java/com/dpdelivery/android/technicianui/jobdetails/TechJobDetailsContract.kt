package com.dpdelivery.android.technicianui.jobdetails

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.StartJobIP
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.ActivatePidRes
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.model.techres.StartJobRes

interface TechJobDetailsContract {
    interface View : BaseView {
        fun showAssignedJobRes(res: Job)
        fun showStartJobRes(startJobRes: StartJobRes)
        fun showActivatedPidRes(activatePidRes: ActivatePidRes)
        fun showRefreshPidRes(res: ActivatePidRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJob(jobId: Int)
        fun startJob(jobId: Int, startJobIP: StartJobIP)
        fun activatePid(hashMap: HashMap<String, String>)
        fun refreshPidStatus(hashMap: HashMap<String, String>)
        fun addNote(jobId: Int, updateJobIp: UpdateJobIp)
    }
}