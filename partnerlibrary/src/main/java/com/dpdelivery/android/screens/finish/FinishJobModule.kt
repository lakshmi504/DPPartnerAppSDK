package com.dpdelivery.android.screens.finish

import dagger.Binds
import dagger.Module

@Module
abstract class FinishJobModule {
    @Binds
    abstract fun finishJobPresenter(finishJobPresenter: FinishJobPresenter): FinishJobContract.Presenter

}