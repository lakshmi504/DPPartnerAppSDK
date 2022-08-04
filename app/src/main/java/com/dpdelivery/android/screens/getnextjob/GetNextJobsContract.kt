package com.dpdelivery.android.screens.getnextjob

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.GetNextJobRes
import com.dpdelivery.android.model.techres.PartnerDetailsRes
import okhttp3.Headers

interface GetNextJobsContract {

    interface View : BaseView {
        fun showNextJobRes(res: GetNextJobRes)
        fun showSaveJobResponse(res: GetNextJobRes)
        fun showVoipRes(res: Headers)
        fun showPartnerDetails(res: PartnerDetailsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getNextJob(latitude: String, longitude: String)
        fun saveJobResponse(jobId: Int, response: String)
        fun getVoipCall(caller: String, receiver: String)
        fun getPartnerDetails()
    }
}