package com.dpdelivery.android.technicianui.techjobslist.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.Job
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_asg_jobs_list.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TechJobsListViewHolder(override val containerView: View?, var context: Context, private var adapterClickListener: IAdapterClickListener) : BaseViewholder(containerView), LayoutContainer {

    @SuppressLint("SetTextI18n")
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Job) {
            tv_jobtypevalue.text = item.type?.description
            tv_namevalue.text = item.customerName
            tv_areavalue.text = item.customerAddress?.area?.description.toString()

            if (!item.appointmentStartTime.isNullOrEmpty()) {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                val output = SimpleDateFormat("d-MMM-yyyy", Locale.ROOT)
                val time = SimpleDateFormat("hh:mm a", Locale.ROOT)
                input.timeZone = TimeZone.getTimeZone("IST")
                var d: Date? = null
                var d1: Date? = null
                try {
                    d = input.parse(item.appointmentStartTime)
                    d1 = input.parse(item.appointmentEndTime!!)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val formatted = output.format(d!!)
                val startTime = time.format(d)
                val endTime = time.format(d1!!)
                tv_appointmentdate.text = formatted
                appointmenttimevalue.text = "$startTime - $endTime"
            } else {
                tv_appointmentdate.text = item.appointmentStartTime
            }
            tv_statusvalue.text = item.status?.description
            tv_cust_phn.text = item.customerPhone
            tv_cust_phn.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (item.customerPhone!!.isNotEmpty()) {
                tv_cust_phn.setOnClickListener {
                    val url = "tel:${item.customerPhone}"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    intent.putExtra("finish", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }

            if (item.customerAltPhone.isNullOrEmpty()) {
                tv_alternate_no.visibility = View.GONE
                alternate_no.visibility = View.GONE
            } else {
                tv_alternate_no.text = item.customerAltPhone
                val altPhone = item.customerAltPhone
                tv_alternate_no.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tv_alternate_no.visibility = View.VISIBLE
                alternate_no.visibility = View.VISIBLE
                tv_alternate_no.setOnClickListener {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    intent.putExtra("finish", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            }
            ll_bg.setOnClickListener {
                adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ASSIGN_JOB_DETAILS)
            }

        }
    }
}