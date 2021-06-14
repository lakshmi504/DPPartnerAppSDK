package com.dpdelivery.android.screens.confirmresults

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.dpdelivery.android.R
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.usableinventory.UsableInventoryActivity
import kotlinx.android.synthetic.main.activity_confirm_scan_results.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*

class ConfirmScanResultsActivity : TechBaseActivity() {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_confirm_scan_results, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("Usable Inventory")
        setUpBottomNavView(false)
        showBack()
        intent.extras.apply {
            val content = intent.getStringExtra("result")
            tv_result.text = content
        }
        btn_confirm.setOnClickListener {
            startActivity(Intent(this, UsableInventoryActivity::class.java))
            finish()
        }
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