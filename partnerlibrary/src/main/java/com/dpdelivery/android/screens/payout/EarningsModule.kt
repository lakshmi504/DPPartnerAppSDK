package com.dpdelivery.android.screens.payout

import dagger.Binds
import dagger.Module

/**
 * Created by user on 14/06/21.
 */
@Module
abstract class EarningsModule {
    @Binds
    abstract fun earningsPresenter(earningsPresenter: EarningsPresenter): EarningsContract.Presenter
}