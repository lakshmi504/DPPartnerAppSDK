package com.dpdelivery.android.screens.finish

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FinishJobPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : FinishJobContract.Presenter {

    var view: FinishJobContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: FinishJobContract.View?) {
        this.view = view
    }

    override fun getSparePartsList() {
        view?.showProgress()
        subscription.add(
            apiService.getSpareParts(CommonUtils.getLoginToken())
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showSparePartsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun finishJob(jobId: Int, finishJobIp: FinishJobIp) {
        view?.showProgress()
        subscription.add(
            apiService.finishJob(CommonUtils.getLoginToken(), jobId, finishJobIp)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showFinishJobRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun reSendHappyCode(jobId: Int) {
        view?.showProgress()
        subscription.add(
            apiService.resendHappyCode(CommonUtils.getLoginToken(), jobId = jobId)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.reSendHappyCodeRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getPidDetails(hashMap: HashMap<String, String>) {
        view?.showProgress()
        subscription.add(
            apiService.getBLEDetails(purifierid = hashMap)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showPidDetailsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun updateServerCmds(hashMap: HashMap<String, String>) {
        view?.showProgress()
        subscription.add(
            apiService.updateServerCmds(params = hashMap)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showSyncRes(res)
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