package com.dpdelivery.android.screens

import android.content.Context
import android.content.Intent
import com.dpdelivery.android.screens.splash.SplashActivity

/**
 * Created by user on 08/08/22.
 */
class PartnerApp {
    fun navigateToPartnerActivity(context: Context?) {
        val intent = Intent(context, SplashActivity::class.java)
        context!!.startActivity(intent)
    }

}