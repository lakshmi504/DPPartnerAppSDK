package com.dpdelivery.android.screens.confirmpickupinventory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.model.techres.CommonRes
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.inventoryDetails.InventoryDetailsActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_confirm_scan_results.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
import javax.inject.Inject

class ConfirmScanResultsActivity : TechBaseActivity(), ConfirmPickUpContract.View {

    lateinit var mContext: Context
    lateinit var pageTitle: String
    lateinit var content: String
    lateinit var itemName: String
    lateinit var itemId: String
    lateinit var itemCode: String
    lateinit var productId: String

    @Inject
    lateinit var confirmPickUpPresenter: ConfirmPickUpPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_confirm_scan_results, tech_layout_container)
        init()
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        mContext = this
        setTitle("Confirm Inventory")
        setUpBottomNavView(true)
        showBack()
        intent.extras.apply {
            content = intent?.getStringExtra("result").toString()
            pageTitle = intent?.getStringExtra("title").toString()
            itemName = intent?.getStringExtra("item_name").toString()
            itemId = intent?.getStringExtra("item_id").toString()
            itemCode = intent?.getStringExtra("item_code").toString()
            productId = intent?.getStringExtra("product_id").toString()
            tv_item_name.text = itemName
            tv_item_id.text = "Item Code #$itemCode"
            if (content.isNotEmpty()) {
                et_result.setText(content)
                et_result.isEnabled = false
            } else {
                et_result.hint = "Enter Device Code"
                content = et_result.text.toString()
            }
        }
        btn_confirm.setOnClickListener {
            if (et_result.text.toString().isNotEmpty()) {
                showViewState(MultiStateView.VIEW_STATE_LOADING)
                confirmPickUpPresenter.confirmInventory(
                    submitInventoryIp = SubmitInventoryIp(
                        employee_id = CommonUtils.getId(),
                        product_id = productId.toInt(),
                        qr_code = content
                    )
                )
            } else {
                toast("Please enter device code")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        confirmPickUpPresenter.takeView(this)
        bottom_navigation.menu.getItem(0).isCheckable = false
    }

    override fun showConfirmedPickUpRes(res: CommonRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success) {
            startActivity(
                Intent(this, InventoryDetailsActivity::class.java).putExtra(
                    "title",
                    "Picked Up Items"
                ).putExtra("item_name", itemName)
                    .putExtra("item_id", itemId)
                    .putExtra("item_code", itemCode)
            )
            finish()
        } else {
            toast(res.message)
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
            error_textView.text = throwable.message
        }
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
}