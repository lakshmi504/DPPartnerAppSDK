package com.dpdelivery.android.screens.finish.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.SparePartsData
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_spare_parts.*

class SparePartsViewHolder(override val containerView: View?, var context: Context, private var adapterClickListener: IAdapterClickListener) : BaseViewholder(containerView), LayoutContainer {

    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is SparePartsData) {
            tv_spare_name.text = item.itemname
            item.selected = true
            itemView.setOnClickListener {
                adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.SPARE_PARTS)
            }
        }
    }
}