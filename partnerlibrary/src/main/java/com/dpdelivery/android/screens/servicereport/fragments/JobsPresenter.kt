package com.dpdelivery.android.screens.servicereport.fragments

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by user on 24/08/21.
 */
class JobsPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : JobsContract.Presenter {

    var view: JobsContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: JobsContract.View?) {
        this.view = view
    }

    override fun dropView() {
        subscription.clear()
        this.view = null
    }

    override fun getJobsList(jobId: Int) {
        view?.showProgress()
        subscription.add(
            apiService.getLast3Jobs(
                CommonUtils.getLoginToken(),
                id = jobId
            )
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showJobsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }
}