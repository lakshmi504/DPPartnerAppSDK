package com.dpdelivery.android.ui.photo

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.UploadPhotoRes
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

interface PhotoContract {

    interface View : BaseView {
        fun showUploadRes(uploadPhotoRes: UploadPhotoRes)
    }

    interface Presenter : BasePresenter<View> {
        fun uploadPhoto(jobid: Int, file: File)
    }
}