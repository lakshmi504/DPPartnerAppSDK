package com.dpdelivery.android.screens.workflow.workflowadapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_element_list.*


class ElementListAdapter(
    var context: Context,
    var adapterClickListener: IAdapterClickListener? = null,
    val stepMap: MutableMap<String, String>,
    val stepsFinished: MutableMap<String, Boolean>,
    var activationElementId: Int,
    var wifiConfigId: Int,
    var syncElementId: Int,
    var sparePartId: Int
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
            activationElementId,
            wifiConfigId,
            syncElementId,
            sparePartId
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
        val activationElementId: Int,
        val wifiConfigId: Int,
        val syncElementId: Int,
        val sparePartId: Int
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        @SuppressLint("ResourceAsColor")
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element) {
                when (item.showType) {
                    "VISIBLE" -> {
                        when (item.workflowElementType) {
                            "MANUAL" -> {
                                when (item.inputApi) {
                                    "TEXT" -> {
                                        tv_name.text = item.name
                                        tv_name.visibility = View.VISIBLE
                                        if (item.name.toString() == "PaymentCollected") {
                                            et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                                        }
                                        if (item.name.toString() == "InputTDS") {
                                            et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                                        }
                                        if (item.name.toString() == "OutputTDS") {
                                            et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                                        }
                                        if (item.name.toString() == "HappyCode") {
                                            et_add_text.filters =
                                                arrayOf<InputFilter>(InputFilter.LengthFilter(6))
                                            et_add_text.inputType = InputType.TYPE_CLASS_NUMBER
                                        }
                                        if (item.name.toString() == "DeviceCode") {
                                            et_add_text.filters =
                                                arrayOf<InputFilter>(InputFilter.LengthFilter(10))
                                            et_add_text.inputType =
                                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
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
                                                        et_purifier_id!!.text.toString()
                                                            .trim { it <= ' ' }
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
                                                    if (et_purifier_id!!.text.toString()
                                                            .trim { it <= ' ' }
                                                            .isEmpty()
                                                    ) {
                                                        Toast.makeText(
                                                            context,
                                                            "Purifier Id is required",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        adapterClickListener?.onclick(
                                                            any = item,
                                                            pos = pos,
                                                            type = itemView,
                                                            op = Constants.REFRESH_STATUS
                                                        )
                                                    }
                                                }
                                            }
                                            wifiConfigId == item.id -> {
                                                et_add_text.visibility = View.GONE
                                                layout_wifi_config.visibility = View.VISIBLE
                                                btn_setup_wifi.setOnClickListener {
                                                    adapterClickListener?.onclick(
                                                        any = item,
                                                        pos = pos,
                                                        type = itemView,
                                                        op = Constants.SET_UP_WIFI
                                                    )
                                                }
                                                iv_wifi_bot_refresh.setOnClickListener {
                                                    adapterClickListener?.onclick(
                                                        any = item,
                                                        pos = pos,
                                                        type = itemView,
                                                        op = Constants.REFRESH_WIFI
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
                                                et_add_text!!.addTextChangedListener(object :
                                                    TextWatcher {
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
                                                        if (!item.optional!! && p0.toString()
                                                                .isEmpty()
                                                        ) {
                                                            stepsFinished[item.id.toString()] =
                                                                false
                                                            iv_mandatory.visibility = View.VISIBLE
                                                        } else {
                                                            stepMap[item.id.toString()] =
                                                                p0.toString()
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
                                    "IMAGE" -> {
                                        btn_add_image.visibility = View.VISIBLE
                                        tv_name.visibility = View.VISIBLE
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
                                }
                            }
                            "DROPDOWN" -> {
                                tv_name.visibility = View.VISIBLE
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
                                    val data = ArrayList<String>()
                                    data.clear()
                                    data.add("-")
                                    for (i in item.dropdownContents) {
                                        data.add(i!!)
                                    }
                                    ll_spinner_center.visibility = View.VISIBLE
                                    for (i in data.indices) {
                                        if (!item.value.isNullOrEmpty()) {
                                            if (data[i].equals(
                                                    (item.value),
                                                    true
                                                )
                                            ) {
                                                val adapterMode: ArrayAdapter<String?> =
                                                    object : ArrayAdapter<String?>(
                                                        context,
                                                        android.R.layout.simple_spinner_item,
                                                        data as List<String?>
                                                    ) {
                                                        override fun isEnabled(position: Int): Boolean {
                                                            return position != 0
                                                        }

                                                        override fun getDropDownView(
                                                            position: Int, convertView: View?,
                                                            parent: ViewGroup
                                                        ): View {
                                                            val view = super.getDropDownView(
                                                                position,
                                                                convertView,
                                                                parent
                                                            )
                                                            val tv = view as TextView
                                                            if (position == 0) {
                                                                // Set the hint text color grey
                                                                tv.setTextColor(Color.GRAY)
                                                            } else {
                                                                tv.setTextColor(Color.BLACK)
                                                            }
                                                            return view
                                                        }
                                                    }
                                                adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                spinner_center!!.adapter = adapterMode
                                                spinner_center!!.onItemSelectedListener =
                                                    object : AdapterView.OnItemSelectedListener {
                                                        override fun onNothingSelected(parent: AdapterView<*>?) {

                                                        }

                                                        override fun onItemSelected(
                                                            parent: AdapterView<*>?,
                                                            view: View?,
                                                            position: Int,
                                                            id: Long
                                                        ) {
                                                            if (position > 0) {
                                                                val selectedString =
                                                                    spinner_center!!.selectedItem.toString()
                                                                iv_mandatory1.visibility =
                                                                    View.INVISIBLE
                                                                stepMap[item.id.toString()] =
                                                                    selectedString
                                                                stepsFinished[item.id.toString()] =
                                                                    true
                                                            }
                                                        }
                                                    }
                                                spinner_center.setSelection(i)
                                            }
                                        } else {
                                            val adapterMode: ArrayAdapter<String?> =
                                                object : ArrayAdapter<String?>(
                                                    context,
                                                    android.R.layout.simple_spinner_item,
                                                    data as List<String?>
                                                ) {
                                                    override fun isEnabled(position: Int): Boolean {
                                                        return position != 0
                                                    }

                                                    override fun getDropDownView(
                                                        position: Int, convertView: View?,
                                                        parent: ViewGroup
                                                    ): View {
                                                        val view = super.getDropDownView(
                                                            position,
                                                            convertView,
                                                            parent
                                                        )
                                                        val tv = view as TextView
                                                        if (position == 0) {
                                                            // Set the hint text color grey
                                                            tv.setTextColor(Color.GRAY)
                                                        } else {
                                                            tv.setTextColor(Color.BLACK)
                                                        }
                                                        return view
                                                    }
                                                }
                                            adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinner_center!!.adapter = adapterMode
                                            spinner_center!!.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                                    }

                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>?,
                                                        view: View?,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        if (position > 0) {
                                                            val selectedString =
                                                                spinner_center!!.selectedItem.toString()
                                                            iv_mandatory1.visibility =
                                                                View.INVISIBLE
                                                            stepMap[item.id.toString()] =
                                                                selectedString
                                                            stepsFinished[item.id.toString()] =
                                                                true
                                                        }
                                                    }
                                                }
                                            spinner_center.setSelection(0)
                                        }
                                    }
                                }
                            }
                            "API_INPUT" -> {
                                if (sparePartId == item.id) {
                                    ll_spare_parts.visibility = View.VISIBLE
                                    tv_name.visibility = View.VISIBLE
                                    tv_name.text = item.name
                                    if (!item.optional!!) {
                                        //iv_mandatory.visibility = View.VISIBLE
                                        stepsFinished[item.id.toString()] = false
                                    } else {
                                        // iv_mandatory.visibility = View.INVISIBLE
                                        stepMap[item.id.toString()] = ""
                                        stepsFinished[item.id.toString()] = true
                                    }
                                    if (!item.optional && !item.value.isNullOrEmpty()) {
                                        //iv_mandatory.visibility = View.INVISIBLE
                                        stepsFinished[item.id.toString()] = true
                                    }

                                    adapterClickListener?.onclick(
                                        any = item,
                                        pos = pos,
                                        type = itemView,
                                        op = Constants.SPARE_PARTS
                                    )
                                } else {
                                    ll_spares.visibility = View.VISIBLE
                                    tv_name.visibility = View.VISIBLE
                                    tv_name.text = item.name
                                    if (!item.optional!!) {
                                        if (!item.value.isNullOrEmpty()) {
                                            iv_mandatory_list.visibility = View.INVISIBLE
                                            stepsFinished[item.id.toString()] = true
                                        } else {
                                            iv_mandatory_list.visibility = View.VISIBLE
                                            stepsFinished[item.id.toString()] = false
                                        }
                                    } else {
                                        iv_mandatory_list.visibility = View.INVISIBLE
                                        stepMap[item.id.toString()] = ""
                                        stepsFinished[item.id.toString()] = true
                                    }
                                    adapterClickListener?.onclick(
                                        any = item,
                                        pos = pos,
                                        type = itemView,
                                        op = Constants.API_INPUT
                                    )

                                }
                            }
                        }
                    }
                    "DISABLED" -> {
                        et_add_text.visibility = View.VISIBLE
                        tv_name.visibility = View.VISIBLE
                        tv_name.text = item.name
                        if (!item.value.isNullOrEmpty()) {
                            et_add_text.setText(item.value.toString())
                        } else {
                            et_add_text.setText(context.getString(R.string.not_mentioned))
                        }
                        et_add_text.background = null
                        et_add_text.isEnabled = false
                    }
                    "MASKED" -> {
                        when (item.workflowElementType) {
                            "MANUAL" -> {
                                tv_name.visibility = View.VISIBLE
                                tv_name.text = item.name
                                if (item.value!!.isNotEmpty()) {
                                    et_add_text.visibility = View.VISIBLE
                                    val text = item.value
                                    et_add_text.setText(text.replace(item.value, "*****"))
                                }
                                btn_add_image.visibility = View.VISIBLE
                                btn_add_image.isEnabled = false
                            }
                            "DROPDOWN" -> {
                                tv_name.visibility = View.VISIBLE
                                tv_name.text = item.name
                                ll_spinner_center.visibility = View.VISIBLE
                                ll_spinner_center.isEnabled = false
                            }
                        }
                    }
                }
            }
        }
    }
}