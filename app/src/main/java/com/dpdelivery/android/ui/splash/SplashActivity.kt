package com.dpdelivery.android.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.dpdelivery.android.R
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobListActivity
import com.dpdelivery.android.ui.login.LoginActivity
import com.dpdelivery.android.utils.CommonUtils
import java.util.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            launchScreen()
        }, 2000)
    }

    private fun launchScreen() {
        if (CommonUtils.getLoginToken().isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            try {
                val current_time = Date().time
                var expire_time = CommonUtils.getLoginToken()
                expire_time = expire_time.split(' ')[1]
                val jwt = JWT(expire_time)
                val expiry = jwt.expiresAt!!.time
                if (current_time > expiry) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, DeliveryJobListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: DecodeException) {
                Log.i("delivery app", "raised exception")
            }
        }

    }
}
