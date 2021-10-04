package com.dpdelivery.android.screens.jobslist.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.Job
import kotlinx.android.synthetic.main.item_asg_jobs_list.*
import kotlinx.android.synthetic.main.item_asg_jobs_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class JobsListViewHolder(
    var view: View,
    private var adapterClickListener: IAdapterClickListener,
    private var jobType: String
) : BaseViewholder(view) {
    @SuppressLint("SetTextI18n")
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Job) {
            view.tv_jobtypevalue.text = item.type?.description
            view.tv_jobtypevalue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            view.tv_jobidvalue.text = item.id.toString()
            view.tv_namevalue.text = item.customerName
            view.tv_colorCodeValue.text = item.zipColorName
            val hexColor = item.zipColorCode
            if (!hexColor.isNullOrEmpty()) {
                view.iv_color.visibility=View.VISIBLE
                view.iv_color.setColorFilter(Color.parseColor(hexColor))
            }
            if (item.customerAddress?.area?.description.isNullOrEmpty()) {
                view.tv_areavalue.text = ""
            } else {
                view.tv_areavalue.text = item.customerAddress?.area?.description.toString()
            }
            if (!item.appointmentStartTime.isNullOrEmpty()) {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                val output = SimpleDateFormat("d-MMM-yyyy", Locale.ROOT)
                val time = SimpleDateFormat("hha", Locale.ROOT)
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
                view.tv_appointmentdate.text = formatted
                view.appointmenttimevalue.text = "$startTime to $endTime".toLowerCase(Locale.ROOT)
            } else {
                view.tv_appointmentdate.text = item.appointmentStartTime
            }
            view.tv_statusvalue.text = item.status?.description

            if ((jobType == "INP") && item.workflowId != null) {
                view.btn_update_status.visibility = View.VISIBLE
            } else {
                view.btn_update_status.visibility = View.INVISIBLE
            }
            view.btn_update_status.setOnClickListener {
                adapterClickListener.onclick(
                    any = item,
                    pos = pos,
                    type = itemView,
                    op = Constants.JOB_TYPE
                )
            }
            val text = item.customerPhone
            if (text!!.isNotEmpty()) {
                try {
                    //view.tv_cust_phn.text = text.replaceRange(5..9, "*****")
                    view.tv_cust_phn.text = text
                } catch (e: Exception) {
                }
            }
            view.tv_cust_phn.paintFlags = view.tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (item.customerPhone.isNotEmpty()) {
                view.tv_cust_phn.setOnClickListener {
                    val url = "tel:${item.customerPhone}"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    intent.putExtra("finish", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                    // adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.CUST_PHONE)
                }
            }

            if (item.customerAltPhone.isNullOrEmpty()) {
                view.ll_alt_mobile.visibility = View.GONE
            } else {
                val altPhone = item.customerAltPhone
                try {
                    //view.tv_alternate_no.text = (altPhone).replaceRange(5..9, "*****")
                    view.tv_alternate_no.text = (altPhone)
                } catch (e: Exception) {
                }
                view.tv_alternate_no.paintFlags =
                    view.tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                view.tv_alternate_no.visibility = View.VISIBLE
                view.alternate_no.visibility = View.VISIBLE
                view.tv_alternate_no.setOnClickListener {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    intent.putExtra("finish", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                    //adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ALT_CUST_PHONE)
                }
            }
            view.btn_know_more.setOnClickListener {
                adapterClickListener.onclick(
                    any = item,
                    pos = pos,
                    type = itemView,
                    op = Constants.ASSIGN_JOB_DETAILS
                )
            }

        }

    }
}