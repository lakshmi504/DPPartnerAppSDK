package com.dpdelivery.android.technicianui.jobslist

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import retrofit2.http.Query
import javax.inject.Inject

class JobsListPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : JobsListContract.Presenter {

    var view: JobsListContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: JobsListContract.View?) {
        this.view = view
    }

    override fun getAssignedJobsList(status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getAssignedJobs(CommonUtils.getLoginToken(), status, pageSize = 10, page = 1, orderDir = "asc", orderBy = "appointmentStartTime")
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

    override fun getMoreJobsList(page: Int, status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getMoreJobsList(CommonUtils.getLoginToken(), status, pageSize = 10, page = page, orderDir = "asc", orderBy = "appointmentStartTime")
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showMoreAsgJobsListRes(res)
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