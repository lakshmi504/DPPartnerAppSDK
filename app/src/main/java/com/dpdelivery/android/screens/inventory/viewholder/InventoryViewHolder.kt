package com.dpdelivery.android.screens.inventory.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.PartInfo
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
    @SuppressLint("SetTextI18n")
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is PartInfo) {
            tv_name.text = item.item_name
            val itemCode = item.item_code
            tv_item_code.text = "Item Code #$itemCode"
            tv_picked_up.text = CommonUtils.getPickUpText(context, item.picked.toString())
            tv_to_be_pickup.text =
                CommonUtils.getToBePickUpText(context, item.not_picked.toString())
            tv_returnable.text = CommonUtils.getReturnText(context, item.to_be_returned.toString())
            if (item.picked.toString() != "0") {
                tv_picked_up.isEnabled = true
                tv_picked_up.alpha = 1f
                tv_picked_up.setBackgroundResource(R.drawable.btn_theme_bg)
            }
            if (item.not_picked.toString() != "0") {
                tv_to_be_pickup.isEnabled = true
                tv_to_be_pickup.alpha = 1f
                tv_to_be_pickup.setBackgroundResource(R.drawable.btn_green_bg)
            }
            if (item.to_be_returned.toString() != "0") {
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