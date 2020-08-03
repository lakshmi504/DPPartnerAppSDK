package com.dpdelivery.android.ui.filteredjobs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.technicianui.jobslist.viewholder.JobsListViewHolder
import com.dpdelivery.android.ui.filteredjobs.viewholder.FilteredJobsListViewHolder
import com.dpdelivery.android.ui.filteredjobs.viewholder.FilteredJobsLoadingViewHolder

class FilteredJobListAdapter(private val context: Context) : RecyclerView.Adapter<BaseViewholder>() {
    private var jobsList = ArrayList<Data>()
    private var isLoadingAdded = false
    private val ITEM = 0
    private val LOADING = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewholder {
        val view: View
        var viewHolder: BaseViewholder? = null
        when (viewType) {
            ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_jobs_list, parent, false)
                viewHolder = FilteredJobsListViewHolder(view)
            }
            LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                viewHolder = FilteredJobsLoadingViewHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == jobsList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    override fun onBindViewHolder(holder: BaseViewholder, position: Int) {
        when (holder) {
            is JobsListViewHolder -> {
                holder.bind(context, jobsList[position], position)
            }
            else -> {
                holder.bind(context, jobsList[position], position)
            }
        }

    }

    fun add(r: Data) {
        jobsList.add(r)
        notifyItemInserted(jobsList.size - 1)
    }

    fun addAll(jobsList: ArrayList<Data?>) {
        for (result in jobsList) {
            add(result!!)
        }
    }

    private fun remove(r: Data) {
        val position = jobsList.indexOf(r)
        if (position > -1) {
            jobsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
    }

    private fun getItem(position: Int): Data {
        return jobsList[position]
    }

    override fun getItemCount(): Int {
        return jobsList.size
    }
}