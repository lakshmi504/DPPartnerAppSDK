package com.dpdelivery.android.screens.inventory.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.inventory.InventoryModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_inventory.*

/**
 * Created by user on 03/06/21.
 */
class InventoryViewHolder(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is InventoryModel) {
            tv_name.text = item.name
            tv_usable.text = item.new
            tv_returnable.text = item.toReturn
            tv_usable.setOnClickListener {
                adapterClickListener!!.onclick(
                    any = item,
                    pos = adapterPosition,
                    type = itemView,
                    op = Constants.USABLE_INVENTORY
                )
            }
            tv_returnable.setOnClickListener {
                adapterClickListener!!.onclick(
                    any = item,
                    pos = adapterPosition,
                    type = itemView,
                    op = Constants.RETURNABLE_INVENTORY
                )
            }
        }
    }
}