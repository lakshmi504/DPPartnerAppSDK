package com.dpdelivery.android.technicianui.photo

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

class ImagePresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : ImageContract.Presenter {

    var view: ImageContract.View? = null
    private val subscription = CompositeDisposable()


    override fun takeView(view: ImageContract.View?) {
        this.view = view
    }

    override fun uploadPhoto(jobid: Int, file: File) {
        view?.showProgress()
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
        subscription.add(
                apiService.uploadDevicePhoto(CommonUtils.getLoginToken(), jobid, body)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    if (res.success) {
                                        view?.showUploadPhotoRes(res)
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