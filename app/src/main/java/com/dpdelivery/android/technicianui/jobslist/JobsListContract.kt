package com.dpdelivery.android.technicianui.jobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.ASGListRes

interface JobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showMoreAsgJobsListRes(res: ASGListRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJobsList(status: String)
        fun getMoreJobsList(page: Int,status: String)
    }
}