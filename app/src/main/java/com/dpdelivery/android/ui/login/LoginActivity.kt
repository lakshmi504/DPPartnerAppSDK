package com.dpdelivery.android.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.auth0.android.jwt.JWT
import com.dpdelivery.android.R
import com.dpdelivery.android.model.input.LoginIp
import com.dpdelivery.android.technicianui.techjobslist.TechJobsListActivity
import com.dpdelivery.android.ui.dashboard.DashBoardActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Headers
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity(), View.OnClickListener, LoginContract.View {

    lateinit var mContext: Context

    @Inject
    lateinit var presenter: LoginPresenter

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_login)
        init()
    }

    override fun init() {
        mContext = this
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> {
                showLoginLoader()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun showValidateFieldsError(msg: String) {
        when (msg) {
            getString(R.string.err_Mobile) -> et_email.error = msg
            getString(R.string.err_password) -> et_password.error = msg
        }
    }

    override fun showLoginLoader() {
        val loginIp = LoginIp(
                username = et_email.text.toString(),
                password = et_password.text.toString()
        )
        if (presenter.validate(loginIp))
            presenter.doLogin(loginIp)
    }

    override fun setLoginRes(res: Headers) {
        if (res.name(0) == "Authorization") {
            var data = res.get("Authorization")
            CommonUtils.saveLoginToken(data)
            if (data!!.startsWith("Bearer ")) {
                data = data.split(' ')[1]
                val jwt = JWT(data)
                val aud = jwt.audience?.get(0)
                CommonUtils.saveRole(aud.toString())
                if (aud.equals("ROLE_Administrator")) {
                    val intent = Intent(this, TechJobsListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (aud.equals("ROLE_Technician")) {
                    val intent = Intent(this, TechJobsListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, DashBoardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            toast("Invalid Credentials")
        }
    }

    override fun showProgress(msg: String) {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        super.hideProgress()
        progress_bar.visibility = View.GONE
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }
}
