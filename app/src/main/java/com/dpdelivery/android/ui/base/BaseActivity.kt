package com.dpdelivery.android.ui.base

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.multidex.MultiDex
import com.dpdelivery.android.R
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.layout_header.*

open class BaseActivity : DaggerAppCompatActivity() {

    var appbar: AppBarLayout? = null
    lateinit var context: Context
    var toolbar: Toolbar? = null

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
        //showBack()
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

}
