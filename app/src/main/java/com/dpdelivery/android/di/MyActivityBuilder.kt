package com.dpdelivery.android.di

import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.ui.deliveryjob.DeliveryJobActivity
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobListActivity
import com.dpdelivery.android.ui.location.LocationActivity
import com.dpdelivery.android.ui.location.MapLocationActivity
import com.dpdelivery.android.ui.login.LoginActivity
import com.dpdelivery.android.ui.photo.PhotosActivity
import com.dpdelivery.android.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyActivityBuilder {

    @ContributesAndroidInjector()
    internal abstract fun baseActivity(): BaseActivity

    @ContributesAndroidInjector()
    internal abstract fun splashactivity(): SplashActivity

    @ContributesAndroidInjector()
    internal abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector()
    internal abstract fun deliveryJobListActivity(): DeliveryJobListActivity

    @ContributesAndroidInjector()
    internal abstract fun deliveryJobActivity(): DeliveryJobActivity

    @ContributesAndroidInjector()
    internal abstract fun locationActivity(): LocationActivity

    @ContributesAndroidInjector()
    internal abstract fun mapLocationActivity(): MapLocationActivity

    @ContributesAndroidInjector()
    internal abstract fun photoActivity(): PhotosActivity
}