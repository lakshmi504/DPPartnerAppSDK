package com.dpdelivery.android.di

import android.content.Context
import com.dpdelivery.android.api.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient.Builder): ApiService {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        return retrofit2.Retrofit.Builder()
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiConstants.BASE_URL)
                .build()
                .create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideOkhttpBuilder(networkMonitor: NetworkMonitor): OkHttpClient.Builder {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
        okHttpBuilder.addNetworkInterceptor(StethoInterceptor())
        okHttpBuilder.addInterceptor { chain ->
            if (networkMonitor.isConnected())
                return@addInterceptor chain.proceed(chain.request()) else throw  NoNetworkException()
        }
        return okHttpBuilder
    }


    @Singleton
    @Provides
    fun networkMonitor(context: Context): NetworkMonitor {
        return LiveNetworkMonitor(context)
    }

}