package com.dpdelivery.android.api

import android.content.Context
import android.net.ConnectivityManager

class LiveNetworkMonitor constructor(context: Context) : NetworkMonitor {
    var applicationContext: Context

    init {
        this.applicationContext = context
    }

    override fun isConnected(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}