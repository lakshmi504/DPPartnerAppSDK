package com.dpdelivery.android.di

import com.dpdelivery.android.screens.account.AccountActivity
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.confirmpickupinventory.ConfirmScanResultsActivity
import com.dpdelivery.android.screens.finish.FinishJobActivity
import com.dpdelivery.android.screens.getnextjob.GetNextJobActivity
import com.dpdelivery.android.screens.inventory.InventoryActivity
import com.dpdelivery.android.screens.inventoryDetails.InventoryDetailsActivity
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.screens.jobslist.JobsListActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.payout.DetailEarningsActivity
import com.dpdelivery.android.screens.photo.ImageActivity
import com.dpdelivery.android.screens.scanner.ScannerActivity
import com.dpdelivery.android.screens.search.SearchActivity
import com.dpdelivery.android.screens.servicereport.ServiceReportActivity
import com.dpdelivery.android.screens.splash.SplashActivity
import com.dpdelivery.android.screens.summary.SummaryActivity
import com.dpdelivery.android.screens.sync.SyncActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.screens.workflow.WorkFlowActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyActivityBuilder {

    @ContributesAndroidInjector()
    internal abstract fun splashactivity(): SplashActivity

    @ContributesAndroidInjector()
    internal abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector()
    internal abstract fun techBaseActivity(): TechBaseActivity

    @ContributesAndroidInjector()
    internal abstract fun technicianJobsListActivity(): TechJobsListActivity

    @ContributesAndroidInjector()
    internal abstract fun technicianJobActivity(): TechJobDetailsActivity

    @ContributesAndroidInjector()
    internal abstract fun scannerActivity(): ScannerActivity

    @ContributesAndroidInjector()
    internal abstract fun finishJobActivity(): FinishJobActivity

    @ContributesAndroidInjector()
    internal abstract fun imageActivity(): ImageActivity

    @ContributesAndroidInjector()
    internal abstract fun jobsListActivity(): JobsListActivity

    @ContributesAndroidInjector()
    internal abstract fun searchActivity(): SearchActivity

    @ContributesAndroidInjector()
    internal abstract fun summaryActivity(): SummaryActivity

    @ContributesAndroidInjector()
    internal abstract fun syncActivity(): SyncActivity

    @ContributesAndroidInjector()
    internal abstract fun workFlowActivity(): WorkFlowActivity

    @ContributesAndroidInjector()
    internal abstract fun inventoryActivity(): InventoryActivity

    @ContributesAndroidInjector()
    internal abstract fun newInventoryActivity(): InventoryDetailsActivity

    @ContributesAndroidInjector()
    internal abstract fun ConfirmScanResultsActivity(): ConfirmScanResultsActivity

    @ContributesAndroidInjector()
    internal abstract fun accountActivity(): AccountActivity

    @ContributesAndroidInjector()
    internal abstract fun detailEarningsActivity(): DetailEarningsActivity

    @ContributesAndroidInjector()
    internal abstract fun serviceReportActivity(): ServiceReportActivity

    @ContributesAndroidInjector()
    internal abstract fun getNextJobActivity(): GetNextJobActivity

}