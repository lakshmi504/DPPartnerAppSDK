package com.dpdelivery.android.screens.photo

import dagger.Binds
import dagger.Module

@Module
abstract class ImageModule {
    @Binds
    abstract fun imagePresenter(imagePresenter: ImagePresenter): ImageContract.Presenter
}