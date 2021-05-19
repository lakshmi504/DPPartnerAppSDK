package com.dpdelivery.android.utils

import android.util.Log
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {

        fun getCurrentDate(): String {
            val serverTime = "time-a.nist.gov";
            val timeClient = NTPUDPClient()
            val inetAddress: InetAddress = InetAddress.getByName(serverTime)
            val timeInfo: TimeInfo = timeClient.getTime(inetAddress)
            val returnTime: Long = timeInfo.message.transmitTimeStamp.time //server time
            //Fri May 07 15:20:23 GMT+05:30 2021
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val time = Date(returnTime)
            Log.d("TAG", "Time from $serverTime: $time")
            return simpleDateFormat.format(time)
        }

        fun getCurrentDateTime(): Date {
            val serverTime = "time-a.nist.gov";
            val timeClient = NTPUDPClient()
            val inetAddress: InetAddress = InetAddress.getByName(serverTime)
            val timeInfo: TimeInfo = timeClient.getTime(inetAddress)
            val returnTime: Long = timeInfo.message.transmitTimeStamp.time //server time
            //Fri May 07 15:20:23 GMT+05:30 2021
            return Date(returnTime)
        }
    }
}