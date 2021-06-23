package com.dpdelivery.android.screens.inventory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.account.AccountActivity
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.screens.usableinventory.UsableInventoryActivity
import com.dpdelivery.android.utils.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*


class InventoryActivity : TechBaseActivity(), IAdapterClickListener, View.OnClickListener {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_inventory, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        iv_account.visibility = View.VISIBLE
        iv_logout.visibility = View.VISIBLE
        iv_search.visibility = View.VISIBLE
        iv_logout.setOnClickListener(this)
        iv_account.setOnClickListener(this)
        setUpBottomNavView(true)
        getInventoryItems()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_logout -> {
                SharedPreferenceManager.clearPreferences()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
            R.id.iv_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
            }
        }
    }

    private fun getInventoryItems() {
        /*if (CommonUtils.getRole() == "Technician") {
            et_search.visibility = View.VISIBLE
        }*/
        rv_inventory.layoutManager = LinearLayoutManager(this)
        rv_inventory.setHasFixedSize(true)
        val adapterInventory =
            BasicAdapter(context, R.layout.item_inventory, adapterClickListener = this)
        rv_inventory.adapter = adapterInventory
        val inventoryList = ArrayList<InventoryModel>()
        inventoryList.add(
            InventoryModel(
                name = "Purifier-100001",
                picked_up = "4",
                to_be_pick_up = "6",
                to_be_Returned = "0"
            )
        )
        inventoryList.add(
            InventoryModel(
                name = "Purifier-100001",
                picked_up = "0",
                to_be_pick_up = "4",
                to_be_Returned = "2"
            )
        )
        inventoryList.add(
            InventoryModel(
                name = "Purifier-100001",
                picked_up = "4",
                to_be_pick_up = "0",
                to_be_Returned = "4"
            )
        )
        adapterInventory.addList(inventoryList)
    }

    override fun onResume() {
        super.onResume()
        bottom_navigation.selectedItemId = R.id.action_inventory
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is InventoryModel) {
            when (op) {
                Constants.PICKED_UP_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            UsableInventoryActivity::class.java
                        ).putExtra("title", "Picked Up Items").putExtra("item_name", any.name)
                    )
                }
                Constants.TO_BE_PICKED_UP_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            UsableInventoryActivity::class.java
                        ).putExtra("title", "To be picked up").putExtra("item_name", any.name)
                    )
                }
                Constants.RETURNABLE_INVENTORY -> {
                    startActivity(
                        Intent(
                            this,
                            UsableInventoryActivity::class.java
                        ).putExtra("title", "To be returned").putExtra("item_name", any.name)
                    )
                }
            }
        }
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