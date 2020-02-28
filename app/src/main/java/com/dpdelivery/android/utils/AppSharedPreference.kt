package com.dpdelivery.android.utils

import android.content.Context
import android.content.SharedPreferences
import com.dpdelivery.android.MyApplication
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.PREFERENCE_NAME

class AppSharedPreference  {
    companion object {
        val SEARCHED_ADDRESS = "SEARCHED_ADDRESS"
        val SEARCHED_LAT = "SEARCHED_LAT"
        val SEARCHED_LONG = "SEARCHED_LONG"
        val SEARCHED_AREA = "SEARCHED_AREA"
        val SEARCHED_CITY = "SEARCHED_CITY"

        var PREF_DEVICE_TOKEN = "pref_device_token"

        fun getPrefs(): SharedPreferences {
            return MyApplication.context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
        fun clearPref() = getPrefs().edit().clear().apply()

        fun put(key: String, value: String) {
            getPrefs().edit().putString(key, value).apply()
        }

        fun put(key: String, value: Int) {
            getPrefs().edit().putInt(key, value).apply()
        }

        fun put(key: String, value: Float) {
            getPrefs().edit().putFloat(key, value).apply()
        }

        fun put(key: String, value: Boolean) {
            getPrefs().edit().putBoolean(key, value).apply()
        }

        fun get(key: String, defaultValue: String = ""): String? {
            return getPrefs().getString(key, defaultValue)
        }

        fun get(key: String, defaultValue: Int = -1): Int {
            return getPrefs().getInt(key, defaultValue)
        }

        fun get(key: String, defaultValue: Float = -1F): Float {
            return getPrefs().getFloat(key, defaultValue)
        }

        fun get(key: String, defaultValue: Boolean = false): Boolean {
            return getPrefs().getBoolean(key, defaultValue)
        }

        fun getlat(): String? = get(SEARCHED_LAT, "")
        fun getlng(): String? = get(SEARCHED_LONG, "")

    }
}