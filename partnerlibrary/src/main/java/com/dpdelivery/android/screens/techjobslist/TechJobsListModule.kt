package com.dpdelivery.android.screens.techjobslist

import dagger.Binds
import dagger.Module

@Module
abstract class TechJobsListModule {
    @Binds
    abstract fun techJobsListPresenter(techJobsListPresenter: TechJobsListPresenter): TechJobsListContract.Presenter
}