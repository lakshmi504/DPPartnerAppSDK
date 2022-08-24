package com.dpdelivery.android.screens.inventoryDetails

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.DetailInventoryIp
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.model.techres.CommonRes
import com.dpdelivery.android.model.techres.InventoryDetailRes

/**
 * Created by user on 24/06/21.
 */
interface InventoryDetailsContract {
    interface View : BaseView {
        fun showInventoryDetailsRes(res: InventoryDetailRes)
        fun showRemovedInventoryRes(res: CommonRes)
    }

    interface Presenter : BasePresenter<View> {
        fun getPickedUpInventory(detailInventoryIp: DetailInventoryIp)
        fun getToBePickedUpInventory(detailInventoryIp: DetailInventoryIp)
        fun getReturnedInventory(detailInventoryIp: DetailInventoryIp)
        fun removeInventory(submitInventoryIp: SubmitInventoryIp)
    }
}