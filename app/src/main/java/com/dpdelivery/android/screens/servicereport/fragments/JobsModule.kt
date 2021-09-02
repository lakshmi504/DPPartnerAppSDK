package com.dpdelivery.android.screens.servicereport.fragments

import dagger.Binds
import dagger.Module

/**
 * Created by user on 24/08/21.
 */
@Module
abstract class JobsModule {
    @Binds
    abstract fun jobsPresenter(jobsPresenter: JobsPresenter): JobsContract.Presenter
}