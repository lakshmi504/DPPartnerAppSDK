package com.dpdelivery.android.screens.inventoryDetails.inventoryDetailsAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.DetailsProductInfo
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_inventory.*

/**
 * Created by user on 24/06/21.
 */
class InventoryDetailsAdapter(
    var context: Context,
    var adapterClickListener: IAdapterClickListener? = null, var submissionField: String
) : RecyclerView.Adapter<InventoryDetailsAdapter.InventoryDetailsViewHolder>() {

    private var list: ArrayList<DetailsProductInfo>? = null

    fun addList(
        list: ArrayList<DetailsProductInfo>?,
        submissionField: String
    ) {
        this.list = list
        this.submissionField = submissionField
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryDetailsViewHolder {
        val view = parent.inflate(R.layout.item_detail_inventory)
        return InventoryDetailsViewHolder(
            view,
            adapterClickListener,
            submissionField
        )
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(
        holder: InventoryDetailsViewHolder,
        position: Int
    ) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class InventoryDetailsViewHolder(
        override val containerView: View,
        var adapterClickListener: IAdapterClickListener? = null,
        val submissionField: String
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is DetailsProductInfo) {
                when (submissionField) {
                    "Picked Up Items" -> {
                        tv_pid.text = item.product_code
                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            adapterClickListener!!.onclick(
                                any = item,
                                pos = pos,
                                type = itemView,
                                op = Constants.REMOVE_INVENTORY
                            )
                        }
                    }
                    "To be picked up" -> {
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
                    }
                    "To be returned" -> {
                        tv_pid.text = item.product_code
                    }
                }
            }
        }
    }

}