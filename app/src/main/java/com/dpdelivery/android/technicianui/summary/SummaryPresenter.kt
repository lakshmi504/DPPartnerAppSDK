package com.dpdelivery.android.technicianui.summary

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SummaryPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : SummaryContract.Presenter {

    var view: SummaryContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: SummaryContract.View?) {
        this.view = view
    }

    override fun getSummary() {
        view?.showProgress()
        subscription.add(
                apiService.getSummary(CommonUtils.getLoginToken())
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showSummaryRes(res)
                                },
                                { throwable ->
                                    view?.hideProgress()
                                    view?.showErrorMsg(throwable)
                                }))
    }
    override fun dropView() {
        subscription.clear()
        this.view = null
    }
}