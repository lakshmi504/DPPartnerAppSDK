package com.dpdelivery.android.screens.summary

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.SummaryRes

interface SummaryContract {

    interface View : BaseView {
        fun showSummaryRes(res: SummaryRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getSummary()
    }
}