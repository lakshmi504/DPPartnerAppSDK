package com.dpdelivery.android.technicianui.jobslist

import android.content.Context
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
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

    override fun getAssignedJobsList(status: String, appointmentDate: String) {
        view?.showProgress()
        subscription.add(
                apiService.getAssignedJobs(CommonUtils.getLoginToken(), status, appointmentDate, pageSize = 1, page = 1, orderBy = "appointmentDate")
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

    override fun getJobsList(status: String, appointmentDate: String) {
        view?.showProgress()
        subscription.add(
                apiService.getAssignedJobs(CommonUtils.getLoginToken(), status, appointmentDate, pageSize = 10, page = 1, orderBy = "appointmentDate")
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

    override fun getAssignedJobsList(status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getAssignedJobs(CommonUtils.getLoginToken(), status, pageSize = 10, page = 1, orderBy = "appointmentStartTime")
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

    override fun getMoreJobsList(page: Int, status: String, appointmentDate: String) {
        view?.showProgress()
        subscription.add(
                apiService.getMoreJobsList(CommonUtils.getLoginToken(), status, appointmentDate, pageSize = 10, page = page, orderBy = "appointmentDate")
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

    override fun getMoreJobsList(page: Int, status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getMoreJobsList(CommonUtils.getLoginToken(), status, pageSize = 10, page = page, orderBy = "appointmentStartTime")
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

    override fun getVoipCall(caller: String, receiver: String) {
        view?.showProgress()
        subscription.add(
                apiService.getVoipCall(api_key = context.getString(R.string.call_api_key), method = context.getString(R.string.call_method), caller = caller, receiver = receiver)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    if (res.isSuccessful) {
                                        view?.showVoipRes(res.headers())
                                    }
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