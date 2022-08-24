package com.dpdelivery.android.screens.inventory

import dagger.Binds
import dagger.Module

/**
 * Created by user on 23/06/21.
 */
@Module
abstract class InventoryModule {
    @Binds
    abstract fun inventoryPresenter(inventoryPresenter: InventoryPresenter): InventoryContract.Presenter
}