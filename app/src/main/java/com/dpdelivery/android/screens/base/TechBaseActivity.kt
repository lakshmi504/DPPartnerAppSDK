package com.dpdelivery.android.screens.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.multidex.MultiDex
import com.dpdelivery.android.MyApplication
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.BottomNavigationViewHelper
import com.dpdelivery.android.screens.inventory.InventoryActivity
import com.dpdelivery.android.screens.payout.DetailEarningsActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.screens.usableinventory.UsableInventoryActivity
import com.dpdelivery.android.utils.makeVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import javax.inject.Inject


open class TechBaseActivity : DaggerAppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    var appbar: AppBarLayout? = null
    lateinit var context: Context
    var toolbar: Toolbar? = null

    @Inject
    lateinit var myApp: MyApplication

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tech_base)
        MultiDex.install(this)
        initialize()
    }

    fun initialize() {
        context = this
        appbar = findViewById<View>(R.id.appbar) as AppBarLayout
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun showBack() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
    }

    fun setTitle(title: String?) {
        if (title != null && !TextUtils.isEmpty(title) && title.isNotEmpty()) {
            toolbar_title!!.visibility = View.VISIBLE
            toolbar_title!!.text = title
        } else {
            toolbar_title!!.visibility = View.GONE
        }
    }

    fun setUpBottomNavView(needToShow: Boolean = true) {
        BottomNavigationViewHelper.removeShiftMode(bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.makeVisible(needToShow)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        /* removeItemsUnderline(bottom_navigation); // remove underline from all items
         underlineMenuItem(menuItem); // underline selected item*/
        when (menuItem.itemId) {
            R.id.action_jobs -> {
                if (!myApp.currAct.contentEquals(TechJobsListActivity::class.java.simpleName)) {
                    intent = Intent(this, TechJobsListActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            }

            R.id.action_inventory -> {
                if (!myApp.currAct.contentEquals(InventoryActivity::class.java.simpleName)) {
                    intent = Intent(this, InventoryActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            }

            R.id.action_earnings -> {
                if (!myApp.currAct.contentEquals(DetailEarningsActivity::class.java.simpleName)) {
                    intent = Intent(this, DetailEarningsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            }
        }
        return true
    }

    private fun removeItemsUnderline(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size()) {
            val item = bottomNavigationView.menu.getItem(i)
            item.title = item.title.toString()
        }
    }

    private fun underlineMenuItem(item: MenuItem) {
        val content = SpannableString(item.title)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        item.title = content
    }
}
