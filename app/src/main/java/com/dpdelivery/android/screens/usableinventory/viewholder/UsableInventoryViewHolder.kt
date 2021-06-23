package com.dpdelivery.android.screens.usableinventory.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.usableinventory.UsableInventoryModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_usable_inventory.*

/**
 * Created by user on 03/06/21.
 */
class UsableInventoryViewHolder(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is UsableInventoryModel) {
            if (item.itemName.isNullOrEmpty()) {
                tv_pid.text = "Scan To Pick Up"
                iv_scan.visibility = View.VISIBLE
                itemView.setOnClickListener {
                    adapterClickListener!!.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.PICKED_UP_INVENTORY
                    )
                }
            } else {
                tv_pid.text = item.itemName
                btn_return.visibility = View.VISIBLE
            }
            btn_return.setOnClickListener {
                adapterClickListener!!.onclick(
                    any = item,
                    pos = pos,
                    type = itemView,
                    op = Constants.RETURNABLE_INVENTORY
                )
            }
        }
    }
}