package com.dpdelivery.android.screens.techjobslist

import android.content.Context
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techinp.UpdateTokenIP
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TechJobsListPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : TechJobsListContract.Presenter {

    var view: TechJobsListContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: TechJobsListContract.View?) {
        this.view = view
    }

    override fun getAssignedJobsList(status: String, appointmentDate: String) {
        view?.showProgress()
        subscription.add(
            apiService.getAssignedJobs(
                CommonUtils.getLoginToken(),
                status,
                appointmentDate,
                pageSize = 25,
                page = 1,
                orderBy = "appointmentDate"
            )
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
                    })
        )
    }

    override fun getSearchJobsList(search: String) {
        view?.showProgress()
        subscription.add(
            apiService.searchTechJobsList(
                CommonUtils.getLoginToken(),
                search = search,
                orderBy = "appointmentStartTime"
            )
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
                    })
        )
    }

    override fun getVoipCall(caller: String, receiver: String) {
        view?.showProgress()
        subscription.add(
            apiService.getVoipCall(
                api_key = context.getString(R.string.call_api_key),
                method = context.getString(R.string.call_method),
                caller = caller,
                receiver = receiver
            )
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
                    })
        )
    }

    override fun updateJob(jobId: Int, finishJobIp: FinishJobIp) {
        view?.showProgress()
        subscription.add(
            apiService.finishJob(CommonUtils.getLoginToken(), jobId, finishJobIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showUpdateJobRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun addNote(jobId: Int, updateJobIp: UpdateJobIp) {
        view?.showProgress()
        subscription.add(
            apiService.addNote(
                CommonUtils.getLoginToken(),
                jobId = jobId,
                updateJobIp = updateJobIp
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showAddNoteRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    //update notification token
   /* override fun updateToken(updateTokenIP: UpdateTokenIP) {
        view?.showProgress()
        subscription.add(
            apiService.updateDeviceToken(
                CommonUtils.getLoginToken(),
                updateTokenIP
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showUpdateTokenRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }*/

    override fun getPartnerDetails() {
        view?.showProgress()
        subscription.add(
            apiService.getPartnerDetails(
                CommonUtils.getLoginToken()
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showPartnerDetails(res)
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