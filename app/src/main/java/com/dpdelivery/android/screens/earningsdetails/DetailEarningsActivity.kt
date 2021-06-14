package com.dpdelivery.android.screens.earningsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.summary.SummaryActivity
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_detail_earnings.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailEarningsActivity : TechBaseActivity(), IAdapterClickListener, View.OnClickListener {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_detail_earnings, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("My Earnings")
        setUpBottomNavView(true)
        getDetailEarnings()
        iv_calender.setOnClickListener(this)
        tv_amount.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_calender -> {
                val builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                val now = Calendar.getInstance()
                builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
                builder.setTitleText("Select Custom Range")
                val picker = builder.build()
                picker.show(supportFragmentManager, picker.toString())
                picker.addOnNegativeButtonClickListener {
                    picker.dismiss()
                }
                picker.addOnPositiveButtonClickListener {
                    val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ROOT)
                    val startDateString = formatter.format(Date(it.first!!))
                    val endDateString = formatter.format(Date(it.second!!))
                    //toast("$startDateString:: to :: $endDateString")
                    tv_date_range.text = ("$startDateString to $endDateString")
                    // showViewState(MultiStateView.VIEW_STATE_LOADING)
                    // presenter.getDeliveryJobsListByDate(startDateString, endDateString)
                }
            }
        }
    }

    private fun getDetailEarnings() {
        rv_detail_earnings.layoutManager = LinearLayoutManager(this)
        val adapterDetailEarnings =
            BasicAdapter(context, R.layout.item_detail_earnings, adapterClickListener = this)
        rv_detail_earnings.adapter = adapterDetailEarnings
        val earningsList = ArrayList<EarningsModel>()
        earningsList.add(
            EarningsModel(
                "Job #234 visit credited",
                "24th May 2021", true, 100
            )
        )
        earningsList.add(
            EarningsModel(
                "Job #236 ontime credited",
                "24th May 2021", true, 25
            )
        )
        earningsList.add(
            EarningsModel(
                "Job #2345 Rejected debited",
                "24th May 2021", false, 50
            )
        )
        earningsList.add(
            EarningsModel(
                "Daily bonus credited",
                "24th May 2021", true, 50
            )
        )
        adapterDetailEarnings.addList(earningsList)
        adapterDetailEarnings.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        bottom_navigation.selectedItemId = R.id.action_earnings
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

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

    }

}