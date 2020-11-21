package com.dpdelivery.android.technicianui.workflow.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
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
import com.dpdelivery.android.technicianui.workflow.WorkFlowActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_work_flow.*
import kotlinx.android.synthetic.main.item_element_list.*


class ElementListViewHolder(var context: Context, var adapterClickListener: IAdapterClickListener? = null, var jobId: Int) : RecyclerView.Adapter<ElementListViewHolder.ViewHolder>() {

    private var list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>? = null
    var sharedpreferences: SharedPreferences = context.getSharedPreferences("$jobId${Constants.MyPREFERENCES}", Context.MODE_PRIVATE)
    private var selectedListName = ArrayList<String>()
    private var selectedString = String()

    fun addList(list: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_element_list)
        return ViewHolder(view, adapterClickListener, sharedpreferences, jobId, selectedListName, selectedString)
    }

    override fun getItemCount() = if (list != null && list!!.size > 0) list!!.size else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list!!.size > 0) {
            holder.bind(context, list!![position], position)
            return
        }
        holder.bind(context, holder, position)
    }

    class ViewHolder(override val containerView: View, var adapterClickListener: IAdapterClickListener? = null, var sharedpreferences: SharedPreferences, var jobId: Int, var selectedListName: ArrayList<String>?, var selectedString: String) : RecyclerView.ViewHolder(containerView), LayoutContainer, AdapterView.OnItemSelectedListener {
        @SuppressLint("ResourceAsColor")
        fun bind(context: Context, item: Any, pos: Int) {
            if (item is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element) {
                tv_name.text = item.name
                if (item.showType == "VISIBLE" && item.inputApi == "TEXT") {
                    et_add_text.visibility = View.VISIBLE
                    et_add_text!!.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(editable: Editable?) {
                            if (et_add_text.text.isNullOrEmpty()) {
                                iv_update.visibility = View.GONE
                            } else {
                                iv_update.visibility = View.VISIBLE
                            }
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }
                    })
                    if (!item.value.isNullOrEmpty()) {
                        et_add_text.setText(item.value.toString())
                        iv_update.visibility = View.GONE
                    }
                    if (item.showType == "VISIBLE" && item.workflowElementType == "DROPDOWN" && item.inputApi == "TEXT") {
                        if (!item.dropdownContents.isNullOrEmpty()) {
                            ll_spinner_center.visibility = View.VISIBLE
                            selectedListName!!.clear()
                            if (item.name.equals("PaymentType")) {
                                selectedListName!!.add("-- Select Type --")
                            } else {
                                selectedListName!!.add("-- Select Status --")
                            }
                            val dropDownList = item.dropdownContents
                            for (option in dropDownList) {
                                selectedListName!!.add(option!!)
                            }
                            val adapterMode = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, selectedListName!!)
                            adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner_center!!.adapter = adapterMode
                            spinner_center!!.onItemSelectedListener = this
                        }
                        et_add_text.visibility = View.VISIBLE
                        if (!item.value.isNullOrEmpty()) {
                            et_add_text.setText(item.value.toString())
                        }
                        if (et_add_text.text.toString().isNotEmpty()) {
                            iv_dropdown_update.visibility = View.GONE
                        } else {
                            iv_dropdown_update.visibility = View.VISIBLE
                        }
                        et_add_text.background = null
                        et_add_text.isEnabled = false
                        iv_update.visibility = View.GONE

                    }

                } else if (item.showType == "VISIBLE" && item.inputApi == "IMAGE") {
                    btn_add_image.visibility = View.VISIBLE
                    if (!item.value.isNullOrEmpty()) {
                        CommonUtils.setImage(context, btn_add_image, item.value)
                    }
                }
                if (item.showType == "MASKED" && item.inputApi == "TEXT") {
                    if (item.value!!.isNotEmpty()) {
                        et_add_text.visibility = View.VISIBLE
                        val text = item.value
                        et_add_text.setText(text.replace(item.value, "*"))
                    }
                    if (item.showType == "MASKED" && item.inputApi == "TEXT" && item.workflowElementType == "DROPDOWN") {
                        ll_spinner_center.visibility = View.VISIBLE
                        ll_spinner_center.isEnabled = false
                    }
                } else if (item.showType == "MASKED" && item.inputApi == "TEXT") {
                    btn_add_image.visibility = View.VISIBLE
                    btn_add_image.isEnabled = false
                }

                if (!item.optional!! && item.value.isNullOrEmpty() && item.inputApi == "TEXT" && item.workflowElementType == "MANUAL") {
                    iv_mandatory.visibility = View.VISIBLE
                }
                if (!item.optional && item.value.isNullOrEmpty() && item.inputApi == "TEXT" && item.workflowElementType == "DROPDOWN") {
                    iv_mandatory1.visibility = View.VISIBLE
                }
                if (!item.optional && item.value.isNullOrEmpty() && item.inputApi == "IMAGE" && item.workflowElementType == "MANUAL") {
                    iv_mandatory2.visibility = View.VISIBLE
                }
                if (!item.optional && item.value.isNullOrEmpty() && item.inputApi.isNullOrEmpty()) {
                    iv_mandatory.visibility = View.GONE
                    iv_mandatory1.visibility = View.GONE
                    iv_mandatory2.visibility = View.GONE
                }
                iv_update.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_TEXT)
                }
                btn_add_image.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_IMAGE)
                }
                btn_upload_image.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_UPLOAD_IMAGE)
                }
                iv_dropdown_update.setOnClickListener {
                    adapterClickListener?.onclick(any = item, pos = adapterPosition, type = itemView, op = Constants.ELEMENT_DROPDOWN)
                }
                (context as WorkFlowActivity).btn_next.setOnClickListener {
                    when {
                        iv_mandatory.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        iv_mandatory1.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        iv_mandatory2.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            (context as WorkFlowActivity).nextStep()
                        }
                    }
                }
                (context as WorkFlowActivity).btn_Finish.setOnClickListener {
                    when {
                        iv_mandatory.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        iv_mandatory1.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        iv_mandatory2.visibility == View.VISIBLE -> {
                            Toast.makeText(context, "Please submit mandatory fields", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            (context as WorkFlowActivity).finishJob()
                        }
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent!!.id) {
                R.id.spinner_center -> {
                    selectedString = spinner_center!!.selectedItem.toString()
                    if (selectedString != "-- Select Type --")
                        if (selectedString != "-- Select Status --") {
                            iv_dropdown_update.visibility = View.VISIBLE
                        }
                }
            }
        }
    }
}