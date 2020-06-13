package com.dpdelivery.android.ui.deliveryjob

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.ZoomageView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_images.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.layout_header.*

class ImagesActivity : BaseActivity() {

    lateinit var mContext: Context
    private var payImage: String? = null
    private var deliveryImage: String? = null
    private var dialog: Dialog? = null
    private var image: ZoomageView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_images, layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("Uploaded Images")
        setUpBottomNavView(false)
        toolbar_title.textSize = 20f
        showBack()
        if (intent != null) {
            payImage = intent.getStringExtra(Constants.PAYMENT_IMAGE)
            deliveryImage = intent.getStringExtra(Constants.DELIVERED_IMAGE)
        }
        showImages()
    }

    private fun showImages() {
        if (payImage!!.isNotEmpty()) {
            CommonUtils.setImage(mContext, iv_payment, payImage)
            iv_payment.setOnClickListener {
                dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
                dialog!!.setContentView(R.layout.layout_image_zoom)
                dialog!!.setCancelable(true)
                dialog!!.show()
                (dialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
                    dialog!!.dismiss()
                }
                image = (dialog!!.findViewById(R.id.iv_image) as? ZoomageView)
                CommonUtils.setImage(mContext, image!!, payImage)
            }
        } else {
            iv_payment.visibility = View.INVISIBLE
        }
        if (deliveryImage!!.isNotEmpty()) {
            CommonUtils.setImage(mContext, iv_delivery, deliveryImage)
            iv_delivery.setOnClickListener {
                dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
                dialog!!.setContentView(R.layout.layout_image_zoom)
                dialog!!.setCancelable(true)
                (dialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
                    dialog!!.dismiss()
                }
                image = (dialog!!.findViewById(R.id.iv_image) as? ZoomageView)
                CommonUtils.setImage(mContext, image!!, deliveryImage)
                dialog!!.show()
            }
        } else {
            iv_delivery.visibility = View.INVISIBLE
        }
        if (payImage!!.isEmpty() && deliveryImage!!.isEmpty()) {
            iv_payment.visibility = View.GONE
            iv_delivery.visibility = View.GONE
            tv_image_text.visibility = View.VISIBLE
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
