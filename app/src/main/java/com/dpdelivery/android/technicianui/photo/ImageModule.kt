package com.dpdelivery.android.technicianui.photo

import dagger.Binds
import dagger.Module

@Module
abstract class ImageModule {
    @Binds
    abstract fun imagePresenter(imagePresenter: ImagePresenter): ImageContract.Presenter
}