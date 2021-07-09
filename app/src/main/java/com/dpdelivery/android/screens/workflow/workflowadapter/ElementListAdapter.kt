package com.dpdelivery.android.screens.workflow.workflowadapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.screens.workflow.WorkFlowActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_work_flow.*
import kotlinx.android.synthetic.main.item_element_list.*
import java.util.*


class ElementListAdapter(
    var context: Context,
    var adapterClickListener: IAdapterClickListener? = null,
    val stepMap: MutableMap<String, String>,
    val stepsFinished: MutableMap<String, Boolean>,
    var submissionField: String,
    var activationElementId: Int,
    var syncElementId: Int
) : RecyclerView.Adapter<ElementListAdapter.ElementListViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>? = null

    fun addList(list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementListViewHolder {
        val view = parent.inflate(R.layout.item_element_list)
        return ElementListViewHolder(
            view,
            context,
            adapterClickListener,
            stepMap,
            stepsFinished,
            submissionField,
            activationElementId,
            syncElementId
        )
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(holder: ElementListViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class ElementListViewHolder(
        override val containerView: View,
        var context: Context,
        var adapterClickListener: IAdapterClickListener? = null,
        val stepMap: MutableMap<String, String>,
        val stepsFinished: MutableMap<String, Boolean>,
        val submissionField: String,
        val activationElementId: Int,
        val syncElementId: Int
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer,
        AdapterView.OnItemSelectedListener {
        @SuppressLint("ResourceAsColor")
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element) {
                //TEXT
                if (item.showType == "VISIBLE" && item.workflowElementType == "MANUAL" && item.inputApi == "TEXT") {
                    tv_name.text = item.name
                    if (item.name.toString() == "PaymentCollected") {
                        et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    if (item.name.toString() == "DeviceCode") {
                        ivqrcodescan.visibility = View.VISIBLE
                        ivqrcodescan.setOnClickListener {
                            adapterClickListener?.onclick(
                                any = item,
                                pos = pos,
                                type = itemView,
                                op = Constants.SCAN_CODE
                            )
                        }
                    }
                    when {
                        activationElementId == item.id -> {
                            et_add_text.visibility = View.GONE
                            et_purifier_id.setText(item.value)
                            layout_ins.visibility = View.VISIBLE
                            btn_activate.setOnClickListener {
                                when {
                                    et_purifier_id!!.text.toString().trim { it <= ' ' }
                                        .isEmpty() -> {
                                        Toast.makeText(
                                            context,
                                            "Purifier Id is required",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    et_purifier_id!!.text!!.length != 10 -> {
                                        Toast.makeText(
                                            context,
                                            "Invalid Purifier Id",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        iv_refresh!!.isEnabled = false
                                    }
                                    else -> {
                                        adapterClickListener?.onclick(
                                            any = item,
                                            pos = pos,
                                            type = itemView,
                                            op = Constants.SUBMIT_PID
                                        )
                                    }
                                }
                            }
                            iv_qr_code.setOnClickListener {
                                adapterClickListener?.onclick(
                                    any = item,
                                    pos = pos,
                                    type = itemView,
                                    op = Constants.SCAN_QR_CODE
                                )
                            }
                            iv_refresh.setOnClickListener {
                                adapterClickListener?.onclick(
                                    any = item,
                                    pos = pos,
                                    type = itemView,
                                    op = Constants.REFRESH_STATUS
                                )
                            }

                        }
                        syncElementId == item.id -> {
                            et_add_text.visibility = View.GONE
                            ll_sync.visibility = View.VISIBLE
                            btn_sync.setOnClickListener {
                                adapterClickListener?.onclick(
                                    any = item,
                                    pos = pos,
                                    type = itemView,
                                    op = Constants.SYNC
                                )
                            }
                        }
                        else -> {
                            et_add_text.visibility = View.VISIBLE
                            if (!item.value.isNullOrEmpty()) {
                                et_add_text.setText(item.value.toString())
                            }
                            et_add_text!!.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(editable: Editable?) {

                                }

                                override fun beforeTextChanged(
                                    p0: CharSequence?,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int
                                ) {

                                }

                                override fun onTextChanged(
                                    p0: CharSequence?,
                                    p1: Int,
                                    p2: Int,
                                    p3: Int
                                ) {
                                    if (!item.optional!! && p0.toString().isEmpty()) {
                                        stepsFinished[item.id.toString()] = false
                                        iv_mandatory.visibility = View.VISIBLE
                                    } else {
                                        stepMap[item.id.toString()] = p0.toString()
                                        stepsFinished[item.id.toString()] = true
                                        iv_mandatory.visibility = View.INVISIBLE
                                    }
                                }
                            })
                        }
                    }

                    if (!item.optional!!) {
                        iv_mandatory.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    } else {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                    if (!item.optional && !item.value.isNullOrEmpty() && activationElementId != item.id) {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepMap[item.id.toString()] = item.value.toString()
                        stepsFinished[item.id.toString()] = true
                    } else if (!item.optional && !item.value.isNullOrEmpty() && activationElementId == item.id) {
                        iv_mandatory.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    }
                }
                //DROPDOWN
                if (item.showType == "VISIBLE" && item.workflowElementType == "DROPDOWN" && item.inputApi == "TEXT") {
                    tv_name.text = item.name

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
                                    val adapterMode = ArrayAdapter<String>(
                                        context,
                                        android.R.layout.simple_spinner_item,
                                        item.dropdownContents
                                    )
                                    adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner_center!!.adapter = adapterMode
                                    spinner_center.tag = item
                                    spinner_center!!.onItemSelectedListener = this
                                    spinner_center.setSelection(i)
                                }
                            } else {
                                val adapterMode = ArrayAdapter<String>(
                                    context,
                                    android.R.layout.simple_spinner_item,
                                    item.dropdownContents
                                )
                                adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinner_center!!.adapter = adapterMode
                                spinner_center.tag = item
                                spinner_center!!.onItemSelectedListener = this
                                spinner_center.setSelection(0)
                            }
                        }
                    }
                }
                //IMAGE
                if (item.showType == "VISIBLE" && item.workflowElementType == "MANUAL" && item.inputApi == "IMAGE") {
                    btn_add_image.visibility = View.VISIBLE
                    tv_name.text = item.name
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
                    btn_add_image.setOnClickListener {
                        adapterClickListener?.onclick(
                            any = item,
                            pos = pos,
                            type = itemView,
                            op = Constants.ELEMENT_IMAGE
                        )
                    }
                }
                //API_INPUT
                if (item.showType == "VISIBLE" && item.workflowElementType == "API_INPUT" && item.inputApi == "TEXT") {
                    ll_spares.visibility = View.VISIBLE
                    tv_name.text = item.name
                    if (!item.optional!!) {
                        iv_mandatory.visibility = View.VISIBLE
                        stepsFinished[item.id.toString()] = false
                    } else {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepMap[item.id.toString()] = ""
                        stepsFinished[item.id.toString()] = true
                    }
                    if (!item.optional && !item.value.isNullOrEmpty()) {
                        iv_mandatory.visibility = View.INVISIBLE
                        stepsFinished[item.id.toString()] = true
                    }
                    ll_spinner_spares.setOnClickListener {
                        adapterClickListener?.onclick(
                            any = item,
                            pos = pos,
                            type = itemView,
                            op = Constants.SPARE_PARTS
                        )
                    }
                }
                //MASKED
                if (item.showType == "MASKED" && item.inputApi == "TEXT") {
                    tv_name.text = item.name
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
                    tv_name.text = item.name
                }
                //DISABLED
                if (item.showType == "DISABLED" && item.inputApi == "TEXT" && item.workflowElementType == "MANUAL") {
                    et_add_text.visibility = View.VISIBLE
                    tv_name.text = item.name
                    if (!item.value.isNullOrEmpty()) {
                        et_add_text.setText(item.value.toString())
                    } else {
                        et_add_text.setText("Not Mentioned")
                    }
                    et_add_text.background = null
                    et_add_text.isEnabled = false
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent!!.id) {
                R.id.spinner_center -> {
                    val item =
                        parent.tag as WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element
                    val selectedString = item.dropdownContents?.get(position)
                    selectedText(selectedString!!)
                    iv_mandatory1.visibility = View.INVISIBLE
                    stepMap[item.id.toString()] = selectedString
                    stepsFinished[item.id.toString()] = true
                }
            }
        }

        private fun selectedText(selectedString: String) {
            if (submissionField != selectedString && submissionField.isNotEmpty()) {
                (context as WorkFlowActivity).btn_submit.visibility = View.VISIBLE
                (context as WorkFlowActivity).btn_next.visibility = View.GONE
                if ((context as WorkFlowActivity).btn_Finish.visibility == View.VISIBLE) {
                    (context as WorkFlowActivity).btn_submit.visibility = View.GONE
                }
            } else if (submissionField.isEmpty()) {
                (context as WorkFlowActivity).btn_submit.visibility = View.GONE
                (context as WorkFlowActivity).btn_next.visibility = View.VISIBLE
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