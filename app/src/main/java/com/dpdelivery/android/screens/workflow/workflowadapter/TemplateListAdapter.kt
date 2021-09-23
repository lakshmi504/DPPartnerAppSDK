package com.dpdelivery.android.screens.workflow.workflowadapter

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

class TemplateListAdapter(
    var context: Context,
    var adapterClickListener: IAdapterClickListener? = null,
    val stepMap: MutableMap<String, String>,
    val stepsFinished: MutableMap<String, Boolean>,
    var submissionField: String,
    var activationElementId: Int,
    var syncElementId: Int,
    var sparePartId: Int
) : RecyclerView.Adapter<TemplateListAdapter.TemplateListViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>? = null

    fun addList(
        list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>?,
        submissionField: String,
        activationElementId: Int,
        syncElementId: Int,
        sparePartId: Int
    ) {
        this.list = list
        this.submissionField = submissionField
        this.activationElementId = activationElementId
        this.syncElementId = syncElementId
        this.sparePartId = sparePartId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateListViewHolder {
        val view = parent.inflate(R.layout.item_template_list)
        return TemplateListViewHolder(
            view,
            adapterClickListener,
            stepMap,
            stepsFinished,
            submissionField,
            activationElementId,
            syncElementId,
            sparePartId
        )
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(holder: TemplateListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class TemplateListViewHolder(
        override val containerView: View,
        var adapterClickListener: IAdapterClickListener? = null,
        val stepMap: MutableMap<String, String>,
        val stepsFinished: MutableMap<String, Boolean>,
        val submissionField: String,
        val activationElementId: Int,
        val syncElementId: Int,
        val sparePartId: Int
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template) {
                tv_template_name.text = "${item.name} :"
                val lowerList = item.elements
                rv_lower_list.layoutManager = LinearLayoutManager(context)
                val adapterLowerList = ElementListAdapter(
                    context,
                    adapterClickListener,
                    stepMap = stepMap,
                    stepsFinished = stepsFinished,
                    submissionField = submissionField,
                    activationElementId = activationElementId,
                    syncElementId = syncElementId,
                    sparePartId = sparePartId
                )
                rv_lower_list.adapter = adapterLowerList
                adapterLowerList.addList(lowerList)
            }
        }
    }
}