package com.dpdelivery.android.screens.workflow

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.*
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class WorkFlowPresenter @Inject constructor(
    var apiService: ApiService,
    var context: Context,
    var baseScheduler: BaseScheduler
) : WorkFlowContract.Presenter {

    var view: WorkFlowContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: WorkFlowContract.View?) {
        this.view = view
    }

    override fun getWorkFlowData(jobId: Int) {
        view?.showProgress()
        subscription.add(
            apiService.getWorkFlowData(CommonUtils.getLoginToken(), jobId = jobId)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.hideProgress()
                        view?.showWorFlowDataRes(res)
                    },
                    { throwable ->
                        view?.hideProgress()
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun addWorkFlow(workFlow: AddWorkFlowData) {
        view?.showProgress()
        subscription.add(
            apiService.addWorkFlow(CommonUtils.getLoginToken(), workFlow)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.hideProgress()
                        view?.showWorkFlowDataRes(res)
                    },
                    { throwable ->
                        view?.hideProgress()
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun addWorkFlowSubmit(workFlow: AddWorkFlowData) {
        view?.showProgress()
        subscription.add(
            apiService.addWorkFlow(CommonUtils.getLoginToken(), workFlow)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.hideProgress()
                        view?.showWorkFlowDataSubmitRes(res)
                    },
                    { throwable ->
                        view?.hideProgress()
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun addFinishWorkFlow(workFlow: AddWorkFlowData) {
        view?.showProgress()
        subscription.add(
            apiService.addWorkFlow(CommonUtils.getLoginToken(), workFlow)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.hideProgress()
                        view?.showWorkFlowFinishDataRes(res)
                    },
                    { throwable ->
                        view?.hideProgress()
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun addImage(jobid: Int, elementId: Int, file: File) {
        //  view?.showProgress()
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
        subscription.add(
            apiService.addImage(CommonUtils.getLoginToken(), jobid, elementId, body)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        if (res.success!!) {
                            view?.hideProgress()
                            view?.showAddTextRes(res)
                        }
                    },
                    { throwable ->
                        view?.hideProgress()
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
                    })
        )
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
                        try {
                            view?.showErrorMsg(throwable)
                        } catch (e: Exception) {

                        }
                    })
        )
    }

    override fun getSparePartsList(functionName: String?) {
        view?.showProgress()
        subscription.add(
            apiService.getSpareParts(CommonUtils.getLoginToken(), functionName!!)
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

    override fun getApiDataList(functionName: String?) {
        view?.showProgress()
        subscription.add(
            apiService.getApiInputData(CommonUtils.getLoginToken(), functionName!!)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showApiInputRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getPidDetails(homeIP: HomeIP) {
        view?.showProgress()
        subscription.add(
            apiService.getBLEDetails(homeIP = homeIP)
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

    override fun getBleCmdDetails(deviceCode: String, onlyPending: Boolean) {
        view?.showProgress()
        subscription.add(
            apiService.getCommands(deviceCode, onlyPending)
                .subscribeOn(baseScheduler.io())
                .observeOn(baseScheduler.ui())
                .subscribe(
                    { res ->
                        view?.showBleCmdDetailsRes(res)
                    },
                    { throwable ->
                        view?.showErrorMsg(throwable)
                    })
        )
    }

    override fun getJob(jobId: Int) {
        view?.showProgress()
        try {
            subscription.add(
                apiService.getAssignedJobById(CommonUtils.getLoginToken(), jobId = jobId)
                    .subscribeOn(baseScheduler.io())
                    .observeOn(baseScheduler.ui())
                    .subscribe(
                        { res ->
                            view?.hideProgress()
                            view?.showJobRes(res)
                        },
                        { throwable ->
                            view?.hideProgress()
                            view?.showErrorMsg(throwable)
                        })
            )
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun getBidStatus(data: BIDStatusIp) {
        view?.showProgress()
        try {
            subscription.add(
                apiService.getBotStatus(CommonUtils.getLoginToken(), bidStatusIp = data)
                    .subscribeOn(baseScheduler.io())
                    .observeOn(baseScheduler.ui())
                    .subscribe(
                        { res ->
                            view?.hideProgress()
                            view?.showBidStatus(res)
                        },
                        { throwable ->
                            view?.hideProgress()
                            view?.showErrorMsg(throwable)
                        })
            )
        } catch (e: KotlinNullPointerException) {

        }
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