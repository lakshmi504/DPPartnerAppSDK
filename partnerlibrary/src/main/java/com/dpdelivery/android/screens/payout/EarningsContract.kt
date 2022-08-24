package com.dpdelivery.android.screens.payout

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.EarningsRes

/**
 * Created by user on 14/06/21.
 */
interface EarningsContract {
    interface View : BaseView {
        fun showEarningsRes(res: EarningsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getEarningsList(startDate: String, endDate: String)
    }
}