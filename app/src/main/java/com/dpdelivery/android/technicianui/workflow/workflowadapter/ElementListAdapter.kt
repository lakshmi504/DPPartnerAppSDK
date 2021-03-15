package com.dpdelivery.android.technicianui.workflow.workflowadapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.technicianui.workflow.WorkFlowActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_work_flow.*
import kotlinx.android.synthetic.main.item_element_list.*
import java.util.*


class ElementListAdapter(var context: Context, var adapterClickListener: IAdapterClickListener? = null, val stepMap: MutableMap<String, String>, val stepsFinished: MutableMap<String, Boolean>, val submissionField: String) : RecyclerView.Adapter<ElementListAdapter.ElementListViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>? = null

    fun addList(list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementListViewHolder {
        val view = parent.inflate(R.layout.item_element_list)
        return ElementListViewHolder(view, context, adapterClickListener, stepMap, stepsFinished, submissionField)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(holder: ElementListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class ElementListViewHolder(override val containerView: View, var context: Context, var adapterClickListener: IAdapterClickListener? = null, val stepMap: MutableMap<String, String>, val stepsFinished: MutableMap<String, Boolean>, val submissionField: String) : RecyclerView.ViewHolder(containerView), LayoutContainer, AdapterView.OnItemSelectedListener {
        @SuppressLint("ResourceAsColor")
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element) {
                tv_name.text = item.name
                if (item.showType == "VISIBLE" && item.workflowElementType == "MANUAL" && item.inputApi == "TEXT") {
                    et_add_text.visibility = View.VISIBLE
                    if (item.name.toString() == "PaymentCollected") {
                        et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    et_add_text!!.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(editable: Editable?) {

                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            stepMap[item.id.toString()] = p0.toString()
                            stepsFinished[item.id.toString()] = true
                            iv_mandatory.visibility = View.INVISIBLE
                        }
                    })
                    if (!item.value.isNullOrEmpty()) {
                        et_add_text.setText(item.value.toString())
                    }

                    if (!item.optional!!) {
                        iv_mandatory.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    } else {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                    if (!item.optional && !item.value.isNullOrEmpty()) {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                }
                if (item.showType == "VISIBLE" && item.workflowElementType == "DROPDOWN" && item.inputApi == "TEXT") {
                    et_add_text.visibility = View.VISIBLE
                    if (!item.value.isNullOrEmpty()) {
                        et_add_text.setText(item.value.toString())
                    }
                    et_add_text.background = null
                    et_add_text.isEnabled = false

                    if (!item.optional!!) {
                        iv_mandatory1.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    } else {
                        iv_mandatory1.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                    if (!item.optional && !item.value.isNullOrEmpty()) {
                        iv_mandatory1.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }

                    if (!item.dropdownContents.isNullOrEmpty()) {
                        ll_spinner_center.visibility = View.VISIBLE
                        for (i in item.dropdownContents.indices) {
                            if (!item.value.isNullOrEmpty()) {
                                if (item.dropdownContents[i].equals((item.value), true)) {
                                    val adapterMode = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, item.dropdownContents)
                                    adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner_center!!.adapter = adapterMode
                                    spinner_center.tag = item
                                    spinner_center!!.onItemSelectedListener = this
                                    spinner_center.setSelection(i)
                                }
                            } else {
                                val adapterMode = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, item.dropdownContents)
                                adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinner_center!!.adapter = adapterMode
                                spinner_center.tag = item
                                spinner_center!!.onItemSelectedListener = this
                                spinner_center.setSelection(0)
                            }
                        }
                    }
                }
                if (item.showType == "VISIBLE" && item.workflowElementType == "MANUAL" && item.inputApi == "IMAGE") {
                    btn_add_image.visibility = View.VISIBLE
                    if (!item.value.isNullOrEmpty()) {
                        CommonUtils.setImage(context, btn_add_image, item.value)
                        iv_mandatory2.visibility = View.INVISIBLE
                    }

                    if (!item.optional!!) {
                        iv_mandatory2.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    } else {
                        iv_mandatory2.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                    if (!item.optional && !item.value.isNullOrEmpty()) {
                        iv_mandatory2.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                }
                if (item.showType == "MASKED" && item.inputApi == "TEXT") {
                    if (item.value!!.isNotEmpty()) {
                        et_add_text.visibility = View.VISIBLE
                        val text = item.value
                        et_add_text.setText(text.replace(item.value, "*****"))
                    }
                    btn_add_image.visibility = View.VISIBLE
                    btn_add_image.isEnabled = false
                }
                if (item.showType == "MASKED" && item.inputApi == "TEXT" && item.workflowElementType == "DROPDOWN") {
                    ll_spinner_center.visibility = View.VISIBLE
                    ll_spinner_center.isEnabled = false
                }

                btn_add_image.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_IMAGE)
                }
                btn_upload_image.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_UPLOAD_IMAGE)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent!!.id) {
                R.id.spinner_center -> {
                    val item = parent.tag as WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element
                    val selectedString = item.dropdownContents?.get(position)
                    selectedText(selectedString!!)
                    iv_mandatory1.visibility = View.INVISIBLE
                    stepMap[item.id.toString()] = selectedString
                    stepsFinished[item.id.toString()] = true
                }
            }
        }

        private fun selectedText(selectedString: String) {
            if (submissionField != selectedString) {
                (context as WorkFlowActivity).btn_submit.visibility = View.VISIBLE
                (context as WorkFlowActivity).btn_next.visibility = View.GONE
                if ((context as WorkFlowActivity).btn_Finish.visibility == View.VISIBLE) {
                    (context as WorkFlowActivity).btn_submit.visibility = View.GONE
                }
            } else {
                (context as WorkFlowActivity).btn_next.visibility = View.VISIBLE
                (context as WorkFlowActivity).btn_submit.visibility = View.GONE
            }
        }
    }
}