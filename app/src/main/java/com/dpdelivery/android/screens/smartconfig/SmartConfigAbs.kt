package com.dpdelivery.android.screens.smartconfig

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.annotation.Nullable
import androidx.core.location.LocationManagerCompat
import com.dpdelivery.android.R
import com.espressif.iot.esptouch2.provision.TouchNetUtil
import dagger.android.support.DaggerAppCompatActivity
import java.net.InetAddress


/**
 * Created by user on 30/11/21.
 */
abstract class SmartConfigAbs : DaggerAppCompatActivity() {

    private var mWifiManager: WifiManager? = null

    protected class StateResult {
        var message: CharSequence? = null
        var permissionGranted = false
        var locationRequirement = false
        var wifiConnected = false
        var is5G = false
        var address: InetAddress? = null
        var ssid: String? = null
        var ssidBytes: ByteArray? = null
        var bssid: String? = null
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
    }

    protected fun checkPermission(): StateResult {
        val result = StateResult()
        result.permissionGranted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationGranted = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            if (!locationGranted) {
                val splits =
                    getString(R.string.esptouch_message_permission).split("\n").toTypedArray()
                require(splits.size == 2) { "Invalid String @RES esptouch_message_permission" }
                val ssb = SpannableStringBuilder(splits[0])
                ssb.append('\n')
                val clickMsg = SpannableString(splits[1])
                val clickSpan = ForegroundColorSpan(-0xffdd01)
                clickMsg.setSpan(clickSpan, 0, clickMsg.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                ssb.append(clickMsg)
                result.message = ssb
                return result
            }
        }
        result.permissionGranted = true
        return result
    }

    protected fun checkLocation(): StateResult {
        val result = StateResult()
        result.locationRequirement = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val manager = getSystemService(LocationManager::class.java)
            val enable = manager != null && LocationManagerCompat.isLocationEnabled(manager)
            if (!enable) {
                result.message = getString(R.string.esptouch_message_location)
                return result
            }
        }
        result.locationRequirement = false
        return result
    }

    protected fun checkWifi(): StateResult {
        val result = StateResult()
        result.wifiConnected = false
        val wifiInfo = mWifiManager!!.connectionInfo
        val connected: Boolean = TouchNetUtil.isWifiConnected(mWifiManager)
        if (!connected) {
            result.message = getString(R.string.esptouch_message_wifi_connection)
            return result
        }
        val ssid: String = TouchNetUtil.getSsidString(wifiInfo)
        val ipValue = wifiInfo.ipAddress
        if (ipValue != 0) {
            result.address = TouchNetUtil.getAddress(wifiInfo.ipAddress)
        } else {
            result.address = TouchNetUtil.getIPv4Address()
            if (result.address == null) {
                result.address = TouchNetUtil.getIPv6Address()
            }
        }
        result.wifiConnected = true
        result.message = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            result.is5G = TouchNetUtil.is5G(wifiInfo.frequency)
        }
        if (result.is5G) {
            result.message = getString(R.string.esptouch_message_wifi_frequency)
        }
        result.ssid = ssid
        result.ssidBytes = TouchNetUtil.getRawSsidBytesOrElse(wifiInfo, ssid.toByteArray())
        result.bssid = wifiInfo.bssid
        return result
    }
}