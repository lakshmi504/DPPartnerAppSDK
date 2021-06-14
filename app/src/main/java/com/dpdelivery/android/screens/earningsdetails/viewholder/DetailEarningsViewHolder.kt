package com.dpdelivery.android.screens.earningsdetails.viewholder

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.earningsdetails.EarningsModel
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_earnings.*

/**
 * Created by user on 03/06/21.
 */
class DetailEarningsViewHolder(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is EarningsModel) {
            tv_job.text = item.title
            tv_date.text = item.date
            if (item.isCredit) {
                tv_amount.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                tv_amount.text = CommonUtils.getRupeesSymbol(context, item.amount.toString())
            } else {
                tv_amount.setTextColor(ContextCompat.getColor(context, R.color.color_red))
                tv_amount.text = CommonUtils.getMinusRupeesSymbol(context, item.amount.toString())
            }
        }
    }
}