package com.dpdelivery.android.ui.deliveryjoblist

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DeliveryJobsListPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : DeliveryJobsListContract.Presenter {

    var view: DeliveryJobsListContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: DeliveryJobsListContract.View?) {
        this.view = view
    }

    override fun getDeliveryJobsList() {
        view?.showProgress()
        subscription.add(
                apiService.deliveryJobsList(CommonUtils.getLoginToken())
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

    override fun getSearchJobsList(search: String) {
        view?.showProgress()
        subscription.add(
                apiService.searchDeliveryList(CommonUtils.getLoginToken(), search = search)
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

    override fun getFilterJobsList(status: String) {
        view?.showProgress()
        subscription.add(
                apiService.filterDeliveryList(CommonUtils.getLoginToken(), status = status)
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
    override fun dropView() {
        subscription.clear()
        this.view = null
    }

}