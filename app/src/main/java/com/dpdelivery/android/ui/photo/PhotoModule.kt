package com.dpdelivery.android.ui.photo

import dagger.Binds
import dagger.Module

@Module
abstract class PhotoModule {
    @Binds
    abstract fun photoPresenter(photoPresenter: PhotoPresenter): PhotoContract.Presenter
}