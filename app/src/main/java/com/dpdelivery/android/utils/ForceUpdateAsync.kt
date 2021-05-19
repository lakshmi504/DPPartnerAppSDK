package com.dpdelivery.android.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.dpdelivery.android.R
import com.dpdelivery.android.ui.splash.SplashActivity
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class ForceUpdateAsync(private val currentVersion: String, private val context: Context) : AsyncTask<String, String, JSONObject>() {
    private var latestVersion: String? = null
    private var pendingTransactionDialog: Dialog? = null


    override fun doInBackground(vararg params: String): JSONObject {
        try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.packageName + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun onPostExecute(jsonObject: JSONObject) {

        if (!currentVersion.equals(latestVersion, ignoreCase = true) && !latestVersion.isNullOrEmpty()) {
            if (context !is SplashActivity) {
                if (!(context as Activity).isFinishing) {
                    showForceUpdateDialog()
                }
            }
        }
        super.onPostExecute(jsonObject)
    }

    private fun showForceUpdateDialog() {

        pendingTransactionDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        pendingTransactionDialog!!.setContentView(R.layout.dialog_alert_message)
        pendingTransactionDialog!!.setCancelable(false)
        pendingTransactionDialog!!.setCanceledOnTouchOutside(false)

        (pendingTransactionDialog!!.findViewById(R.id.btn_update) as AppCompatButton).setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName)))
            pendingTransactionDialog!!.dismiss()
        }
        (pendingTransactionDialog!!.findViewById(R.id.iv_close) as ImageView).visibility = View.INVISIBLE
        pendingTransactionDialog!!.show()
    }

}
