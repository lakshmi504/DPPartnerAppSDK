package com.dpdelivery.android.screens.sync

import android.util.Log
import java.util.*

class CommandHandler {
    fun readLiters(data: String): Int {
        return (data.substring(4, 6) + "" + data.substring(2, 4) + "" + data.substring(0, 2)).toInt(16)
    }

    fun readFlowlimit(data: String): Int {
        return (data.substring(4, 6) + "" + data.substring(2, 4) + "" + data.substring(0, 2)).toInt(16)
    }

    fun readValidity(data: String): String {
        var date = "20" + data.substring(4, 6).toInt(16)
        date = date + "-" + String.format("%02d", data.substring(2, 4).toInt(16))
        date = date + "-" + String.format("%02d", data.substring(0, 2).toInt(16))
        return date
    }

    fun readPrepaid(data: String): Int {
        return data.substring(0, 2).toInt(16)
    }

    fun readStatus(data: String): Int {
        return data.substring(0, 2).toInt(16)
    }

    fun writeLitersCmd(data: Int): Int {
        var output = "80"
        val zeros = "00000000"
        val temp = Integer.toHexString(data)
        val hex = zeros.substring(0, 6 - temp.length) + temp
        Log.i("temp", "the data writtern is $hex")
        //String hex = String.format("%06s",Integer.toHexString(data));
        output = hex.substring(0, 2) + "" + hex.substring(2, 4) + "" + hex.substring(4, 6) + "" + output
        Log.i("temp", "the output writtern is $output")
        return output.toInt(16)
    }

    fun writeCurrentLitersCmd(data: Int): Int {
        var output = "20"
        val zeros = "00000000"
        val temp = Integer.toHexString(data)
        val hex = zeros.substring(0, 6 - temp.length) + temp
        Log.i("temp", "the data writtern is $hex")
        //String hex = String.format("%06s",Integer.toHexString(data));
        output = hex.substring(0, 2) + "" + hex.substring(2, 4) + "" + hex.substring(4, 6) + "" + output
        Log.i("temp", "the output writtern is $output")
        return output.toInt(16)
    }

    companion object {
        const val PURIFIER_STATE = "10"
        const val READ_LITERS = "00000040"
        const val READ_FLOWLIMIT = "00000041"
        const val READ_VALIDITY = "00000042"
        const val READ_RTCDATE = "00000043"
        const val READ_STATUS = "00000045"
        const val READ_PREPAID = "00000049"
        val values: ArrayList<String>
            get() {
                val list = ArrayList<String>()
                list.add(READ_LITERS)
                list.add(READ_FLOWLIMIT)
                list.add(READ_VALIDITY)
                list.add(READ_STATUS)
                //list.add(READ_PREPAID);
                return list
            }
    }
}