package com.dpdelivery.android.technicianui.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.DecoratorView
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.LastMonth
import com.dpdelivery.android.model.techres.SummaryRes
import com.dpdelivery.android.model.techres.ThisMonth
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.ui.login.LoginActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.DateHelper
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_summary.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SummaryActivity : TechBaseActivity(), SummaryContract.View, IAdapterClickListener {
    lateinit var mContext: Context
    lateinit var summaryListThisMonth: ArrayList<ThisMonth?>
    lateinit var summaryListLastMonth: ArrayList<LastMonth?>
    lateinit var adapterSummaryThisMonth: BasicAdapter
    lateinit var adapterSummaryLastMonth: BasicAdapter

    @Inject
    lateinit var presenter: SummaryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_summary, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Summary")
        setUpBottomNavView(true)
        getSummary()
        iv_logout.visibility = View.VISIBLE
        error_button.setOnClickListener {
            getSummary()
        }
        iv_logout.setOnClickListener {
            SharedPreferenceManager.clearPreferences()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        val latestversion = CommonUtils.version
        val date = DateHelper.getCurrentDate()
        val input = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val output = SimpleDateFormat("yyyy", Locale.ROOT)
        var d: Date? = null
        try {
            d = input.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val year = output.format(d!!)
        tv_version.text = CommonUtils.getCopyRightSymbol(context, "$year. v$latestversion")
    }

    private fun getSummary() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        rv_this_month_summary.visibility = View.VISIBLE
        rv_this_month_summary.layoutManager = LinearLayoutManager(mContext)
        val dividerItemDecoration1 = DecoratorView(ContextCompat.getDrawable(mContext, R.drawable.divider)!!)
        rv_this_month_summary.addItemDecoration(dividerItemDecoration1)
        adapterSummaryThisMonth = BasicAdapter(mContext, R.layout.item_summary_this_month, adapterClickListener = this)
        rv_this_month_summary.adapter = adapterSummaryThisMonth

        rv_last_month_summary.visibility = View.VISIBLE
        rv_last_month_summary.layoutManager = LinearLayoutManager(mContext)
        val dividerItemDecoration2 = DecoratorView(ContextCompat.getDrawable(mContext, R.drawable.divider)!!)
        rv_last_month_summary.addItemDecoration(dividerItemDecoration2)
        adapterSummaryLastMonth = BasicAdapter(mContext, R.layout.item_summary_last_month, adapterClickListener = this)
        rv_last_month_summary.adapter = adapterSummaryLastMonth

        presenter.getSummary()
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        bottom_navigation.selectedItemId = R.id.action_summary
    }

    override fun showSummaryRes(res: SummaryRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            summaryListLastMonth = res.body?.result?.lastMonth!!
            summaryListThisMonth = res.body.result.thisMonth!!
            if (summaryListLastMonth.isEmpty() && summaryListThisMonth.isEmpty()) {
                tv_no_data.visibility = View.VISIBLE
            }
            if (summaryListThisMonth.isNotEmpty()) {
                this_header.visibility = View.VISIBLE
                adapterSummaryThisMonth.addList(summaryListThisMonth)
            }
            if (summaryListLastMonth.isNotEmpty()) {
                last_header.visibility = View.VISIBLE
                adapterSummaryLastMonth.addList(summaryListLastMonth)
            }
        } else {
            toast(res.message!!)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
        showViewState(MultiStateView.VIEW_STATE_ERROR)
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

    }
}