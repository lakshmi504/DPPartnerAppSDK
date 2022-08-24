package com.dpdelivery.android.screens.account

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 04/06/21.
 */
class AccountPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : AccountContract.Presenter {

    var view: AccountContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: AccountContract.View?) {
        this.view = view
    }

    override fun getPartnerDetails() {
        view?.showProgress()
        subscription.add(
            apiService.getPartnerDetails(
                CommonUtils.getLoginToken()
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showPartnerDetails(res)
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