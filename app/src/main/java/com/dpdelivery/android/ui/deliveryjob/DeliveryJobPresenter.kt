package com.dpdelivery.android.ui.deliveryjob

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.input.AssignJobsIp
import com.dpdelivery.android.model.input.UpdateAppointmentIp
import com.dpdelivery.android.model.input.UpdateStatusIp
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DeliveryJobPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : DeliveryJobContract.Presenter {

    var view: DeliveryJobContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: DeliveryJobContract.View?) {
        this.view = view
    }

    override fun getDeliveryJob(jodId: Int) {
        view?.showProgress()
        subscription.add(
                apiService.deliveryJobData(CommonUtils.getLoginToken(), jobId = jodId)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showDeliveryJobRes(res)

                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun updateAppointment(updateAppointmentIp: UpdateAppointmentIp) {
        view?.showProgress()
        subscription.add(
                apiService.updateAppointment(CommonUtils.getLoginToken(), updateAppointmentIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    if (res.isSuccessful) {
                                        view?.showUpdateAppointmntRes(res.body()!!)
                                    }
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun updateStatus(updateStatusIp: UpdateStatusIp) {
        view?.showProgress()
        subscription.add(
                apiService.updateStatus(CommonUtils.getLoginToken(), updateStatusIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    if (res.isSuccessful) {
                                        view?.showUpdateStatusRes(res.body()!!)
                                    }
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun getAgentsList() {
        view?.showProgress()
        subscription.add(
                apiService.getAgentsList(CommonUtils.getLoginToken())
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showAgentsListRes(res)
                                },
                                { throwable ->
                                    view?.hideProgress()
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun assignJob(assignJobsIp: AssignJobsIp) {
        view?.showProgress()
        subscription.add(
                apiService.assignJob(CommonUtils.getLoginToken(), assignJobsIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    view?.showAssignJobsRes(res)
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