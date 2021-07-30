package com.dpdelivery.android.screens.summary.viewholder

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.LastMonth
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_summary_last_month.*

class SummaryLastMonth(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, position: Int) {
        if (item is LastMonth) {
            tv_name.text = item.name
            if (item.name == "Completed Jobs") {
                tv_quantity.text = item.count.toString()
                tv_quantity.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            } else {
                tv_quantity.text = item.count.toString()
            }
        }
    }
}
