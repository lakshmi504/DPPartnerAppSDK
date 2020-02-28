package com.dpdelivery.android.interfaces

interface IAdapterClickListener {
    fun onclick(any: Any, pos: Int = 0, type: Any = "none", op: String = "none")
}