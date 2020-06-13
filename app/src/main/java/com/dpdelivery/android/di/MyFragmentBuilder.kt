package com.dpdelivery.android.di

import com.dpdelivery.android.technicianui.finish.SparePartsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyFragmentBuilder {
    @ContributesAndroidInjector
    internal abstract fun sparePartsFragment(): SparePartsFragment
}