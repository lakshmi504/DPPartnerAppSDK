package com.dpdelivery.android.model.techres

import androidx.annotation.Keep

@Keep
data class BLEDetailsRes(
        val cmds: ArrayList<Cmd?>?,
        val cmdsPending: Int?,
        val output: BLEDetailsResOutput?,
        val pc: String?,
        val status: String?
)
@Keep
data class Cmd(
        val cmd: String?,
        val cmdid: String?,
        val id: Int?
)
@Keep
data class BLEDetailsResOutput(
        val balance: Int?,
        val color: String?,
        val current: String?,
        val devicecondition: String?,
        val flowlimit: String?,
        val lastsync: String?,
        val owner: String?,
        val popuplink: String?,
        val popuplinktext: String?,
        val popuptext: String?,
        val showbookservice: Int?,
        val showpopup: Int?,
        val showrecharge: Int?,
        val status: String?,
        val validity: String?,
        val message: String? = ""
)
