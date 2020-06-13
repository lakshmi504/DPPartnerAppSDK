package com.dpdelivery.android.ui.deliveryjoblist.viewholder


import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.MyApplication.Companion.context
import com.dpdelivery.android.R
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.utils.CommonUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewAdapter(val recyclerView: RecyclerView,var mItemList: List<Data?>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_jobs_list, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mItemList == null) 0 else mItemList!!.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (mItemList!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv_customer_name: TextView? = null
        var tv_cust_phn: TextView? = null
        var tv_area: TextView? = null
        var tv_appt_time: TextView? = null
        var tv_agent_name: TextView? = null
        var assign: TextView? = null
        var tv_alternate_no: TextView? = null
        var alternate_no: TextView? = null
        var tv_status: TextView? = null

        init {
            tv_customer_name = itemView.findViewById(R.id.tv_customer_name)
            tv_cust_phn = itemView.findViewById(R.id.tv_cust_phn)
            tv_area = itemView.findViewById(R.id.tv_area)
            tv_appt_time = itemView.findViewById(R.id.tv_appt_time)
            tv_agent_name = itemView.findViewById(R.id.tv_agent_name)
            assign = itemView.findViewById(R.id.assign)
            tv_alternate_no = itemView.findViewById(R.id.tv_alternate_no)
            alternate_no = itemView.findViewById(R.id.alternate_no)
            tv_status = itemView.findViewById(R.id.tv_status)
        }
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) { //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item = mItemList!![position]!!
        viewHolder.tv_customer_name?.text = (item.custName)
        viewHolder.tv_cust_phn?.text = item.custPhone
        val phone = item.custPhone
        viewHolder.tv_area?.text = item.custArea

        if (!item.appointmentAt.isNullOrEmpty()) {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
            val output = SimpleDateFormat("EEE, d-MMM-yyyy hh:mm:ss a", Locale.ROOT)
            input.timeZone = TimeZone.getTimeZone("IST")
            var d: Date? = null
            try {
                d = input.parse(item.appointmentAt)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val formatted = output.format(d!!)
            viewHolder.tv_appt_time?.text = formatted
        } else {
            viewHolder.tv_appt_time?.text = item.appointmentAt
        }
        viewHolder.tv_status?.text = item.status?.description
        if (CommonUtils.getRole() == "ROLE_DeliveryPerson") {
            viewHolder.tv_agent_name?.visibility = View.GONE
            viewHolder.assign?.visibility = View.GONE
        } else {
            viewHolder.tv_agent_name?.text = item.assignedTo?.name
            viewHolder.tv_agent_name?.visibility = View.VISIBLE
            viewHolder.assign?.visibility = View.VISIBLE
        }
        viewHolder.tv_cust_phn?.paintFlags = viewHolder.tv_cust_phn?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!
        if (item.custPhone!!.isNotEmpty()) {
            viewHolder.tv_cust_phn!!.setOnClickListener {
                val url = "tel:$phone"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                context.startActivity(intent)
            }
        }

        if (item.custAltPhone.isNullOrEmpty()) {
            viewHolder.tv_alternate_no?.visibility = View.GONE
            viewHolder.alternate_no?.visibility = View.GONE
        } else {
            viewHolder.tv_alternate_no?.text = item.custAltPhone
            val altPhone = item.custAltPhone
            viewHolder.tv_alternate_no?.paintFlags = viewHolder.tv_cust_phn!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            viewHolder.tv_alternate_no?.visibility = View.VISIBLE
            viewHolder.alternate_no?.visibility = View.VISIBLE
            viewHolder.tv_alternate_no?.setOnClickListener {
                val url = "tel:$altPhone"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                context.startActivity(intent)
            }
        }


    }

}