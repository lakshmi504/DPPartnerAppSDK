package com.dpdelivery.android.technicianui.workflow.viewholder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_template_list.*

class TemplateListViewHolder(var context: Context, var adapterClickListener: IAdapterClickListener? = null,var jobId:Int) : RecyclerView.Adapter<TemplateListViewHolder.ViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>? = null

    fun addList(list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_template_list)
        return ViewHolder(view, adapterClickListener,jobId)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class ViewHolder(override val containerView: View, var adapterClickListener: IAdapterClickListener? = null,var jobId: Int) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template) {
                tv_template_name.text = "${item.name} :"
                val lowerList = item.elements
                rv_lower_list.layoutManager = LinearLayoutManager(context)
                val adapterLowerList = ElementListViewHolder(context,adapterClickListener,jobId)
                rv_lower_list.adapter = adapterLowerList
                adapterLowerList.addList(lowerList)
            }
        }
    }
}