package com.dpdelivery.android.screens.confirmpickupinventory

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.model.techres.CommonRes

/**
 * Created by user on 24/06/21.
 */
interface ConfirmPickUpContract {
    interface View : BaseView {
        fun showConfirmedPickUpRes(res: CommonRes)
    }

    interface Presenter : BasePresenter<View> {
        fun confirmInventory(submitInventoryIp: SubmitInventoryIp)
        fun confirmTechInventory(submitInventoryIp: SubmitInventoryIp)
    }
}