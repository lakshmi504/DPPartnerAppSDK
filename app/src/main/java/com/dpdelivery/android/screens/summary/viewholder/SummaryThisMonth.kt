package com.dpdelivery.android.screens.summary.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.ThisMonth
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_summary_this_month.*

class SummaryThisMonth(override val containerView: View?,
                       var context: Context?,
                       var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, position: Int) {
        if (item is ThisMonth) {
            tv_name.text = item.name
            tv_quantity.text = item.count.toString()
        }
    }
}
