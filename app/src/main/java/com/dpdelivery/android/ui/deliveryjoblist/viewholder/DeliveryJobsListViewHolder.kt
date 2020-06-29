package com.dpdelivery.android.ui.deliveryjoblist.viewholder

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_delivery_jobs_list.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DeliveryJobsListViewHolder(override val containerView: View?, var context: Context, private var adapterClickListener: IAdapterClickListener) : BaseViewholder(containerView), LayoutContainer {

    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Data) {
            tv_job_id.text = item.id.toString()
            tv_customer_name.text = item.custName
            tv_cust_phn.text = item.custPhone
            val phone = item.custPhone
            tv_area.text = item.custArea

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
                tv_appt_time.text = formatted
            } else {
                tv_appt_time.text = item.appointmentAt
            }
            tv_status.text = item.status?.description
            if (CommonUtils.getRole() == "ROLE_DeliveryPerson") {
                tv_agent_name.visibility = View.GONE
                assign.visibility = View.GONE
            } else {
                tv_agent_name.text = item.assignedTo?.name
                tv_agent_name.visibility = View.VISIBLE
                assign.visibility = View.VISIBLE
            }
            tv_cust_phn.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (item.custPhone!!.isNotEmpty()) {
                tv_cust_phn.setOnClickListener {
                    val url = "tel:$phone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    context.startActivity(intent)
                }
            }

            if (item.custAltPhone.isNullOrEmpty()) {
                tv_alternate_no.visibility = View.GONE
                alternate_no.visibility = View.GONE
            } else {
                tv_alternate_no.text = item.custAltPhone
                val altPhone = item.custAltPhone
                tv_alternate_no.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tv_alternate_no.visibility = View.VISIBLE
                alternate_no.visibility = View.VISIBLE
                tv_alternate_no.setOnClickListener {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    context.startActivity(intent)
                }
            }

            ll_bg.setOnClickListener {
                adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.JOB_DETAILS)
            }
        }
    }
}