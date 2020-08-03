package com.dpdelivery.android.ui.filteredjobs

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FilteredJobsListPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : FilteredJobsListContract.Presenter {

    var view: FilteredJobsListContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: FilteredJobsListContract.View?) {
        this.view = view
    }

    override fun getDeliveryJobsList(status: String) {
        view?.showProgress()
        subscription.add(
                apiService.getDeliveryJobs(CommonUtils.getLoginToken(), status, pageSize = 10, page = 1)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showDeliveryJobsListRes(res)
                                },
                                { throwable ->
                                    view?.hideProgress()
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun getMoreDeliveryJobsList(page: Int, status: String) {
        view?.showProgress()
        subscription.add(
                apiService.moreDeliveryJobsList(CommonUtils.getLoginToken(), status, pageSize = 10, page = page)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showMoreDeliveryJobsListRes(res)
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