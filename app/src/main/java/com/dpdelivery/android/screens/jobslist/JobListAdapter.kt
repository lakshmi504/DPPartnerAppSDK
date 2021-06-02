package com.dpdelivery.android.screens.jobslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.screens.jobslist.viewholder.JobsListViewHolder
import com.dpdelivery.android.screens.jobslist.viewholder.LoadingViewHolder


class JobListAdapter(private val context: Context, private var adapterClickListener: IAdapterClickListener, private var jobType: String) : RecyclerView.Adapter<BaseViewholder>() {
    private var jobsList = ArrayList<Job>()
    private var isLoadingAdded = false
    private val ITEM = 0
    private val LOADING = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewholder {
        val view: View
        var viewHolder: BaseViewholder? = null
        when (viewType) {
            ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_asg_jobs_list, parent, false)
                viewHolder = JobsListViewHolder(view, adapterClickListener,jobType)
            }
            LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                viewHolder = LoadingViewHolder(view)
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

    fun add(r: Job) {
        jobsList.add(r)
        notifyItemInserted(jobsList.size - 1)
    }

    fun addAll(jobsList: ArrayList<Job?>) {
        for (result in jobsList) {
            add(result!!)
        }
    }

    private fun remove(r: Job) {
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

    private fun getItem(position: Int): Job {
        return jobsList[position]
    }

    override fun getItemCount(): Int {
        return jobsList.size
    }
}