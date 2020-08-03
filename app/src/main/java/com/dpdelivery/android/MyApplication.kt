package com.dpdelivery.android

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDex
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.di.DaggerAppComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject


class MyApplication : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var apiService: ApiService
    var currAct: String = ""

    companion object {
        lateinit var myApplication: MyApplication
        var mActivity: Activity? = null
        lateinit var context: Context

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
        context = this
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build())
        }
        registerActivityLifecycleCallbacks(this)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivity = activity
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Create")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(ContentValues.TAG, "${activity::class.java.simpleName}:on Started")
    }

    override fun onActivityResumed(activity: Activity) {
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