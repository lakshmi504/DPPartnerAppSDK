package com.dpdelivery.android.technicianui.jobdetails

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.StartJobIP
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
        subscription.add(
                apiService.getAssignedJobById(CommonUtils.getLoginToken(), jobId = jobId)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showAssignedJobRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
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

    override fun activatePid(hashMap: HashMap<String, String>) {
        view?.showProgress()
        subscription.add(
                apiService.activatePid(hashMap)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.showActivatedPidRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun refreshPidStatus(hashMap: HashMap<String, String>) {
        view?.showProgress()
        subscription.add(
                apiService.refreshPisStatus(hashMap)
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
                                    view?.showStartJobRes(res)
                                },
                                { throwable ->
                                    view?.showErrorMsg(throwable)
                                }))
    }

    override fun dropView() {
        subscription.clear()
        this.view = null
    }
}