package com.dpdelivery.android.technicianui.jobdetails

import dagger.Binds
import dagger.Module

@Module
abstract class TechJobDetailsModule {
    @Binds
    abstract fun techJobDetailsPresenter(techJobDetailsPresenter: TechJobDetailsPresenter): TechJobDetailsContract.Presenter
}