package com.dpdelivery.android.screens.getnextjob

import android.content.Context
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.SaveJobResponseIP
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GetNextJobPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : GetNextJobsContract.Presenter {

    var view: GetNextJobsContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: GetNextJobsContract.View?) {
        this.view = view
    }

    override fun getNextJob(latitude: String, longitude: String) {
        view?.showProgress()
        subscription.add(
            apiService.getNextJobs(
                CommonUtils.getLoginToken(),
                latitude, longitude
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showNextJobRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun saveJobResponse(jobId: Int, response:String) {
        view?.showProgress()
        subscription.add(
            apiService.saveJobResponse(
                CommonUtils.getLoginToken(),
                jobId = jobId,
                response =response
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showSaveJobResponse(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getVoipCall(caller: String, receiver: String) {
        view?.showProgress()
        subscription.add(
            apiService.getVoipCall(
                api_key = context.getString(R.string.call_api_key), method = context.getString(
                    R.string.call_method
                ), caller = caller, receiver = receiver
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