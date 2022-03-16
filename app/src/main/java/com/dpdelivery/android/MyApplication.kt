package com.dpdelivery.android

import android.app.Activity
import android.app.Application
import android.content.*
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.multidex.MultiDex
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.di.DaggerAppComponent
import com.facebook.stetho.Stetho
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.util.HashMap
import javax.inject.Inject


class MyApplication : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var apiService: ApiService
    var currAct: String = ""

    companion object {
        lateinit var myApplication: MyApplication
        lateinit var mInstance: MyApplication
        var mActivity: Activity? = null
        lateinit var context: Context

    }

    private var mBroadcastData: MutableLiveData<String>? = null

    private var mCacheMap: MutableMap<String, Any>? = null

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            when (action) {
                WifiManager.NETWORK_STATE_CHANGED_ACTION, LocationManager.PROVIDERS_CHANGED_ACTION -> mBroadcastData!!.setValue(
                    action
                )
            }
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {

        super.onCreate()
        myApplication = this
        mInstance = this
        context = this
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
        }
        mCacheMap = HashMap()
        mBroadcastData = MutableLiveData()
        val filter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        registerReceiver(mReceiver, filter)
        registerActivityLifecycleCallbacks(this)
        val crashLytics = FirebaseCrashlytics.getInstance()
        crashLytics.log("my message")

    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(mReceiver)
    }

    fun getInstance(): MyApplication {
        return mInstance
    }

    fun observeBroadcast(owner: LifecycleOwner, observer: Observer<String>) {
        mBroadcastData!!.observe(owner, observer)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivity = activity
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Create")
    }

    override fun onActivityStarted(activity: Activity) {
        mActivity = activity
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Started")
    }

    override fun onActivityResumed(activity: Activity) {
        mActivity = activity
        currAct = activity::class.java.simpleName
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Resume")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Paused")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Stop")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on SaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Destroy")
    }
}