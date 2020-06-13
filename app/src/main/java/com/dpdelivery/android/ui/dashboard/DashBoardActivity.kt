package com.dpdelivery.android.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.*
import android.widget.TextView
import com.dpdelivery.android.R
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.app_bar_base.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DashBoardActivity : BaseActivity() {

    lateinit var mContext: Context
    //lateinit var dialog: Dialog
    private var loading_text: TextView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_dash_board, layout_container)
        init()
    }

    private fun init() {
        mContext = this
        setTitle("DP Delivery")
        //dialog = showProgress(context)
        setUpBottomNavView(true)

       /* webView.visibility = WebView.INVISIBLE
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setSupportZoom(false)
        webView.settings.builtInZoomControls = false
        webView.isHorizontalScrollBarEnabled = false
        // webView.settings.userAgentString = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36"
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.v("onPageStarted", "onPageStarted")
                try {
                    if (!isFinishing) {
                        dialog.show()
                    }
                } catch (e: WindowManager.BadTokenException) {
                    Log.v("onPageStarted", "exception")
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val headerMap = HashMap<String, String>()
                headerMap["Authorization"] = CommonUtils.getLoginToken()
                webView.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.v("onPageFinished", "onPageFinished")
                dialog.dismiss()
                webView.visibility = WebView.VISIBLE
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.v("onReceivedError", "onReceivedError")
            }
            *//*override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                try {
                    val httpClient = OkHttpClient()
                    val request = Request.Builder()
                            .url(url)
                            .addHeader("Authorization", CommonUtils.getLoginToken())
                            .build()
                    val response = httpClient.newCall(request).execute()
                    return WebResourceResponse(
                            response.header("content-type", response.body()!!.contentType()!!.type()), // You can set something other as default content-type
                            response.header("content-encoding", "charset=UTF-8"), // Again, you can set another encoding as default
                            response.body()!!.byteStream()
                    )
                } catch (e: IOException) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    return null
                }
            }*//*
        }

        val myPdfUrl = "http://staging.waterwalaprime.in/app/operation/dashboard"
          val headerMap = HashMap<String, String>()
          headerMap["Authorization"] = CommonUtils.getLoginToken()
        webView.loadUrl(myPdfUrl)*/
    }

    private fun showProgress(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        loading_text = dialog.findViewById(R.id.loading_text) as? TextView
        loading_text?.text = "Loading Please Wait.."
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onResume() {
        super.onResume()
        bottom_navigation.selectedItemId = R.id.action_home
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
