package com.dpdelivery.android.ui.deliveryjob

import dagger.Binds
import dagger.Module

@Module
abstract class DeliveryJobModule {
    @Binds
    abstract fun deliveryJobPresenter(deliveryJobPresenter: DeliveryJobPresenter): DeliveryJobContract.Presenter
}