package com.dpdelivery.android.technicianui.jobslist.viewholder

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.Job
import kotlinx.android.synthetic.main.item_asg_jobs_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class JobsListViewHolder(var view: View, private var adapterClickListener: IAdapterClickListener, private var jobType: String) : BaseViewholder(view) {
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Job) {
            view.tv_jobtypevalue.text = item.type?.description
            view.tv_jobidvalue.text = item.id.toString()
            view.tv_namevalue.text = item.customerName
            if (item.customerAddress?.area?.description.isNullOrEmpty()) {
                view.tv_areavalue.text = " "
            } else {
                view.tv_areavalue.text = item.customerAddress?.area?.description.toString()
            }

            //job assigned diff
            val startJobInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
            startJobInput.timeZone = TimeZone.getTimeZone("GMT")
            var startJobDate: Date? = null
            try {
                startJobDate = startJobInput.parse(item.appointmentStartTime!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val jobstarttime: Long = (startJobDate!!.time) - Date().time
            val diffInHours = jobstarttime / (60 * 60 * 1000) % 24

            /*  if (CommonUtils.getRole() == "ROLE_Technician") {
                  if (jobType == "ASG") {
                      if (pos == 0 && diffInHours <= 1) {
                          view.ll_mobile.visibility = View.VISIBLE
                          view.ll_alt_mobile.visibility = View.VISIBLE
                      } else {
                          view.ll_mobile.visibility = View.GONE
                          view.ll_alt_mobile.visibility = View.GONE
                      }
                  }
              }*/
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
                view.tv_appointmentdate.text = formatted
                view.appointmenttimevalue.text = "$startTime - $endTime"
            } else {
                view.tv_appointmentdate.text = item.appointmentStartTime
            }
            view.tv_statusvalue.text = item.status?.description
            /*if ((jobType == "ASG" || jobType == "INP") && item.workflowId != null) {
                view.ll_instatus.visibility = View.VISIBLE
            } else {
                view.ll_instatus.visibility = View.GONE
            }
            view.tv_instatusvalue.paintFlags = view.tv_statusvalue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            view.tv_instatusvalue.setOnClickListener {
                adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.JOB_TYPE)
            }*/
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
                view.tv_alternate_no.visibility = View.GONE
                view.alternate_no.visibility = View.GONE
            } else {
                val altPhone = item.customerAltPhone
                try {
                    //view.tv_alternate_no.text = (altPhone).replaceRange(5..9, "*****")
                    view.tv_alternate_no.text = (altPhone)
                } catch (e: Exception) {
                }
                view.tv_alternate_no.paintFlags = view.tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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
            view.ll_bg.setOnClickListener {
                adapterClickListener.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ASSIGN_JOB_DETAILS)
            }

        }

    }
}