package com.dpdelivery.android.screens.servicereport.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.SpareConsumption
import com.dpdelivery.android.screens.servicereport.adapter.SparesAdapter.SparesListViewHolder
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_spares.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by user on 17/08/21.
 */
class SparesAdapter(
    var context: Context
) : RecyclerView.Adapter<SparesListViewHolder>() {

    private var list: ArrayList<SpareConsumption?>? = null

    fun addList(list: ArrayList<SpareConsumption?>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparesListViewHolder {
        val view = parent.inflate(R.layout.item_spares)
        return SparesListViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: SparesListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position]!!, position)
            return
        }
        holder.bind(context, holder, position)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    class SparesListViewHolder(override val containerView: View, var context: Context) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is SpareConsumption) {
                tv_spare_name.text = item.name
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                val output = SimpleDateFormat("d-MMM-yyyy", Locale.ROOT)
                var d: Date? = null
                try {
                    d = input.parse(item.date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val formatted = output.format(d!!)
                tv_last_changed.text = formatted
            }
        }

    }
}
