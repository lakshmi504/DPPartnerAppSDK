package com.dpdelivery.android.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiConstants
import com.dpdelivery.android.api.ApiService
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techinp.LoginIp
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.utils.CommonUtils
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by user on 08/08/22.
 */
class PartnerApp {
    companion object {
        fun navigateToDPPartnerApp(context: Context?, jobId: String) {
            loginUser(context, jobId)
        }

        fun loginUser(context: Context?, jobId: String) {
            if (context != null) {
                val login = LoginIp(
                    username = context.getString(R.string.username),
                    password = context.getString(R.string.login_password)
                )
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                val apiService: ApiService = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(ApiConstants.TEST_BASE_URL)
                    .build()
                    .create(ApiService::class.java)

                val subscription = CompositeDisposable()
                subscription.add(
                    apiService.login(login)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { res ->
                                val token = res.headers()
                                val data = token.get("Authorization")
                                CommonUtils.saveLoginToken(data)
                                val intent = Intent(context, TechJobDetailsActivity::class.java)
                                intent.putExtra(Constants.ID, jobId)
                                context.startActivity(intent)
                            },
                            { throwable ->
                                Log.e("PartnerApp", throwable.message ?: "onError")
                            }
                        )
                )
            }

        }
    }
}
