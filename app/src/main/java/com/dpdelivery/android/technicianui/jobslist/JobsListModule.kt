package com.dpdelivery.android.technicianui.jobslist

import dagger.Binds
import dagger.Module

@Module
abstract class JobsListModule {
    @Binds
    abstract fun techJobsListPresenter(techJobsListPresenter: JobsListPresenter): JobsListContract.Presenter
}