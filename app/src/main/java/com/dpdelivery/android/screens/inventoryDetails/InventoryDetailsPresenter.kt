package com.dpdelivery.android.screens.inventoryDetails

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.DetailInventoryIp
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 24/06/21.
 */
class InventoryDetailsPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : InventoryDetailsContract.Presenter {

    var view: InventoryDetailsContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: InventoryDetailsContract.View?) {
        this.view = view
    }

    override fun getPickedUpInventory(detailInventoryIp: DetailInventoryIp) {
        view?.showProgress()
        subscription.add(
            apiService.pickedUpInventoryDetails(CommonUtils.getLoginToken(), detailInventoryIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showInventoryDetailsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getToBePickedUpInventory(detailInventoryIp: DetailInventoryIp) {
        view?.showProgress()
        subscription.add(
            apiService.toBePickedUpInventoryDetails(CommonUtils.getLoginToken(), detailInventoryIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showInventoryDetailsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getReturnedInventory(detailInventoryIp: DetailInventoryIp) {
        view?.showProgress()
        subscription.add(
            apiService.returnedInventoryDetails(CommonUtils.getLoginToken(), detailInventoryIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showInventoryDetailsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun removeInventory(submitInventoryIp: SubmitInventoryIp) {
        view?.showProgress()
        subscription.add(
            apiService.cancelInventoryDetails(CommonUtils.getLoginToken(), submitInventoryIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showRemovedInventoryRes(res)
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