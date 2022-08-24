package com.dpdelivery.android.screens.inventoryDetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techinp.DetailInventoryIp
import com.dpdelivery.android.model.techinp.SubmitInventoryIp
import com.dpdelivery.android.model.techres.CommonRes
import com.dpdelivery.android.model.techres.DetailsProductInfo
import com.dpdelivery.android.model.techres.InventoryDetailRes
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.confirmpickupinventory.ConfirmScanResultsActivity
import com.dpdelivery.android.screens.inventoryDetails.inventoryDetailsAdapter.InventoryDetailsAdapter
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.scanner.ScannerActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_detail_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import retrofit2.HttpException
import java.util.regex.Pattern
import javax.inject.Inject

class InventoryDetailsActivity : TechBaseActivity(), InventoryDetailsContract.View,
    IAdapterClickListener {

    lateinit var mContext: Context
    lateinit var pageTitle: String
    lateinit var itemName: String
    lateinit var itemId: String
    lateinit var itemCode: String
    lateinit var productId: String
    lateinit var adapterInventory: InventoryDetailsAdapter
    lateinit var progressDialog: Dialog

    @Inject
    lateinit var inventoryDetailsPresenter: InventoryDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_detail_inventory, tech_layout_container)
        init()
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        mContext = this
        intent.extras.apply {
            pageTitle = intent?.getStringExtra("title").toString()
            itemName = intent?.getStringExtra("item_name").toString()
            itemId = intent?.getStringExtra("item_id").toString()
            itemCode = intent?.getStringExtra("item_code").toString()
            setTitle(pageTitle)
        }
        setUpBottomNavView(true)
        showBack()
        tv_item_name.text = itemName
        tv_item_code.text = "Item Code #$itemCode"
        progressDialog = CommonUtils.progressDialog(this)
        getNewInventoryItems()
    }

    private fun getNewInventoryItems() {
        rv_usable_inventory.layoutManager = LinearLayoutManager(this)
        rv_usable_inventory.setHasFixedSize(true)
        rv_usable_inventory.isFocusable = false
        adapterInventory =
            InventoryDetailsAdapter(
                context,
                adapterClickListener = this,
                submissionField = pageTitle
            )
        rv_usable_inventory.adapter = adapterInventory
        when (pageTitle) {
            "Picked Up Items" -> {
                showViewState(MultiStateView.VIEW_STATE_LOADING)
                inventoryDetailsPresenter.getPickedUpInventory(
                    detailInventoryIp = DetailInventoryIp(
                        CommonUtils.getId(),
                        itemId.toInt()
                    )
                )
            }
            "To be picked up" -> {
                showViewState(MultiStateView.VIEW_STATE_LOADING)
                inventoryDetailsPresenter.getToBePickedUpInventory(
                    detailInventoryIp = DetailInventoryIp(
                        CommonUtils.getId(),
                        itemId.toInt()
                    )
                )
            }
            "To be returned" -> {
                showViewState(MultiStateView.VIEW_STATE_LOADING)
                inventoryDetailsPresenter.getReturnedInventory(
                    detailInventoryIp = DetailInventoryIp(
                        CommonUtils.getId(),
                        itemId.toInt()
                    )
                )
            }
        }
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is DetailsProductInfo) {
            when (op) {
                Constants.PICKED_UP_INVENTORY -> {
                    productId = any.id.toString()
                    IntentIntegrator(this).setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity::class.java)
                        .initiateScan()
                }
                Constants.REMOVE_INVENTORY -> {
                    val dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
                    dialog.setContentView(R.layout.dialog_logout)
                    dialog.setCancelable(true)
                    dialog.show()
                    (dialog.findViewById(R.id.dialog_title) as TextView).text =
                        "Are you sure,want to cancel?"
                    (dialog.findViewById(R.id.tv_confirm) as TextView).setOnClickListener {
                        progressDialog.show()
                        inventoryDetailsPresenter.removeInventory(
                            submitInventoryIp = SubmitInventoryIp(
                                employee_id = CommonUtils.getId(),
                                product_id = any.id
                            )
                        )
                        dialog.dismiss()
                    }
                    (dialog.findViewById(R.id.tv_cancel) as TextView).setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    override fun showRemovedInventoryRes(res: CommonRes) {
        progressDialog.dismiss()
        if (res.success) {
            init()
        } else {
            toast(res.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //We will get scan results here
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //check for null
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        this,
                        ConfirmScanResultsActivity::class.java
                    ).putExtra("result", "")
                        .putExtra("title", pageTitle).putExtra("item_name", itemName)
                        .putExtra("item_id", itemId)
                        .putExtra("item_code", itemCode)
                        .putExtra("product_id", productId)
                )
                finish()
            } else {
                val regex = "^[a-zA-Z0-9]+$"
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(result.contents)
                if (matcher.matches()) {
                    startActivity(
                        Intent(
                            this,
                            ConfirmScanResultsActivity::class.java
                        ).putExtra("result", result.contents)
                            .putExtra("title", pageTitle).putExtra("item_name", itemName)
                            .putExtra("item_id", itemId)
                            .putExtra("item_code", itemCode)
                            .putExtra("product_id", productId)
                    )
                    finish()
                } else
                    toast("Purifier ID Is Not Valid")
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showInventoryDetailsRes(res: InventoryDetailRes) {
        if (res.success) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            if (res.body.product_info.isNotEmpty()) {
                adapterInventory.addList(res.body.product_info, pageTitle)
            } else {
                showViewState(MultiStateView.VIEW_STATE_EMPTY)
                empty_button.visibility = View.GONE
            }
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

    override fun onResume() {
        super.onResume()
        inventoryDetailsPresenter.takeView(this)
        bottom_navigation.menu.getItem(0).isCheckable = false
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