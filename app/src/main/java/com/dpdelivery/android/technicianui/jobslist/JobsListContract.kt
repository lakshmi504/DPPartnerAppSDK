package com.dpdelivery.android.technicianui.jobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.ASGListRes
import okhttp3.Headers

interface JobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showMoreAsgJobsListRes(res: ASGListRes)
        fun showVoipRes(res: Headers)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJobsList(status: String)
        fun getAssignedJobsList(status: String, appointmentDate: String)
        fun getMoreJobsList(page: Int, status: String)
        fun getMoreJobsList(page: Int, status: String, appointmentDate: String)
        fun getVoipCall(caller: String, receiver: String)
    }
}