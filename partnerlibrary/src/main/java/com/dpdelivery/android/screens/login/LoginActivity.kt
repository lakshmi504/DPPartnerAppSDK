package com.dpdelivery.android.screens.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techinp.LoginIp
import com.dpdelivery.android.screens.getnextjob.GetNextJobActivity
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
        iv_show_password.setOnClickListener(this)
        et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        iv_show_password.setBackgroundResource(R.drawable.password_eye_toggle)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_login -> {
                showLoginLoader()
            }
            R.id.iv_show_password -> {
                et_password.setSelection(et_password.text!!.length)
                if (et_password.inputType == InputType.TYPE_CLASS_TEXT) {
                    et_password.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    iv_show_password.setBackgroundResource(R.drawable.password_eye_toggle)

                } else {
                    et_password.inputType = InputType.TYPE_CLASS_TEXT
                    iv_show_password.setBackgroundResource(R.drawable.ic_eye_on)
                }
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
            username = et_email.text.toString().trim { it <= ' ' },
            password = et_password.text.toString().trim { it <= ' ' }
        )
        if (presenter.validate(loginIp))
            presenter.doLogin(loginIp)
    }

    override fun setLoginRes(res: Headers) {
        val data = res.get("Authorization")
        CommonUtils.saveLoginToken(data)
        val intent = Intent(this, GetNextJobActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showProgress(msg: String) {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        super.hideProgress()
        progress_bar.visibility = View.GONE
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }
}
