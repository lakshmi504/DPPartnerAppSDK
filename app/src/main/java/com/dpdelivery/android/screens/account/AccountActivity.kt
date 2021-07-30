package com.dpdelivery.android.screens.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techres.AccountModel
import com.dpdelivery.android.model.techres.PartnerDetailsRes
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.DateHelper
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AccountActivity : TechBaseActivity(), AccountContract.View {

    lateinit var mContext: Context
    lateinit var accountList: ArrayList<String>

    @Inject
    lateinit var presenter: AccountPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_account, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Account")
        setUpBottomNavView(false)
        showBack()
        error_button.setOnClickListener {
            getPartnerDetails()
        }
        getPartnerDetails()
    }

    private fun getRecyclerViewData() {
        rv_account.layoutManager = LinearLayoutManager(this)
        rv_account.setHasFixedSize(true)
        rv_account.isFocusable = false
        accountList = ArrayList<String>()
        //accountList.add(Constants.MY_EARNINGS)
        accountList.add(Constants.SUMMARY)

        val accountAdapter = AccountAdapter(mContext, AccountModel(accountList, "Account"))
        rv_account.adapter = accountAdapter
    }

    private fun getPartnerDetails() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        presenter.getPartnerDetails()
    }

    override fun showPartnerDetails(res: PartnerDetailsRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        tv_name.text = res.firstname
        tv_mobile_no.text = res.phone
        getRecyclerViewData()
        showAppVersion()
    }

    private fun showAppVersion() {
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

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_ERROR)
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


    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
