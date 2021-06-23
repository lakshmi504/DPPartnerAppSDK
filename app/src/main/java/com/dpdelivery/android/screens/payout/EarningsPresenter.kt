package com.dpdelivery.android.screens.payout

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 14/06/21.
 */
class EarningsPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : EarningsContract.Presenter {

    var view: EarningsContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: EarningsContract.View?) {
        this.view = view
    }

    override fun getEarningsList(startDate: String, endDate: String) {
        view?.showProgress()
        subscription.add(
            apiService.getPayoutDetails(
                CommonUtils.getLoginToken(),
                startDate = startDate,
                endDate = endDate
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showEarningsRes(res)
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