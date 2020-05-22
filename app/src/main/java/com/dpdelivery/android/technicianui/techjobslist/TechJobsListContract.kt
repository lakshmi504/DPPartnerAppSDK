package com.dpdelivery.android.technicianui.techjobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.ASGListRes

interface TechJobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getAssignedJobsList()
        fun getFilterJobsList(status: String)
    }
}