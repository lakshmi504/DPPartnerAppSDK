package com.dpdelivery.android.di

import com.dpdelivery.android.utils.schedulers.BaseScheduler
import com.dpdelivery.android.utils.schedulers.Scheduler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BaseSchedulerModule {

    @Singleton
    @Provides
    fun baseScheduler(): BaseScheduler {
        return Scheduler()
    }
}