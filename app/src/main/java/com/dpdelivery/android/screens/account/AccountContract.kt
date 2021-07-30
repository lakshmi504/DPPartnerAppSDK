package com.dpdelivery.android.screens.account

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.PartnerDetailsRes

/**
 * Created by user on 04/06/21.
 */
interface AccountContract {
    interface View : BaseView {
        fun showPartnerDetails(res: PartnerDetailsRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getPartnerDetails()
    }
}