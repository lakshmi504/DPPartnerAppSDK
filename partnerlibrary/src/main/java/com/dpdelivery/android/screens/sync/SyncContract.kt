package com.dpdelivery.android.screens.sync

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.SyncCommandsUpdatedRes
import com.dpdelivery.android.model.techinp.SyncCommandIP
import com.dpdelivery.android.model.techinp.SyncDataIP

/**
 * Created by user on 29/07/22.
 */
interface SyncContract {
    interface View : BaseView {
        fun showUpdatedSyncCommandsRes(res: SyncCommandsUpdatedRes)
        fun showUpdatedSyncDataRes(res: SyncCommandsUpdatedRes)
    }

    interface Presenter : BasePresenter<View> {
        fun updateSyncCommands(data: SyncCommandIP)
        fun updateSyncPurifierData(data: SyncDataIP)
    }
}