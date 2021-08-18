package com.dpdelivery.android.screens.servicereport.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import java.util.*

/**
 * Created by user on 18/08/21.
 */
class JobsAdapter(
    var context: Context
) : RecyclerView.Adapter<JobsAdapter.JobsListViewHolder>() {

    private var list: ArrayList<String>? = null

    fun addList(list: ArrayList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsListViewHolder {
        val view = parent.inflate(R.layout.item_jobs)
        return JobsListViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: JobsListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    class JobsListViewHolder(override val containerView: View, var context: Context) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {

        }

    }
}