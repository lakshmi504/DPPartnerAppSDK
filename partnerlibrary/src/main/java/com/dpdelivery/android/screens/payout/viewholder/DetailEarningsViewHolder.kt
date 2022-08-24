package com.dpdelivery.android.screens.payout.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.EntryWM
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_earnings.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by user on 03/06/21.
 */
class DetailEarningsViewHolder(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    @SuppressLint("SetTextI18n")
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is EntryWM) {
            tv_job.text = item.description
            if (!item.date.isNullOrEmpty()) {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                val output = SimpleDateFormat("d-MMM-yyyy", Locale.ROOT)
                input.timeZone = TimeZone.getTimeZone("IST")
                var d: Date? = null
                try {
                    d = input.parse(item.date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val formatted = output.format(d!!)
                tv_date.text = "Created on $formatted"
            } else {
                tv_date.text = item.date
            }
            if (item.credit) {
                tv_amount.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                tv_amount.text = CommonUtils.getRupeesSymbol(context, item.amount)
            } else {
                tv_amount.setTextColor(ContextCompat.getColor(context, R.color.color_red))
                tv_amount.text = CommonUtils.getMinusRupeesSymbol(context, item.amount)
            }
        }
    }
}