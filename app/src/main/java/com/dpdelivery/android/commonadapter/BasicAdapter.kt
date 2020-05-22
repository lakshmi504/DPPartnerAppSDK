package com.dpdelivery.android.commonadapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.technicianui.techjobslist.viewholder.TechJobsListViewHolder
import com.dpdelivery.android.ui.deliveryjob.viewholder.NotesListViewHolder
import com.dpdelivery.android.ui.deliveryjoblist.viewholder.DeliveryJobsListViewHolder
import com.dpdelivery.android.utils.inflate
import com.dpdelivery.android.utils.withNotNullNorEmpty
import java.util.*

class BasicAdapter(var context: Context, var type: Int, var adapterType: String = "common", var adapterClickListener: IAdapterClickListener? = null) : RecyclerView.Adapter<BaseViewholder>() {

    var list: ArrayList<*>? = null

    fun addList(list: ArrayList<*>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewholder {
        var view = parent.inflate(type)
        lateinit var holder: BaseViewholder
        when (type) {
            R.layout.item_delivery_jobs_list -> holder = DeliveryJobsListViewHolder(view, context, adapterClickListener!!)
            R.layout.item_notes_list -> holder = NotesListViewHolder(view, context, adapterClickListener!!)


            //techapp

            R.layout.item_asg_jobs_list -> holder = TechJobsListViewHolder(view, context, adapterClickListener!!)


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