package com.dpdelivery.android.technicianui.summary

import dagger.Binds
import dagger.Module

@Module
abstract class SummaryModule {
    @Binds
    abstract fun summaryPresenter(summaryPresenter: SummaryPresenter): SummaryContract.Presenter
}