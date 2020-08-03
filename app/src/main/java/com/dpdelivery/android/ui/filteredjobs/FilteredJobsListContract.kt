package com.dpdelivery.android.ui.filteredjobs

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.DeliveryJobsListRes
import com.dpdelivery.android.model.techres.ASGListRes

interface FilteredJobsListContract {

    interface View : BaseView {
        fun showDeliveryJobsListRes(res: DeliveryJobsListRes)
        fun showMoreDeliveryJobsListRes(res: DeliveryJobsListRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getDeliveryJobsList(status: String)
        fun getMoreDeliveryJobsList(page: Int, status: String)
    }
}