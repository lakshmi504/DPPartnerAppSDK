package com.dpdelivery.android.screens.inventory

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.InventoryRes

/**
 * Created by user on 23/06/21.
 */
interface InventoryContract {
    interface View : BaseView {
        fun showInventoryRes(res: InventoryRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getInventory(id: Int)
    }
}