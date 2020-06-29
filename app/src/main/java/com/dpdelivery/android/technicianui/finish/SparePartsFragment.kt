package com.dpdelivery.android.technicianui.finish

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.SparePartsData
import com.dpdelivery.android.utils.withNotNullNorEmpty
import kotlinx.android.synthetic.main.item_spare_parts.view.*
import kotlinx.android.synthetic.main.layout_spare_parts.*


class SparePartsFragment : DialogFragment(), IAdapterClickListener {
    lateinit var mActivity: FragmentActivity
    lateinit var mContext: Context
    private var partsList: ArrayList<SparePartsData?>? = null
    lateinit var adapterSpares: BasicAdapter
    private var selectedPartsList = ArrayList<Int>()
    private var selectedPartsListName = ArrayList<String>()
    private var partId: String? = null
    private var partName: String? = null
    private var jobId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_spare_parts, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(jobId: Int, partsList: ArrayList<SparePartsData>): SparePartsFragment {
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.OBJECT, partsList)
            bundle.putInt(Constants.PARTS, jobId)
            val fragment = SparePartsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = requireActivity()
        mContext = requireContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            partsList = getParcelableArrayList(Constants.OBJECT)
            jobId = getInt(Constants.PARTS)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterSpares = BasicAdapter(mContext, R.layout.item_spare_parts, adapterClickListener = this)
        val myDivider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        myDivider.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.divider)!!)
        rv_spares.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = adapterSpares
            addItemDecoration(myDivider)
        }
        partsList!!.withNotNullNorEmpty {
            adapterSpares.addList(partsList!!)
        }
        btn_add_spares.setOnClickListener {
            val sharedPreferences = mContext.getSharedPreferences("DrinkPrimeParts_$jobId.txt", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var s = ""
            for (i in selectedPartsList) {
                s += "$i,"
            }
            editor.putString("itemsName", partName)
            editor.putString("key", s)
            editor.apply()
            (context as FinishJobActivity).fetchItemsFromSharedPref()
            dismiss()
        }

    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is SparePartsData && type is View) {
            when (op) {
                Constants.SPARE_PARTS -> {
                    for (i in partsList!!) {
                        if (any.selected && i!!.itemid == any.itemid) {
                            type.iv_msg.setImageResource(R.drawable.ic_checkbox_on)
                            val selectedId = any.itemid
                            any.selected = false
                            selectedPartsList.add(selectedId)
                            selectedPartsListName.add(any.itemname)
                            partId = selectedPartsList.toString()
                            partName = selectedPartsListName.toString()
                        } else if (i!!.itemid == any.itemid) {
                            selectedPartsList.remove(any.itemid)
                            selectedPartsListName.remove(any.itemname)
                            partId = selectedPartsList.toString()
                            partName = selectedPartsListName.toString()
                            type.iv_msg.setImageResource(R.drawable.ic_checkbox)
                            any.selected = true
                        }
                    }
                }
            }
        }
    }
}
