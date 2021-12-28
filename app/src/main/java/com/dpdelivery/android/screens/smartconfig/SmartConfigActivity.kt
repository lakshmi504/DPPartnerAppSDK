package com.dpdelivery.android.screens.smartconfig

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.dpdelivery.android.MyApplication
import com.dpdelivery.android.R
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchListener
import com.espressif.iot.esptouch.IEsptouchResult
import com.espressif.iot.esptouch.IEsptouchTask
import com.espressif.iot.esptouch.util.ByteUtil
import com.espressif.iot.esptouch.util.TouchNetUtil
import kotlinx.android.synthetic.main.activity_smart_config.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class SmartConfigActivity : SmartConfigAbs() {

    private val TAG: String = SmartConfigActivity::class.java.simpleName

    private val REQUEST_PERMISSION = 0x01

    private var mTask: EspTouchAsyncTask4? = null

    private var mSsid: String? = null
    private var mSsidBytes: ByteArray? = null
    private var mBssid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_config)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        confirmBtn.setOnClickListener {
            executeEspTouch()
        }
        cancel_button.setOnClickListener {
            showProgress(false)
            if (mTask != null) {
                mTask!!.cancelEspTouch()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, REQUEST_PERMISSION)
        }

       /* MyApplication().getInstance().observeBroadcast(this, { broadcast ->
            Log.d(TAG, "onCreate: Broadcast=$broadcast")
            onWifiChanged()
        })*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onWifiChanged()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.esptouch1_location_permission_title)
                    .setMessage(R.string.esptouch1_location_permission_message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { dialog, which -> finish() }
                    .show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            content.visibility = View.INVISIBLE
            progressView.visibility = View.VISIBLE
        }
    }

    private fun check(): StateResult {
        var result: StateResult = checkPermission()
        if (!result.permissionGranted) {
            return result
        }
        result = checkLocation()
        result.permissionGranted = true
        if (result.locationRequirement) {
            return result
        }
        result = checkWifi()
        result.permissionGranted = true
        result.locationRequirement = false
        return result
    }

    private fun onWifiChanged() {
        val stateResult = check()
        mSsid = stateResult.ssid
        mSsidBytes = stateResult.ssidBytes
        mBssid = stateResult.bssid
        var message = stateResult.message
        var confirmEnable = false
        if (stateResult.wifiConnected) {
            confirmEnable = true
            if (stateResult.is5G) {
                message = getString(R.string.esptouch1_wifi_5g_message)
            }
        } else {
            if (mTask != null) {
                mTask!!.cancelEspTouch()
                mTask = null
                AlertDialog.Builder(this)
                    .setMessage(R.string.esptouch1_configure_wifi_change_message)
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
        apSsidText.text = mSsid
        apBssidText.text = mBssid
        messageView.text = message
        confirmBtn.isEnabled = confirmEnable
    }

    private fun executeEspTouch() {
        val ssid = if (mSsidBytes == null) ByteUtil.getBytesByString(mSsid) else mSsidBytes
        val pwdStr: CharSequence = apPasswordEdit.text.toString()
        val password = if (pwdStr == null) null else ByteUtil.getBytesByString(pwdStr.toString())
        val bssid = TouchNetUtil.parseBssid2bytes(mBssid)
        val devCountStr: CharSequence = deviceCountEdit.text.toString()
        val deviceCount = devCountStr.toString().toByteArray() ?: ByteArray(0)
        val broadcast =
            byteArrayOf((if (packageModeGroup.checkedRadioButtonId === R.id.packageBroadcast) 1 else 0).toByte())
        if (mTask != null) {
            mTask!!.cancelEspTouch()
        }
        mTask = EspTouchAsyncTask4(this)
        mTask!!.execute(ssid, bssid, password, deviceCount, broadcast)
    }

    private class EspTouchAsyncTask4 constructor(activity: SmartConfigActivity) :
        AsyncTask<ByteArray?, IEsptouchResult?, List<IEsptouchResult>?>() {
        private var mActivity: WeakReference<SmartConfigActivity>? = null
        private val mLock = Any()
        private var mResultDialog: androidx.appcompat.app.AlertDialog? = null
        private var mEsptouchTask: IEsptouchTask? = null

        fun cancelEspTouch() {
            cancel(true)
            val activity: SmartConfigActivity? = mActivity?.get()
            activity?.showProgress(false)
            if (mResultDialog != null) {
                mResultDialog!!.dismiss()
            }
            if (mEsptouchTask != null) {
                mEsptouchTask!!.interrupt()
            }
        }

        override fun onPreExecute() {
            val activity: SmartConfigActivity? = mActivity?.get()
            activity!!.testResult.text = ""
            activity.showProgress(true)
        }

        override fun onProgressUpdate(vararg values: IEsptouchResult?) {
            val activity: SmartConfigActivity? = mActivity?.get()
            if (activity != null) {
                val result = values[0]
                Log.i(
                    "TAG",
                    "EspTouchResult: $result"
                )
                val text = result!!.bssid + " is connected to the wifi"
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
                activity.testResult.append(
                    String.format(
                        Locale.ENGLISH,
                        "%s,%s\n",
                        result.inetAddress.hostAddress,
                        result.bssid
                    )
                )
            }
        }

        override fun doInBackground(vararg params: ByteArray?): List<IEsptouchResult>? {
            val activity: SmartConfigActivity? = mActivity?.get()
            var taskResultCount: Int
            synchronized(mLock) {
                val apSsid = params[0]
                val apBssid = params[1]
                val apPassword = params[2]
                val deviceCountData = params[3]
                val broadcastData = params[4]
                taskResultCount =
                    if (deviceCountData!!.isEmpty()) -1 else String(deviceCountData).toInt()
                val context: Context = activity!!.applicationContext
                mEsptouchTask = EsptouchTask(apSsid, apBssid, apPassword, context)
                (mEsptouchTask as EsptouchTask).setPackageBroadcast(broadcastData!![0].toInt() == 1)
                (mEsptouchTask as EsptouchTask).setEsptouchListener(IEsptouchListener { values: IEsptouchResult? ->
                    publishProgress(
                        values
                    )
                })
            }
            return mEsptouchTask!!.executeForResults(taskResultCount)
        }

        override fun onPostExecute(result: List<IEsptouchResult>?) {
            val activity: SmartConfigActivity? = mActivity?.get()
            activity!!.mTask = null
            var wifiBotId: String? = null
            activity.showProgress(false)
            if (result == null) {
                mResultDialog = androidx.appcompat.app.AlertDialog.Builder(activity)
                    .setMessage(R.string.esptouch1_configure_result_failed_port)
                    .setPositiveButton(R.string.ok, null)
                    .show()
                mResultDialog!!.setCanceledOnTouchOutside(false)
                return
            }
            Log.d("result", result.toString())
            // check whether the task is cancelled and no results received
            val firstResult = result[0]
            if (firstResult.isCancelled) {
                return
            }
            // the task received some results including cancelled while
            // executing before receiving enough results
            if (!firstResult.isSuc) {
                mResultDialog = androidx.appcompat.app.AlertDialog.Builder(activity)
                    .setMessage(R.string.esptouch1_configure_result_failed)
                    .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            activity.finish()
                        })
                    .show()
                mResultDialog!!.setCanceledOnTouchOutside(false)
                return
            }
            val resultMsgList = ArrayList<CharSequence>(result.size)
            for (touchResult in result) {
                val message: String = activity.getString(
                    R.string.esptouch1_configure_result_success_item,
                    touchResult.bssid, touchResult.inetAddress.hostAddress
                )
                wifiBotId = touchResult.bssid
                resultMsgList.add(message)
            }
            val items = arrayOfNulls<CharSequence>(resultMsgList.size)
            mResultDialog = androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle(R.string.esptouch1_configure_result_success)
                .setItems(resultMsgList.toArray(items), null)
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, which ->
                        activity.finish()
                    })
                .show()
            mResultDialog!!.setCanceledOnTouchOutside(false)
        }

        init {
            mActivity = WeakReference<SmartConfigActivity>(activity)
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