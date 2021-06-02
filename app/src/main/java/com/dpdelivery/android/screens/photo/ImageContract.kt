package com.dpdelivery.android.screens.photo

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techres.UploadPhotoRes
import java.io.File

interface ImageContract {
    interface View : BaseView {
        fun showUploadPhotoRes(uploadPhotoRes: UploadPhotoRes)
    }

    interface Presenter : BasePresenter<View> {
        fun uploadPhoto(jobid: Int, file: File)
    }
}