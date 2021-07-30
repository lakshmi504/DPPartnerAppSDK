package com.dpdelivery.android.screens.payout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.EarningsRes
import com.dpdelivery.android.model.techres.EntryWM
import com.dpdelivery.android.screens.account.AccountActivity
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_detail_earnings.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DetailEarningsActivity : TechBaseActivity(), IAdapterClickListener, View.OnClickListener,
    EarningsContract.View {

    lateinit var mContext: Context

    @Inject
    lateinit var presenter: EarningsPresenter
    private var adapterDetailEarnings: BasicAdapter? = null
    private var earningsList: ArrayList<EntryWM>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_detail_earnings, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(true)
        getDetailEarnings()
        iv_logout.visibility = View.VISIBLE
        iv_account.visibility = View.VISIBLE
        iv_calender.setOnClickListener(this)
        iv_logout.setOnClickListener(this)
        iv_account.setOnClickListener(this)
        error_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            //date range picker
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
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getEarningsList(startDateString, endDateString)
                }
            }
            R.id.iv_logout -> {
                logOut()
            }
            R.id.iv_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
            }
            R.id.error_button -> {
                init()
            }
        }
    }

    private fun getDetailEarnings() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        rv_detail_earnings.layoutManager = LinearLayoutManager(this)
        adapterDetailEarnings =
            BasicAdapter(context, R.layout.item_detail_earnings, adapterClickListener = this)
        rv_detail_earnings.adapter = adapterDetailEarnings
        adapterDetailEarnings!!.notifyDataSetChanged()
        presenter.getEarningsList(startDate = "", endDate = "")
    }

    @SuppressLint("SetTextI18n")
    override fun showEarningsRes(res: EarningsRes) {
        if (res.entryWMList.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.entryWMList.withNotNullNorEmpty {
                earningsList = res.entryWMList
                adapterDetailEarnings!!.addList(earningsList!!)
            }
            //setting up the data
            tv_name.text = "${CommonUtils.getName()}, You are doing great!"
            tv_total.text = res.totalAmount.toString()
            tv_credit.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            tv_debit.setTextColor(ContextCompat.getColor(context, R.color.color_red))
            //colour based on data
            if (res.credit > 0)
                tv_credit.text = CommonUtils.getRupeesSymbol(context, res.credit)
            else
                tv_credit.text = res.credit.toInt().toString()

            if (res.debit > 0)
                tv_debit.text = CommonUtils.getMinusRupeesSymbol(context, res.debit)
            else
                tv_debit.text = res.debit.toInt().toString()

        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_button.visibility = View.GONE
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
            showViewState(MultiStateView.VIEW_STATE_ERROR)
            toast(throwable.message.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        bottom_navigation.selectedItemId = R.id.action_earnings
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TechJobsListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

}