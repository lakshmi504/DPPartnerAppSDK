package com.dpdelivery.android.screens.finish

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.HomeIP
import com.dpdelivery.android.model.techinp.SyncIP
import com.dpdelivery.android.model.techres.AddTextRes
import com.dpdelivery.android.model.techres.BLEDetailsRes
import com.dpdelivery.android.model.techres.SparePartsData
import com.dpdelivery.android.model.techres.SubmiPidRes

interface FinishJobContract {
    interface View : BaseView {
        fun showSparePartsRes(res: ArrayList<SparePartsData>)
        fun showFinishJobRes(res: SubmiPidRes)
        fun showPidDetailsRes(res: BLEDetailsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getSparePartsList()
        fun finishJob(jobId: Int, finishJobIp: FinishJobIp)
        fun getPidDetails(homeIP: HomeIP)
    }
}