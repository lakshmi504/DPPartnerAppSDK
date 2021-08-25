package com.dpdelivery.android.screens.servicereport.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.LastJobsRes
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_jobs.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by user on 18/08/21.
 */
class JobsAdapter(
    var context: Context
) : RecyclerView.Adapter<JobsAdapter.JobsListViewHolder>() {

    private var list: ArrayList<LastJobsRes.LastJobsResItem>? = null

    fun addList(list: ArrayList<LastJobsRes.LastJobsResItem>) {
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
            if (item is LastJobsRes.LastJobsResItem) {

                tv_job_name.text = item.type.description
                tv_job_id.text = item.id.toString()

                if (!item.appointmentStartTime.isNullOrEmpty()) {
                    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                    val output = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    input.timeZone = TimeZone.getTimeZone("IST")
                    var d: Date? = null
                    try {
                        d = input.parse(item.appointmentStartTime)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val formattedStartTime = output.format(d!!)
                    tv_job_date.text = formattedStartTime
                }
                if (item.spareParts.isEmpty() || item.workflowData != null) {
                    iv_show_more.visibility = View.GONE
                } else {
                    iv_show_more.visibility = View.VISIBLE
                    iv_show_more.setImageResource(R.drawable.ic_down_arrow)
                }
                rl_job_data.setOnClickListener {
                    ll_report.visibility = View.VISIBLE
                    iv_show_more.setImageResource(R.drawable.ic_up_arrow)
                }
                ll_report.setOnClickListener {
                    if (ll_report.visibility == View.VISIBLE) {
                        rl_job_data.visibility = View.VISIBLE
                        ll_report.visibility = View.GONE
                        iv_show_more.visibility = View.VISIBLE
                        iv_show_more.setImageResource(R.drawable.ic_down_arrow)
                    }
                }
                if (item.spareParts.isNotEmpty()) {
                    ll_spares_changed.visibility = View.VISIBLE
                    val spareParts =
                        item.spareHistory.spareConsumptions.toString()!!.replace("[", "")
                    val spareParts1 = spareParts.replace("]", "")
                    tv_spares_desc.text = spareParts1
                }
                if (item.workflowData != null) {
                    if (item.workflowData.body!!.steps!![3].templates!![0].elements!!.isNotEmpty()) {
                        val data = item.workflowData.body.steps!![3].templates!![0].elements!!
                        ll_issue.visibility = View.VISIBLE
                        ll_diagnosis.visibility = View.VISIBLE
                        ll_resolution.visibility = View.VISIBLE

                        tv_issue.text = data[0].name
                        tv_issue_desc.text = data[0].value
                        tv_diagnosis.text = data[1].name
                        tv_diagnosis_desc.text = data[1].value
                        tv_resolution.text = data[2].name
                        tv_resolution_desc.text = data[2].value
                    }
                }
            }
        }

    }
}