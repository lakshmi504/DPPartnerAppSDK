package com.dpdelivery.android.screens.inventoryDetails

import dagger.Binds
import dagger.Module

/**
 * Created by user on 24/06/21.
 */
@Module
abstract class InventoryDetailsModule {
    @Binds
    abstract fun inventoryDetailsPresenter(inventoryDetailsPresenter: InventoryDetailsPresenter): InventoryDetailsContract.Presenter
}