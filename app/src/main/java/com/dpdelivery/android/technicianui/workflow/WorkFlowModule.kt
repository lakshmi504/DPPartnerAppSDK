package com.dpdelivery.android.technicianui.workflow

import dagger.Binds
import dagger.Module

@Module
abstract class WorkFlowModule {
    @Binds
    abstract fun workFlowPresenter(workFlowPresenter: WorkFlowPresenter): WorkFlowContract.Presenter
}