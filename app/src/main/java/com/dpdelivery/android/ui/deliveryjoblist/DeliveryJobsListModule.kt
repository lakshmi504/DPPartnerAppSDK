package com.dpdelivery.android.ui.deliveryjoblist

import dagger.Binds
import dagger.Module

@Module
abstract class DeliveryJobsListModule {
    @Binds
    abstract fun deliveryJobsListPresenter(deliveryJobsListPresenter: DeliveryJobsListPresenter): DeliveryJobsListContract.Presenter
}