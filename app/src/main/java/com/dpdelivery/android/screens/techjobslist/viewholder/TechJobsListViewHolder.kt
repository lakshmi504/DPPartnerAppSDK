package com.dpdelivery.android.screens.techjobslist.viewholder

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
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_asg_jobs_list.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TechJobsListViewHolder(
    override val containerView: View?,
    var context: Context,
    private var adapterClickListener: IAdapterClickListener
) : BaseViewholder(containerView), LayoutContainer {

    @SuppressLint("SetTextI18n")
    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is Job) {
            tv_jobtypevalue.text = item.type?.description
            tv_jobtypevalue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tv_jobidvalue.text = item.id.toString()
            tv_namevalue.text = item.customerName
            tv_colorCodeValue.text = item.zipColorName
            val hexColor = item.zipColorCode
            if (!hexColor.isNullOrEmpty()) {
                iv_color.visibility = View.VISIBLE
                iv_color.setColorFilter(Color.parseColor(hexColor))
            }

            if (item.customerAddress?.area?.description.isNullOrEmpty()) {
                tv_areavalue.text = ""
            } else {
                tv_areavalue.text = item.customerAddress?.area?.description.toString()
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
                tv_appointmentdate.text = formatted
                appointmenttimevalue.text = "$startTime to $endTime".toLowerCase(Locale.ROOT)
            } else {
                tv_appointmentdate.text = item.appointmentStartTime
            }
            tv_statusvalue.text = item.status?.description
            if (item.workflowId != null) {
                btn_update_status.visibility = View.VISIBLE
            } else {
                btn_update_status.visibility = View.INVISIBLE
            }
            btn_update_status.setOnClickListener {
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
                    try {
                        tv_cust_phn.text = text.replaceRange(5..9, "*****")
                    } catch (e: Exception) {

                    }
                    /* if (CommonUtils.getRole() == "Technician")
                         try {
                             tv_cust_phn.text = text.replaceRange(5..9, "*****")
                         } catch (e: Exception) {

                         }
                     else {
                         tv_cust_phn.text = text
                     }*/
                } catch (e: Exception) {

                }
            }
            tv_cust_phn.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            if (!item.customerPhone.isNullOrEmpty()) {
                tv_cust_phn.setOnClickListener {
                    adapterClickListener.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.CUST_PHONE
                    )
                    /* if (CommonUtils.getRole() == "Technician") {
                         adapterClickListener.onclick(
                             any = item,
                             pos = pos,
                             type = itemView,
                             op = Constants.CUST_PHONE
                         )
                     } else {
                         val url = "tel:${item.customerPhone}"
                         val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                         intent.putExtra("finish", true)
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                         context.startActivity(intent)
                     }*/
                }
            }

            if (item.customerAltPhone.isNullOrEmpty()) {
                ll_alt_mobile.visibility = View.GONE
            } else {
                val altPhone = item.customerAltPhone
                tv_alternate_no.paintFlags = tv_alternate_no.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tv_alternate_no.visibility = View.VISIBLE
                alternate_no.visibility = View.VISIBLE
                try {
                    tv_alternate_no.text = (altPhone).replaceRange(5..9, "*****")
                } catch (e: Exception) {

                }
                /*if (CommonUtils.getRole() == "Technician")
                    try {
                        tv_alternate_no.text = (altPhone).replaceRange(5..9, "*****")
                    } catch (e: Exception) {

                    }
                else {
                    tv_alternate_no.text = altPhone
                }*/
                tv_alternate_no.setOnClickListener {
                   /* if (CommonUtils.getRole() == "Technician") {
                        adapterClickListener.onclick(
                            any = item,
                            pos = pos,
                            type = itemView,
                            op = Constants.ALT_CUST_PHONE
                        )
                    } else {
                        val url = "tel:$altPhone"
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        intent.putExtra("finish", true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                    }*/
                    adapterClickListener.onclick(
                        any = item,
                        pos = pos,
                        type = itemView,
                        op = Constants.ALT_CUST_PHONE
                    )
                }
            }
            btn_know_more.setOnClickListener {
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