package com.dpdelivery.android.screens.servicereport.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.screens.servicereport.adapter.SparesAdapter.SparesListViewHolder
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import java.util.*

/**
 * Created by user on 17/08/21.
 */
class SparesAdapter(
    var context: Context
) : RecyclerView.Adapter<SparesListViewHolder>() {

    private var list: ArrayList<String>? = null

    fun addList(list: ArrayList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparesListViewHolder {
        val view = parent.inflate(R.layout.item_spares)
        return SparesListViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: SparesListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    class SparesListViewHolder(override val containerView: View, var context: Context) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {

        }

    }
}
