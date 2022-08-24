package com.dpdelivery.android.screens.sync

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.SyncCommandIP
import com.dpdelivery.android.model.techinp.SyncDataIP
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 29/07/22.
 */
class SyncPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : SyncContract.Presenter {

    var view: SyncContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: SyncContract.View?) {
        this.view = view
    }

    override fun updateSyncCommands(data: SyncCommandIP) {
        view?.showProgress()
        subscription.add(
            apiService.updateSyncCommands(
                data = data
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showUpdatedSyncCommandsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun updateSyncPurifierData(data: SyncDataIP) {
        view?.showProgress()
        subscription.add(
            apiService.updateSyncPurifierData(
                data = data
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showUpdatedSyncDataRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun dropView() {
        subscription.clear()
        this.view = null
    }

}