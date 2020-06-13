package com.dpdelivery.android.technicianui.finish

import dagger.Binds
import dagger.Module

@Module
abstract class FinishJobModule {
    @Binds
    abstract fun finishJobPresenter(finishJobPresenter: FinishJobPresenter): FinishJobContract.Presenter

}