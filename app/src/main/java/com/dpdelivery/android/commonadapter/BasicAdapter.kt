package com.dpdelivery.android.commonadapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.screens.earningsdetails.viewholder.DetailEarningsViewHolder
import com.dpdelivery.android.screens.finish.viewholder.SparePartsViewHolder
import com.dpdelivery.android.screens.inventory.viewholder.InventoryViewHolder
import com.dpdelivery.android.screens.jobdetails.TechNotesListViewHolder
import com.dpdelivery.android.screens.returnableInventory.viewholder.ReturnableInventoryViewHolder
import com.dpdelivery.android.screens.summary.viewholder.SummaryLastMonth
import com.dpdelivery.android.screens.summary.viewholder.SummaryThisMonth
import com.dpdelivery.android.screens.techjobslist.viewholder.TechJobsListViewHolder
import com.dpdelivery.android.screens.usableinventory.viewholder.UsableInventoryViewHolder
import com.dpdelivery.android.utils.inflate
import com.dpdelivery.android.utils.withNotNullNorEmpty
import java.util.*

class BasicAdapter(
    var context: Context,
    var type: Int,
    var adapterType: String = "common",
    var adapterClickListener: IAdapterClickListener? = null
) : RecyclerView.Adapter<BaseViewholder>() {

    var list: ArrayList<*>? = null

    fun addList(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewholder {
        val view = parent.inflate(type)
        lateinit var holder: BaseViewholder
        when (type) {
            R.layout.item_asg_jobs_list -> holder =
                TechJobsListViewHolder(view, context, adapterClickListener!!)
            R.layout.item_spare_parts -> holder =
                SparePartsViewHolder(view, context, adapterClickListener!!)
            R.layout.tech_item_notes_list -> holder =
                TechNotesListViewHolder(view, context, adapterClickListener!!)
            R.layout.item_summary_this_month -> holder =
                SummaryThisMonth(view, context, adapterClickListener!!)
            R.layout.item_summary_last_month -> holder =
                SummaryLastMonth(view, context, adapterClickListener!!)
            R.layout.item_inventory -> holder =
                InventoryViewHolder(view, context, adapterClickListener!!)
            R.layout.item_usable_inventory -> holder =
                UsableInventoryViewHolder(view, context, adapterClickListener!!)
            R.layout.item_returnable_inventory -> holder =
                ReturnableInventoryViewHolder(view, context, adapterClickListener!!)
            R.layout.item_detail_earnings -> holder =
                DetailEarningsViewHolder(view, context, adapterClickListener!!)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return (list?.size ?: 0)

    }

    override fun onBindViewHolder(holder: BaseViewholder, position: Int) {
        list.withNotNullNorEmpty {
            holder.bind(context, this[position], position)
            return
        }

        holder.bind(context, holder, position)
    }
}