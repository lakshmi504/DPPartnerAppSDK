package com.dpdelivery.android.ui.filteredjobs.viewholder

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.ui.deliveryjob.DeliveryJobActivity
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.synthetic.main.item_delivery_jobs_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FilteredJobsListViewHolder(var view: View) : BaseViewholder(view) {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Data) {
            view.tv_job_id.text = item.id.toString()
            view.tv_customer_name.text = item.custName
            view.tv_cust_phn.text = item.custPhone
            val phone = item.custPhone
            view.tv_area.text = item.custArea

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
                view.tv_appt_time.text = formatted
            } else {
                view.tv_appt_time.text = item.appointmentAt
            }
            view.tv_status.text = item.status?.description
            if (CommonUtils.getRole() == "ROLE_DeliveryPerson") {
                view.tv_agent_name.visibility = View.GONE
                view.assign.visibility = View.GONE
            } else {
                view.tv_agent_name.text = item.assignedTo?.name
                view.tv_agent_name.visibility = View.VISIBLE
                view.assign.visibility = View.VISIBLE
            }
            view.tv_cust_phn.paintFlags = view.tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (item.custPhone!!.isNotEmpty()) {
                view.tv_cust_phn.setOnClickListener {
                    val url = "tel:$phone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    context.startActivity(intent)
                }
            }

            if (item.custAltPhone.isNullOrEmpty()) {
                view.tv_alternate_no.visibility = View.GONE
                view.alternate_no.visibility = View.GONE
            } else {
                view.tv_alternate_no.text = item.custAltPhone
                val altPhone = item.custAltPhone
                view.tv_alternate_no.paintFlags = view.tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                view.tv_alternate_no.visibility = View.VISIBLE
                view.alternate_no.visibility = View.VISIBLE
                view.tv_alternate_no.setOnClickListener {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    context.startActivity(intent)
                }
            }

            view.ll_bg.setOnClickListener {
                val intent = Intent(context, DeliveryJobActivity::class.java)
                intent.putExtra(Constants.ID, item.id)
                context.startActivity(intent)
            }
        }

    }
}