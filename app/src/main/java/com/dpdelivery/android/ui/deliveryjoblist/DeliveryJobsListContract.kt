package com.dpdelivery.android.ui.deliveryjoblist

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.AssignJobRes
import com.dpdelivery.android.model.AssignJobsIp
import com.dpdelivery.android.model.DeliveryJobsListRes
import com.dpdelivery.android.model.GetAgentsRes

interface DeliveryJobsListContract {

    interface View : BaseView {
        fun showDeliveryJobsListRes(res: DeliveryJobsListRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getDeliveryJobsList()
        fun getSearchJobsList(search: String)
        fun getFilterJobsList(status: String)
    }
}