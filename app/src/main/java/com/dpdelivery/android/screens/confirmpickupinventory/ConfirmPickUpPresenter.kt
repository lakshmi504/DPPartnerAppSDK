package com.dpdelivery.android.screens.confirmpickupinventory

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 24/06/21.
 */
class ConfirmPickUpPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : ConfirmPickUpContract.Presenter {

    var view: ConfirmPickUpContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: ConfirmPickUpContract.View?) {
        this.view = view
    }

    override fun confirmInventory(submitInventoryIp: SubmitInventoryIp) {
        view?.showProgress()
        subscription.add(
            apiService.submitInventoryDetails(CommonUtils.getLoginToken(), submitInventoryIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showConfirmedPickUpRes(res)
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