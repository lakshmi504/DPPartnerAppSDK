package com.dpdelivery.android.screens.inventory

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 23/06/21.
 */
class InventoryPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : InventoryContract.Presenter {

    var view: InventoryContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: InventoryContract.View?) {
        this.view = view
    }

    override fun getInventory(id: Int) {
        view?.showProgress()
        subscription.add(
            apiService.getInventoryCount(CommonUtils.getLoginToken(), id = id)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showInventoryRes(res)
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