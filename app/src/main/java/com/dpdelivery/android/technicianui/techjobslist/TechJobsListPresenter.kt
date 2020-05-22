package com.dpdelivery.android.technicianui.techjobslist

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TechJobsListPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : TechJobsListContract.Presenter {

    var view: TechJobsListContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: TechJobsListContract.View?) {
        this.view = view
    }

    override fun getAssignedJobsList() {
        view?.showProgress()
        subscription.add(
                apiService.getAssignedJobs(CommonUtils.getLoginToken())
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showAsgJobsListRes(res)
                                },
                                { throwable ->
                                    view?.hideProgress()
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun getFilterJobsList(status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getFilterJobs(CommonUtils.getLoginToken(),status)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showAsgJobsListRes(res)
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