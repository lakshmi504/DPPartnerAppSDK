package com.dpdelivery.android.screens.servicereport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.SpareConsumption
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.servicereport.fragments.JobsFragment
import com.dpdelivery.android.screens.servicereport.fragments.SparesFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_service_report.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*

class ServiceReportActivity : TechBaseActivity() {
    private var sparesHistory: ArrayList<SpareConsumption>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_service_report, tech_layout_container)
        init()
    }

    private fun init() {
        setUpBottomNavView(false)
        showBack()
        setTitle("Service Report")
        intent.apply {
            sparesHistory = getParcelableArrayListExtra("sparesHistory")
        }
        addTabs(viewPager!!)
        tabLayout!!.setupWithViewPager(viewPager)
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun addTabs(viewPager: ViewPager) {
        val adapter = ServiceReportAdapter(supportFragmentManager)
        adapter.addFrag(SparesFragment.newInstance(sparesHistory!!), "Spares")
        adapter.addFrag(JobsFragment.newInstance("purifierId"), "Jobs")
        viewPager.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}