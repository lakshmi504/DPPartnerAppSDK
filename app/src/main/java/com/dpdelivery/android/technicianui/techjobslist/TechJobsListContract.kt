package com.dpdelivery.android.technicianui.techjobslist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.ASGListRes
import okhttp3.Headers

interface TechJobsListContract {

    interface View : BaseView {
        fun showAsgJobsListRes(res: ASGListRes)
        fun showVoipRes(res: Headers)
    }

    interface Presenter : BasePresenter<View> {
        //fun getAssignedJobsList()
        fun getFilterJobsList(status: String, appointmentDate: String)
        fun getSearchJobsList(search: String)
        fun getVoipCall(caller: String, receiver: String)
    }
}