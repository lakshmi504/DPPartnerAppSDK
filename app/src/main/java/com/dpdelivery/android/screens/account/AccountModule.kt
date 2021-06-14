package com.dpdelivery.android.screens.account

import dagger.Binds
import dagger.Module

/**
 * Created by user on 04/06/21.
 */
@Module
abstract class AccountModule {
    @Binds
    abstract fun accountPresenter(accountPresenter: AccountPresenter): AccountContract.Presenter

}