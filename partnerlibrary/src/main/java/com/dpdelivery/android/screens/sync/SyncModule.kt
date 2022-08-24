package com.dpdelivery.android.screens.sync

import dagger.Binds
import dagger.Module

/**
 * Created by user on 29/07/22.
 */
@Module
abstract class SyncModule {
    @Binds
    abstract fun syncPresenter(syncPresenter: SyncPresenter): SyncContract.Presenter

}