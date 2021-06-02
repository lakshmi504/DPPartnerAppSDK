package com.dpdelivery.android.screens.jobslist

import dagger.Binds
import dagger.Module

@Module
abstract class JobsListModule {
    @Binds
    abstract fun techJobsListPresenter(techJobsListPresenter: JobsListPresenter): JobsListContract.Presenter
}