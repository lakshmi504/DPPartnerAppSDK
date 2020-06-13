package com.dpdelivery.android.utils

import android.content.Context
import android.content.SharedPreferences
import com.dpdelivery.android.MyApplication

class SharedPreferenceManager {
    enum class VALUE_TYPE {
        BOOLEAN, INTEGER, STRING, FLOAT, LONG
    }

    companion object {
        val PREFERENCE_NAME = "app_pref"
        val IS_LOGED_IN = "IS_LOGED_IN"
        val TOKEN = "token"
        val ROLE = "ROLE"
        val SEARCHED_ADDRESS = "SEARCHED_ADDRESS"
        val SEARCHED_LAT = "SEARCHED_LAT"
        val SEARCHED_LONG = "SEARCHED_LONG"
        val SEARCHED_AREA = "SEARCHED_AREA"
        val SEARCHED_CITY = "SEARCHED_CITY"
        val TRANSACTION_IMAGE= "TRANSACTION_IMAGE"
        val DELIVERED_IMAGE= "DELIVERED_IMAGE"

        fun clearPreferences() {
            getPrefs().edit().clear().apply()
        }

        private fun getPrefs(): SharedPreferences {
            return MyApplication.myApplication.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }

        fun setPrefVal(key: String, value: Any, vType: VALUE_TYPE) {
            when (vType) {
                VALUE_TYPE.BOOLEAN -> getPrefs().edit().putBoolean(key, value as Boolean).apply()
                VALUE_TYPE.INTEGER -> getPrefs().edit().putInt(key, value as Int).apply()
                VALUE_TYPE.STRING -> getPrefs().edit().putString(key, value as String).apply()
                VALUE_TYPE.FLOAT -> getPrefs().edit().putFloat(key, value as Float).apply()
                VALUE_TYPE.LONG -> getPrefs().edit().putLong(key, value as Long).apply()

            }
        }

        fun getPrefVal(key: String, defValue: Any, vType: VALUE_TYPE): Any? {
            val `object`: Any?
            when (vType) {
                VALUE_TYPE.BOOLEAN -> `object` = getPrefs().getBoolean(key, defValue as Boolean)
                VALUE_TYPE.INTEGER -> `object` = getPrefs().getInt(key, defValue as Int)
                VALUE_TYPE.STRING -> `object` = getPrefs().getString(key, defValue as String)
                VALUE_TYPE.FLOAT -> `object` = getPrefs().getFloat(key, defValue as Float)
                VALUE_TYPE.LONG -> `object` = getPrefs().getLong(key, defValue as Long)
            }
            return `object`
        }

        fun get(key: String, defaultValue: String = ""): String? {
            return getPrefs().getString(key, defaultValue)
        }


        fun isLogIn(): Boolean {
            return getPrefVal(IS_LOGED_IN, false, VALUE_TYPE.BOOLEAN) as Boolean
        }

        fun getlat(): Int = AppSharedPreference.get(AppSharedPreference.SEARCHED_LAT, 0)
        fun getlng(): Int = AppSharedPreference.get(AppSharedPreference.SEARCHED_LONG, 0)

    }

    fun put(key: String, defaultValue: String = ""): String? {
        return getPrefs().getString(key, defaultValue)
    }

}