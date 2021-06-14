package com.dpdelivery.android.screens.returnableInventory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.base.TechBaseActivity
import kotlinx.android.synthetic.main.activity_returnable_inventory.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*

class ReturnableInventoryActivity : TechBaseActivity(), IAdapterClickListener {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_returnable_inventory, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("Returnable Inventory")
        setUpBottomNavView(false)
        showBack()
        getReturnInventoryItems()
    }

    private fun getReturnInventoryItems() {
        rv_returnable_inventory.layoutManager = LinearLayoutManager(this)
        rv_returnable_inventory.setHasFixedSize(true)
        rv_returnable_inventory.isFocusable = false
        val adapterInventory =
            BasicAdapter(context, R.layout.item_returnable_inventory, adapterClickListener = this)
        rv_returnable_inventory.adapter = adapterInventory
        val inventoryList = ArrayList<String>()
        inventoryList.add("")
        inventoryList.add("")
        inventoryList.add("")
        inventoryList.add("")
        adapterInventory.addList(inventoryList)
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