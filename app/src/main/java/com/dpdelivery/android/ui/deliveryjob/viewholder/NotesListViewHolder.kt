package com.dpdelivery.android.ui.deliveryjob.viewholder

import android.content.Context
import android.view.View
import com.dpdelivery.android.commonadapter.BaseViewholder
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.DeliveryJobsResNote
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_notes_list.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotesListViewHolder(override val containerView: View?, var context: Context, private var adapterClickListener: IAdapterClickListener) : BaseViewholder(containerView), LayoutContainer {

    override fun bind(context: Context, item: Any, pos: Int) {
        if (item is DeliveryJobsResNote) {
            tv_note.text = item.note

            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
            val output = SimpleDateFormat("EEE, d-MMM-yyyy hh:mm:ss a", Locale.ROOT)
            input.timeZone = TimeZone.getTimeZone("IST")
            var d: Date? = null
            try {
                d = input.parse(item.createdOn!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val formatted = output.format(d!!)
            tv_time.text = formatted
            tv_created.text = item.createdBy?.name
        }
    }
}