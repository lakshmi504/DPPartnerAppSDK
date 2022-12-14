package com.dpdelivery.android.screens.login

import com.dpdelivery.android.BasePresenter
import com.dpdelivery.android.BaseView
import com.dpdelivery.android.model.techinp.LoginIp
import okhttp3.Headers

interface LoginContract {

    interface View : BaseView {
        fun showValidateFieldsError(msg: String)
        fun showLoginLoader()
        fun setLoginRes(res: Headers)
    }

    interface Presenter : BasePresenter<View> {
        fun validate(loginIp: LoginIp): Boolean
        fun doLogin(loginIp: LoginIp)
    }
}