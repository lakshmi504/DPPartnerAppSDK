package com.dpdelivery.android.screens.inventory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.returnableInventory.ReturnableInventoryActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.screens.usableinventory.UsableInventoryActivity
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*


class InventoryActivity : TechBaseActivity(), IAdapterClickListener {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_inventory, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("Inventory")
        setUpBottomNavView(true)
        getInventoryItems()
    }

    private fun getInventoryItems() {
        if (CommonUtils.getRole() == "Technician") {
            et_search.visibility = View.VISIBLE
        }
        rv_inventory.layoutManager = LinearLayoutManager(this)
        rv_inventory.setHasFixedSize(true)
       /* rv_inventory.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )*/
        val adapterInventory =
            BasicAdapter(context, R.layout.item_inventory, adapterClickListener = this)
        rv_inventory.adapter = adapterInventory
        val inventoryList = ArrayList<InventoryModel>()
        inventoryList.add(
            InventoryModel(
                name = "Purifier-100001",
                new = "Usable : 4",
                toReturn = "Returnable : 0"
            )
        )
        adapterInventory.addList(inventoryList)
    }

    override fun onResume() {
        super.onResume()
        bottom_navigation.selectedItemId = R.id.action_inventory
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TechJobsListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is InventoryModel) {
            when (op) {
                Constants.USABLE_INVENTORY -> {
                    startActivity(Intent(this, UsableInventoryActivity::class.java))
                }
                Constants.RETURNABLE_INVENTORY -> {
                    startActivity(Intent(this, ReturnableInventoryActivity::class.java))
                }
            }
        }
    }
}