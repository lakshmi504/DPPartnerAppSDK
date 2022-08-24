package com.dpdelivery.android.di

import android.content.Context
import com.dpdelivery.android.MyApplication
import com.dpdelivery.android.api.ApiRequestParam
import com.dpdelivery.android.utils.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: MyApplication): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun sharedPrefHelper(context: Context): SharedPreferenceManager = SharedPreferenceManager()


    @Singleton
    @Provides
    fun apiRequestParam(appSharedPrefs: SharedPreferenceManager): ApiRequestParam = ApiRequestParam(appSharedPrefs = appSharedPrefs)

}