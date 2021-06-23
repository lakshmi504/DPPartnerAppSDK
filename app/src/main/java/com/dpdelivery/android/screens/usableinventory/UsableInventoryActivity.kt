package com.dpdelivery.android.screens.usableinventory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.confirmresults.ConfirmScanResultsActivity
import com.dpdelivery.android.screens.scanner.ScannerActivity
import com.dpdelivery.android.utils.toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_usable_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import java.util.regex.Pattern

class UsableInventoryActivity : TechBaseActivity(), IAdapterClickListener {

    lateinit var mContext: Context
    lateinit var pageTitle: String
    lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_usable_inventory, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        intent.extras.apply {
            pageTitle = intent?.getStringExtra("title").toString()
            itemName = intent?.getStringExtra("item_name").toString()
            setTitle(pageTitle)
        }
        setUpBottomNavView(true)
        showBack()
        tv_item_name.text = itemName
        getNewInventoryItems()
    }

    private fun getNewInventoryItems() {
        rv_usable_inventory.layoutManager = LinearLayoutManager(this)
        rv_usable_inventory.setHasFixedSize(true)
        rv_usable_inventory.isFocusable = false
        val adapterInventory =
            BasicAdapter(context, R.layout.item_usable_inventory, adapterClickListener = this)
        rv_usable_inventory.adapter = adapterInventory
        val inventoryList = ArrayList<UsableInventoryModel>()
        when (pageTitle) {
            "Picked Up Items" -> {
                inventoryList.add(UsableInventoryModel(itemName = "SYNCTEST20"))
                inventoryList.add(UsableInventoryModel(itemName = "DRINKPRIME"))
                adapterInventory.addList(inventoryList)
            }
            "To be picked up" -> {
                inventoryList.add(UsableInventoryModel(itemName = ""))
                inventoryList.add(UsableInventoryModel(itemName = ""))
                adapterInventory.addList(inventoryList)
            }
            "To be returned" -> {
                inventoryList.add(UsableInventoryModel(itemName = "SYNCTEST20"))
                inventoryList.add(UsableInventoryModel(itemName = "DRINKPRIME"))
                adapterInventory.addList(inventoryList)
            }
        }
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is UsableInventoryModel) {
            when (op) {
                Constants.PICKED_UP_INVENTORY -> {
                    IntentIntegrator(this).setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity::class.java)
                        .initiateScan()
                }
                Constants.RETURNABLE_INVENTORY -> {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //We will get scan results here
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //check for null
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        //bottom_navigation.selectedItemId = R.id.action_inventory
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