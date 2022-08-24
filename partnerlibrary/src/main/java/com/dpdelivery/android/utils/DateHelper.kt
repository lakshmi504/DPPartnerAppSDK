package com.dpdelivery.android.utils

import java.text.SimpleDateFormat
import java.util.*


class DateHelper {
    companion object {

        fun getCurrentDate(): String {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val date = Calendar.getInstance().time
            return simpleDateFormat.format(date)
        }
    }
}