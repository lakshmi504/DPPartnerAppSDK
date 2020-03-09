package com.dpdelivery.android.ui.photo

import android.content.Context
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class PhotoPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : PhotoContract.Presenter {

    var view: PhotoContract.View? = null
    private val subscription = CompositeDisposable()


    override fun takeView(view: PhotoContract.View?) {
        this.view = view
    }

    override fun uploadPhoto(jobid: Int, file: File) {
        view?.showProgress()
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
        val jobId = RequestBody.create(MediaType.parse("multipart/form-data"), jobid.toString())

        subscription.add(
                apiService.uploadPhoto(CommonUtils.getLoginToken(), jobId, body)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    if (res.success) {
                                        view?.showUploadRes(res)
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