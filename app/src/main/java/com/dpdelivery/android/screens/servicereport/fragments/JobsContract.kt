package com.dpdelivery.android.screens.servicereport.fragments

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.LastJobsRes

/**
 * Created by user on 24/08/21.
 */
interface JobsContract {
    interface View : BaseView {
        fun showJobsRes(res: LastJobsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getJobsList(jobId: Int)
    }
}