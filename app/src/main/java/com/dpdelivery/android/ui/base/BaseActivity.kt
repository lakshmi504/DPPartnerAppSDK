package com.dpdelivery.android.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.multidex.MultiDex
import com.dpdelivery.android.MyApplication
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.BottomNavigationViewHelper
import com.dpdelivery.android.ui.dashboard.DashBoardActivity
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobListActivity
import com.dpdelivery.android.utils.makeVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.layout_header.*
import javax.inject.Inject

open class BaseActivity : DaggerAppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_base)
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

    fun setUpBottomNavView(needToShow: Boolean = true) {
        BottomNavigationViewHelper.removeShiftMode(bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.makeVisible(needToShow)
    }

    fun setTitle(title: String?) {
        if (title != null && !TextUtils.isEmpty(title) && title.isNotEmpty()) {
            toolbar_title!!.visibility = View.VISIBLE
            toolbar_title!!.text = title
        } else {
            toolbar_title!!.visibility = View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                if (!myApp.currAct.contentEquals(DashBoardActivity::class.java.simpleName)) {
                    intent = Intent(this, DashBoardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            }
            R.id.action_jobs_list -> {
                if (!myApp.currAct.contentEquals(DeliveryJobListActivity::class.java.simpleName)) {
                    intent = Intent(this, DeliveryJobListActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            }
        }
        return true
    }
}
