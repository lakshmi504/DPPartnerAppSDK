package com.dpdelivery.android.screens.confirmpickupinventory

import dagger.Binds
import dagger.Module

/**
 * Created by user on 24/06/21.
 */
@Module
abstract class ConfirmPickUpModule {
    @Binds
    abstract fun confirmPickUpPresenter(confirmPickUpPresenter: ConfirmPickUpPresenter): ConfirmPickUpContract.Presenter

}