package com.dpdelivery.android.screens.jobslist.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.model.techres.Job
import kotlinx.android.synthetic.main.item_loading.view.*

class LoadingViewHolder(var view: View) : BaseViewholder(view) {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Job) {
            view.load_more_progress.visibility = View.VISIBLE
        }
    }
}