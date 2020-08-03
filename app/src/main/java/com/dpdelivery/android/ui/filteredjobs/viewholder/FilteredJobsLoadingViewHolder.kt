package com.dpdelivery.android.ui.filteredjobs.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.model.Data
import kotlinx.android.synthetic.main.item_loading.view.*

class FilteredJobsLoadingViewHolder(var view: View) : BaseViewholder(view) {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Data) {
            view.load_more_progress.visibility = View.VISIBLE
        }
    }
}