package com.dpdelivery.android.screens.login

import android.content.Context
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.model.techinp.LoginIp
import com.dpdelivery.android.utils.schedulers.BaseScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        var apiService: ApiService,
        var context: Context,
        var baseScheduler: BaseScheduler) : LoginContract.Presenter {

    var view: LoginContract.View? = null
    private val subscription = CompositeDisposable()

    override fun takeView(view: LoginContract.View?) {
        this.view = view
    }

    override fun validate(loginIp: LoginIp): Boolean {
        if (loginIp.username!!.isEmpty()) {
            view?.showValidateFieldsError(context.getString(R.string.err_Mobile))
            return false
        }
        if (loginIp.password!!.isEmpty()) {
            view?.showValidateFieldsError(context.getString(R.string.err_password))
            return false
        }
        return true
    }

    override fun doLogin(loginIp: LoginIp) {
        view?.showProgress()
        subscription.add(
                apiService.login(loginIp)
                        .subscribeOn(baseScheduler.io())
                        .observeOn(baseScheduler.ui())
                        .subscribe(
                                { res ->
                                    view?.hideProgress()
                                    if (res.isSuccessful) {
                                        view?.setLoginRes(res.headers())
                                    } else
                                        view?.showErrorMsg(Throwable("Username and Password is not found"))
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