package com.dpdelivery.android.screens.inventory.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.inventory.InventoryModel
import com.dpdelivery.android.utils.CommonUtils
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
            tv_picked_up.text = CommonUtils.getPickUpText(context, item.picked_up!!)
            tv_to_be_pickup.text = CommonUtils.getToBePickUpText(context, item.to_be_pick_up!!)
            tv_returnable.text = CommonUtils.getReturnText(context, item.to_be_Returned!!)
            if (item.picked_up != "0") {
                tv_picked_up.isEnabled = true
                tv_picked_up.alpha = 1f
                tv_picked_up.setBackgroundResource(R.drawable.btn_theme_bg)
            }
            if (item.to_be_pick_up != "0") {
                tv_to_be_pickup.isEnabled = true
                tv_to_be_pickup.alpha = 1f
                tv_to_be_pickup.setBackgroundResource(R.drawable.btn_green_bg)
            }
            if (item.to_be_Returned != "0") {
                tv_returnable.isEnabled = true
                tv_returnable.alpha = 1f
                tv_returnable.setBackgroundResource(R.drawable.btn_red_bg)
            }
            tv_picked_up.setOnClickListener {
                adapterClickListener!!.onclick(
                    any = item,
                    pos = pos,
                    type = itemView,
                    op = Constants.PICKED_UP_INVENTORY
                )
            }
            tv_to_be_pickup.setOnClickListener {
                adapterClickListener!!.onclick(
                    any = item,
                    pos = pos,
                    type = itemView,
                    op = Constants.TO_BE_PICKED_UP_INVENTORY
                )
            }
            tv_returnable.setOnClickListener {
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