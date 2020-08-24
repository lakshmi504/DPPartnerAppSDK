package com.dpdelivery.android.technicianui.finish

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techres.SparePartsData
import com.dpdelivery.android.model.techres.SubmiPidRes

interface FinishJobContract {
    interface View : BaseView {
        fun showSparePartsRes(res: ArrayList<SparePartsData>)
        fun showFinishJobRes(res: SubmiPidRes)
        fun reSendHappyCodeRes(res: SubmiPidRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getSparePartsList()
        fun reSendHappyCode(jobId: Int)
        fun finishJob(jobId: Int, finishJobIp: FinishJobIp)
    }
}