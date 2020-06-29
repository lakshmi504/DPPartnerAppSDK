package com.dpdelivery.android.ui.deliveryjoblist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.DeliveryJobsListRes

interface DeliveryJobsListContract {

    interface View : BaseView {
        fun showDeliveryJobsListRes(res: DeliveryJobsListRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getDeliveryJobsList()
        fun getMoreDeliveryJobsList(page: Int)
        fun getSearchJobsList(search: String)
        fun getFilterJobsList(status: String)
    }
}