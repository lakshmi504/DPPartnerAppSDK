package com.dpdelivery.android.technicianui.workflow

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.technicianui.workflow.viewholder.TemplateListViewHolder
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timeline.*

class WorkFlowAdapter(var context: Context) : RecyclerView.Adapter<WorkFlowAdapter.ViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step>? = null

    fun addList(list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_timeline)
        return ViewHolder(view)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step) {
                tv_nums.text = "" + (pos + 1) + "."
                tv_step_name.text = item.name
                val sublist = item.templates
               // rv_sublist.layoutManager = LinearLayoutManager(context)
               /* val adapterSubList = TemplateListViewHolder(context,jobId =)
              //  rv_sublist.adapter = adapterSubList
                adapterSubList.addList(sublist)*/
            }
        }
    }
}