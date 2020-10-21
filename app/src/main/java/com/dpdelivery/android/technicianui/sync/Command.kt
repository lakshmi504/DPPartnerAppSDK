package com.dpdelivery.android.technicianui.sync

class Command {
    var id = 0

    constructor() {}

    var cmd: String? = null
    var status: String? = null

    constructor(id: Int, cmd: String?, status: String?) {
        this.id = id
        this.cmd = cmd
        this.status = status
    }
}