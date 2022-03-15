package com.dpdelivery.android.model.techinp

import androidx.annotation.Keep

@Keep
data class SyncIP(
    val cmd_status: String? = null,
    val cmds: ArrayList<Cmd>,
    val currentliters: String,
    val flowlimit: String,
    val latLong: String? = null,
    val mode: String?=null,
    val purifierid: String,
    val status: String,
    val tdsIn: String? = null,
    val tdsOut: String? = null,
    val tempIn: String? = null,
    val tempOut: String? = null,
    val validity: String
)

@Keep
data class Cmd(
    val cmd: String,
    val cmdid: Int,
    val status: String
)
