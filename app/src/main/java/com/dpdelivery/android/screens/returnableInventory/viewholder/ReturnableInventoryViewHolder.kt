package com.dpdelivery.android.screens.returnableInventory.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by user on 03/06/21.
 */
class ReturnableInventoryViewHolder(
    override val containerView: View?,
    var context: Context?,
    var adapterClickListener: IAdapterClickListener?
) : BaseViewholder(containerView!!), LayoutContainer {
    override fun bind(context: Context, item: Any, pos: Int) {

    }
}