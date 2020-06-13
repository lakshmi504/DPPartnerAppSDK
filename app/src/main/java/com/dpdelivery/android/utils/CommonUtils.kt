package com.dpdelivery.android.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.util.Base64
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dpdelivery.android.R
import com.dpdelivery.android.api.ApiConstants
import com.dpdelivery.android.interfaces.SelectedDateListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class CommonUtils {
    companion object {
        private var dateListener: SelectedDateListener? = null

        fun getLoginToken(): String {
            return SharedPreferenceManager.getPrefVal(SharedPreferenceManager.TOKEN, "", SharedPreferenceManager.VALUE_TYPE.STRING) as String
        }

        fun saveLoginToken(string: String?) {
            SharedPreferenceManager.setPrefVal(SharedPreferenceManager.TOKEN, string!!, SharedPreferenceManager.VALUE_TYPE.STRING)
        }

        fun saveRole(string: String?) {
            SharedPreferenceManager.setPrefVal(SharedPreferenceManager.ROLE, string!!, SharedPreferenceManager.VALUE_TYPE.STRING)
        }

        fun getRole(): String {
            return SharedPreferenceManager.getPrefVal(SharedPreferenceManager.ROLE, "", SharedPreferenceManager.VALUE_TYPE.STRING) as String
        }

        fun showdatepicker(context: Context, listener: SelectedDateListener, dateValue: Long, s: String) {
            dateListener = listener
            val mYear: Int
            val mMonth: Int
            val mDay: Int
            val cal = Calendar.getInstance()
            mYear = cal.get(Calendar.YEAR)
            mMonth = cal.get(Calendar.MONTH)
            mDay = cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(context, R.style.MyDatePickerDialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        dateListener!!.setSelectedDate(("" + dayOfMonth.toString() + "-" + (monthOfYear + 1).toString() + "-" + year), s)
                    }, mDay, mMonth, mYear)
            datePickerDialog.datePicker.minDate = cal.timeInMillis
            datePickerDialog.show()
        }

        fun getCurrentLocationAddress(context: Context, latitude: Double, longitude: Double): Address? {
            var fetchedAddress: Address? = null
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Geocoder.isPresent()) {
                try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                    if (addresses != null && addresses.size > 0) {
                        fetchedAddress = addresses[0]
                        val strAddress = StringBuilder()
                        for (i in 0 until fetchedAddress!!.maxAddressLineIndex) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return fetchedAddress
        }

        fun setImage(mContext: Context, imageView: ImageView, photo: String?) {
            Glide.with(mContext).load(photo)
                    .apply(RequestOptions.centerInsideTransform().placeholder(R.drawable.placeholder))
                    .into(imageView)
        }

        fun setUserImagebitmap(mContext: Context, imageView: ImageView, stream: ByteArrayOutputStream) {
            Glide.with(mContext).load(stream.toByteArray()).apply(RequestOptions.centerCropTransform()).into(imageView)
        }

        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT))
            return dialog
        }

        fun saveTransactionImageName(string: String?) {
            SharedPreferenceManager.setPrefVal(SharedPreferenceManager.TRANSACTION_IMAGE, string!!, SharedPreferenceManager.VALUE_TYPE.STRING)

        }

        fun getTransactionImageName(): String {
            return SharedPreferenceManager.getPrefVal(SharedPreferenceManager.TRANSACTION_IMAGE, "", SharedPreferenceManager.VALUE_TYPE.STRING) as String
        }

        fun saveDeliveredImageName(string: String?) {
            SharedPreferenceManager.setPrefVal(SharedPreferenceManager.DELIVERED_IMAGE, string!!, SharedPreferenceManager.VALUE_TYPE.STRING)

        }

        fun getDeliveredImageName(): String {
            return SharedPreferenceManager.getPrefVal(SharedPreferenceManager.DELIVERED_IMAGE, "", SharedPreferenceManager.VALUE_TYPE.STRING) as String
        }
    }
}

inline fun <E : Any, T : Collection<E?>> T?.withNotNullNorEmpty(func: T.() -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        with(this) { func() }
    }
}