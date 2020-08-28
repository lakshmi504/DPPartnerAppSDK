package com.dpdelivery.android.ui.deliveryjob

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.*
import com.dpdelivery.android.model.input.AssignJobsIp
import com.dpdelivery.android.model.input.UpdateAppointmentIp
import com.dpdelivery.android.model.input.UpdateStatusIp
import okhttp3.ResponseBody

interface DeliveryJobContract {

    interface View : BaseView {
        fun showDeliveryJobRes(res: DeliveryJobsRes)
        fun showUpdateAppointmntRes(res:String)
        fun showUpdateStatusRes(res:String)
        fun showAgentsListRes(res: List<GetAgentsRes>)
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