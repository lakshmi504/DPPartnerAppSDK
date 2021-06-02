package com.dpdelivery.android.utils

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dpdelivery.android.R
import java.io.ByteArrayOutputStream

class CommonUtils {
    companion object {

        fun getLoginToken(): String {
            return SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.TOKEN,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String
        }

        fun saveLoginToken(string: String?) {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.TOKEN,
                string!!,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )
        }

        fun getUserName(): String {
            return SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.USER_NAME,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String
        }

        fun saveUserName(string: String?) {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.USER_NAME,
                string!!,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )
        }

        fun saveBotId(string: String?) {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.BOT_ID,
                string!!,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )
        }

        fun getBotId(): String {
            return SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.BOT_ID,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String
        }

        fun setImage(mContext: Context, imageView: ImageView, photo: String?) {
            Glide.with(mContext).load(photo)
                .apply(RequestOptions.centerInsideTransform().placeholder(R.drawable.placeholder))
                .into(imageView)
        }

        fun setUserImagebitmap(
            mContext: Context,
            imageView: ImageView,
            stream: ByteArrayOutputStream
        ) {
            Glide.with(mContext).load(stream.toByteArray())
                .apply(RequestOptions.centerCropTransform()).into(imageView)
        }

        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }

        fun saveTransactionImageName(string: String?) {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.TRANSACTION_IMAGE,
                string!!,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )

        }

        fun setCurrentVersion(version: String) {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.CURRENT_VERSION,
                version,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )

        }

        val version: String
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.CURRENT_VERSION,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String

        fun getCopyRightSymbol(context: Context, text: String): String {
            return context.getString(R.string.copyright) + text
        }

        fun setStatus(
            current: Int,
            flowlimit: Int,
            validity: String,
            status: Int,
            prepaid: Int,
            cmds: String
        ) {

            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_HASUPDATE,
                true,
                SharedPreferenceManager.VALUE_TYPE.BOOLEAN
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_CURRENT,
                current,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_FLOWLIMIT,
                flowlimit,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_VALIDITY,
                validity,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_STATUS,
                status,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_PREPAID,
                prepaid,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            )
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_CMDS,
                cmds,
                SharedPreferenceManager.VALUE_TYPE.STRING
            )
        }

        val hasUpdates: Boolean
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_HASUPDATE,
                false,
                SharedPreferenceManager.VALUE_TYPE.BOOLEAN
            ) as Boolean

        fun resetUpdate() {
            SharedPreferenceManager.setPrefVal(
                SharedPreferenceManager.KEY_HASUPDATE,
                false,
                SharedPreferenceManager.VALUE_TYPE.BOOLEAN
            )
        }

        val current: Int
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_CURRENT,
                0,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            ) as Int

        val flowlimit: Int
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_FLOWLIMIT,
                0,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            ) as Int

        val validity: String
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_VALIDITY,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String

        val purifierStatus: Int
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_STATUS,
                0,
                SharedPreferenceManager.VALUE_TYPE.INTEGER
            ) as Int

        val cmds: String
            get() = SharedPreferenceManager.getPrefVal(
                SharedPreferenceManager.KEY_CMDS,
                "",
                SharedPreferenceManager.VALUE_TYPE.STRING
            ) as String

        /**
         * Function to request permission from the user
         */
        fun requestAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestId
            )
        }

        /**
         * Function to check if the location permissions are granted or not
         */
        fun isAccessFineLocationGranted(context: Context): Boolean {
            return ContextCompat
                .checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Function to check if location of the device is enabled or not
         */
        fun isLocationEnabled(context: Context): Boolean {
            val locationManager: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        /**
         * Function to show the "enable GPS" Dialog box
         */
        fun showGPSNotEnabledDialog(context: Context) {
            AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(context.getString(R.string.enable_gps))
                .setMessage(context.getString(R.string.required_for_this_app))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        }

    }
}

inline fun <E : Any, T : Collection<E?>> T?.withNotNullNorEmpty(func: T.() -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        with(this) { func() }
    }
}