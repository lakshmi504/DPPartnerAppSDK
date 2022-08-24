package com.dpdelivery.android.commonadapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewholder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    abstract fun bind(context: Context, item: Any, pos: Int = 0)
}