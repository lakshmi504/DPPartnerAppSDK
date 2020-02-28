package com.dpdelivery.android.ui.deliveryjob

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.*
import okhttp3.ResponseBody

interface DeliveryJobContract {

    interface View : BaseView {
        fun showDeliveryJobRes(res: DeliveryJobsRes)
        fun showUpdateAppointmnt(responseBody: ResponseBody)
        fun showUpdateStatus(responseBody: ResponseBody)
        fun showAgentsList(res: List<GetAgentsRes>)
        fun showAssignJobsRes(res: AssignJobRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getDeliveryJob(jodId: Int)
        fun updateAppointment(updateAppointmentIp: UpdateAppointmentIp)
        fun updateStatus(updateStatusIp: UpdateStatusIp)
        fun getAgentsList()
        fun assignJob(assignJobsIp: AssignJobsIp)
    }
}