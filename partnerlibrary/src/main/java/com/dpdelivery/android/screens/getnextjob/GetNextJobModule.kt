package com.dpdelivery.android.screens.getnextjob

import dagger.Binds
import dagger.Module

@Module
abstract class GetNextJobModule {
    @Binds
    abstract fun getNextJobPresenter(getNextJobPresenter: GetNextJobPresenter): GetNextJobsContract.Presenter
}