package com.dpdelivery.android.di

import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.finish.FinishJobActivity
import com.dpdelivery.android.technicianui.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.technicianui.photo.ImageActivity
import com.dpdelivery.android.technicianui.scanner.ScannerActivity
import com.dpdelivery.android.technicianui.techjobslist.TechJobsListActivity
import com.dpdelivery.android.ui.dashboard.DashBoardActivity
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.ui.deliveryjob.DeliveryJobActivity
import com.dpdelivery.android.ui.deliveryjob.ImagesActivity
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

    @ContributesAndroidInjector()
    internal abstract fun imagesActivity(): ImagesActivity

    @ContributesAndroidInjector()
    internal abstract fun dashBoardActivity(): DashBoardActivity

    @ContributesAndroidInjector()
    internal abstract fun techBaseActivity(): TechBaseActivity

    @ContributesAndroidInjector()
    internal abstract fun technicianJobsListActivity(): TechJobsListActivity

    @ContributesAndroidInjector()
    internal abstract fun technicianJobActivity(): TechJobDetailsActivity

    @ContributesAndroidInjector()
    internal abstract fun scannerActivity(): ScannerActivity

    @ContributesAndroidInjector()
    internal abstract fun finishJobActivity(): FinishJobActivity

    @ContributesAndroidInjector()
    internal abstract fun imageActivity(): ImageActivity

}