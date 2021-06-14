package com.dpdelivery.android.screens.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.LastMonth
import com.dpdelivery.android.model.techres.SummaryRes
import com.dpdelivery.android.model.techres.ThisMonth
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_summary.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
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
        showBack()
        setUpBottomNavView(false)
        getSummary()
        error_button.setOnClickListener {
            getSummary()
        }
    }

    private fun getSummary() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        rv_this_month_summary.visibility = View.VISIBLE
        rv_this_month_summary.layoutManager = LinearLayoutManager(mContext)
        adapterSummaryThisMonth =
            BasicAdapter(mContext, R.layout.item_summary_this_month, adapterClickListener = this)
        rv_this_month_summary.adapter = adapterSummaryThisMonth

        rv_last_month_summary.visibility = View.VISIBLE
        rv_last_month_summary.layoutManager = LinearLayoutManager(mContext)
        adapterSummaryLastMonth =
            BasicAdapter(mContext, R.layout.item_summary_last_month, adapterClickListener = this)
        rv_last_month_summary.adapter = adapterSummaryLastMonth

        presenter.getSummary()
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun showSummaryRes(res: SummaryRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            summaryListLastMonth = res.body?.result?.lastMonth!!
            summaryListThisMonth = res.body.result.thisMonth!!
            if (summaryListLastMonth.isEmpty() && summaryListThisMonth.isEmpty()) {
                tv_no_data.visibility = View.VISIBLE
                tv_summary.visibility = View.GONE
            }
            if (summaryListThisMonth.isNotEmpty()) {
                tv_this_month.visibility = View.VISIBLE
                adapterSummaryThisMonth.addList(summaryListThisMonth)
            }
            if (summaryListLastMonth.isNotEmpty()) {
                tv_last_month.visibility = View.VISIBLE
                adapterSummaryLastMonth.addList(summaryListLastMonth)
            }
        } else {
            toast(res.message!!)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (throwable is HttpException) {
            when (throwable.code()) {
                403 -> {
                    SharedPreferenceManager.clearPreferences()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                else -> {
                    showViewState(MultiStateView.VIEW_STATE_ERROR)
                    toast(throwable.message.toString())
                }
            }
        } else {
            toast(throwable.message.toString())
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

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