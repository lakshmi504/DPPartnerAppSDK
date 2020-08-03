package com.dpdelivery.android.ui.filteredjobs

import dagger.Binds
import dagger.Module

@Module
abstract class FilteredJobsListModule {
    @Binds
    abstract fun filteredJobsListPresenter(filteredJobsListPresenter: FilteredJobsListPresenter): FilteredJobsListContract.Presenter
}