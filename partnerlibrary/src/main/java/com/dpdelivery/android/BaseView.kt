package com.dpdelivery.android

import org.json.JSONException
import retrofit2.HttpException

interface BaseView {
    fun init()
    fun handleError(throwable: Throwable, apiType: String = "none") {
        when (throwable) {
            is KotlinNullPointerException -> showErrorMsg(Throwable("something went wrong"), apiType)
            is HttpException -> showErrorMsg(Throwable("something went wrong"), apiType)
            is JSONException -> showErrorMsg(Throwable("something went wrong"), apiType)
        }
    }

    fun showErrorMsg(throwable: Throwable, apiType: String = "none")
    fun showSuccessMsg(any: Any) {}
    fun showProgress(msg: String = "Please wait") {}
    fun hideProgress() {}
    fun showViewState(state: Int) {}
}
