package com.dpdelivery.android.di

import com.dpdelivery.android.screens.finish.SparePartsFragment
import com.dpdelivery.android.screens.servicereport.fragments.JobsFragment
import com.dpdelivery.android.screens.servicereport.fragments.SparesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyFragmentBuilder {
    @ContributesAndroidInjector
    internal abstract fun sparePartsFragment(): SparePartsFragment

    @ContributesAndroidInjector
    internal abstract fun sparesFragment(): SparesFragment

    @ContributesAndroidInjector
    internal abstract fun jobsFragment(): JobsFragment
}