package com.dpdelivery.android.technicianui.finish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.photo.ImageActivity
import com.dpdelivery.android.ui.location.MapLocationActivity
import com.dpdelivery.android.utils.setDrawableLeft
import kotlinx.android.synthetic.main.activity_finish_job.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*


class FinishJobActivity : TechBaseActivity(), View.OnClickListener {

    lateinit var mContext: Context
    private var address: String? = null
    private val LOCATION_REQUEST_CODE = 6
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var isLocationSet: Boolean = false
    private val PHOTO_REQUEST_CODE = 1
    private var jobId: Int = 0
    private val TAG = FinishJobActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_finish_job, tech_layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("Finish Job")
        showBack()
        setUpBottomNavView(false)
        btn_location.setOnClickListener(this)
        btn_happy_code.setOnClickListener(this)
        btn_photo.setOnClickListener(this)
        btn_add_parts.setOnClickListener(this)
        btn_tds.setOnClickListener(this)
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_location -> {
                val intent = Intent(context, MapLocationActivity::class.java)
                startActivityForResult(intent, LOCATION_REQUEST_CODE)
            }
            R.id.btn_happy_code -> {

            }
            R.id.btn_photo -> {
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                startActivityForResult(intent, PHOTO_REQUEST_CODE)
            }
            R.id.btn_add_parts -> {

            }
            R.id.btn_tds -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            address = data!!.getStringExtra("address")
            latitude = data.getDoubleExtra("latitude", 0.00)
            longitude = data.getDoubleExtra("longitude", 0.00)
            Log.i(TAG, "LAT:$latitude .LONG$longitude")
            locationtxt!!.text = address
            locationtxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            isLocationSet = true
        } else if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            phototxt.text = getString(R.string.uploaded_photo)
            phototxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
