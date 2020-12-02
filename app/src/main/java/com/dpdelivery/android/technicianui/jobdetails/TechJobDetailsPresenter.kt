package com.dpdelivery.android.technicianui.jobdetails

import android.content.Context
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.StartJobIP
import com.dpdelivery.android.model.techinp.SubmitPidIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TechJobDetailsPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : TechJobDetailsContract.Presenter {

    var view: TechJobDetailsContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: TechJobDetailsContract.View?) {
        this.view = view
    }

    override fun getAssignedJob(jobId: Int) {
        view?.showProgress()
        try {
            subscription.add(
                    apiService.getAssignedJobById(CommonUtils.getLoginToken(), jobId = jobId)
                            .subscribeOn(baseScheduler.io())
                            .observeOn(baseScheduler.ui())
                            .subscribe(
                                    { res ->
                                        view?.hideProgress()
                                        view?.showAssignedJobRes(res)
                                    },
                                    { throwable ->
                                        view?.hideProgress()
                                        view?.showErrorMsg(throwable)
                                    }))
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun startJob(jobId: Int, startJobIP: StartJobIP) {
        view?.showProgress()
        subscription.add(
                apiService.startJob(CommonUtils.getLoginToken(), jobId = jobId, startJobIP = startJobIP)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showStartJobRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun submitPid(submitPidIp: SubmitPidIp) {
        view?.showProgress()
        subscription.add(
                apiService.submitPid(CommonUtils.getLoginToken(), submitPidIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showSubmittedPidRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun refreshPidStatus(purifierId: String) {
        view?.showProgress()
        subscription.add(
                apiService.refreshPidStatus(CommonUtils.getLoginToken(), deviceId = purifierId)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showRefreshPidRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun addNote(jobId: Int, updateJobIp: UpdateJobIp) {
        view?.showProgress()
        subscription.add(
                apiService.addNote(CommonUtils.getLoginToken(), jobId = jobId, updateJobIp = updateJobIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showAddNoteRes(res)
                                },
                                { throwable ->
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