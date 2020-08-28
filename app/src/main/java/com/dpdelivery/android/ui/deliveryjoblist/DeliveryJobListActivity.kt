package com.dpdelivery.android.ui.deliveryjoblist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.model.DeliveryJobsListRes
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.ui.deliveryjob.DeliveryJobActivity
import com.dpdelivery.android.ui.search.DeliverySearchActivity
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_delivery_job_list.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_header.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class DeliveryJobListActivity : BaseActivity(), DeliveryJobsListContract.View, View.OnClickListener,
        IAdapterClickListener, AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    lateinit var jobsList: ArrayList<Data?>

    @Inject
    lateinit var presenter: DeliveryJobsListPresenter
    lateinit var adapterJobsList: BasicAdapter
    lateinit var manager: LinearLayoutManager
    private var mode: String? = null
    private var jobModeType: String? = null
    private val statusMode: Array<String> = arrayOf<String>("Status Filter", "New", "Assigned", "Picked-Up", "In-Progress", "Delayed", "On-Hold", "Rejected", "Delivered")
    private val jobType: Array<String> = arrayOf<String>("Job Type", "Installation", "UnInstallation")

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_delivery_job_list, layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Jobs List")
        setUpBottomNavView(true)
        search_filter.visibility = View.GONE
        empty_button.setOnClickListener(this)
        error_button.setOnClickListener(this)
        fb_datePicker.setOnClickListener(this)
        iv_search.visibility = View.VISIBLE
        sp_filter.visibility = View.VISIBLE
        sp_type.visibility = View.VISIBLE
        fb_datePicker.visibility = View.VISIBLE
        iv_search.setOnClickListener(this)
        loadDefaultSpinner()
        loadDefaultType()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    @RequiresApi(Build.VERSION_CODES.ECLAIR)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_filter -> {
                (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).textSize = 14f
                (parent.getChildAt(0) as TextView).gravity = Gravity.END
                mode = sp_filter!!.selectedItem.toString()
                if (mode == "New") {
                    mode = "NEW"
                } else if (mode == "In-Progress") {
                    mode = "INP"
                } else if (mode == "On-Hold") {
                    mode = "HLD"
                } else if (mode == "Assigned") {
                    mode = "ASG"
                } else if (mode == "Picked-Up") {
                    mode = "PKU"
                } else if (mode == "Delayed") {
                    mode = "DLY"
                } else if (mode == "Rejected") {
                    mode = "REJ"
                } else if (mode == "Delivered") {
                    mode = "DEL"
                }
                if (mode != "Status Filter" && jobModeType != "Job Type") {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getFilterJobsList(status = mode.toString(), jobType = jobModeType.toString())
                    /*startActivity(Intent(this, FilteredJobsListActivity::class.java).putExtra("filter", mode))
                    overridePendingTransition(0, 0)*/
                } else if (mode != "Status Filter" && jobModeType == "Job Type") {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getFilterJobsList(status = mode.toString(), jobType = "")
                }
            }
            R.id.sp_type -> {
                (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).textSize = 14f
                (parent.getChildAt(0) as TextView).gravity = Gravity.END
                jobModeType = sp_type!!.selectedItem.toString()
                if (jobModeType == "Installation") {
                    jobModeType = "INS"
                } else if (jobModeType == "UnInstallation") {
                    jobModeType = "UIN"
                }
                if (jobModeType != "Job Type" && mode != "Status Filter") {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getFilterJobsList(status = mode.toString(), jobType = jobModeType.toString())
                    /*startActivity(Intent(this, FilteredJobsListActivity::class.java).putExtra("filter", mode))
                    overridePendingTransition(0, 0)*/
                } else if (mode == "Status Filter" && jobModeType != "Job Type") {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getFilterJobsList(status = "", jobType = jobModeType.toString())
                }
            }
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                loadDefaultSpinner()
                loadDefaultType()
                getDeliveryJobsList()
            }
            R.id.empty_button -> {
                loadDefaultSpinner()
                loadDefaultType()
                getDeliveryJobsList()
            }
            R.id.iv_search -> {
                startActivity(Intent(this, DeliverySearchActivity::class.java))
            }
            R.id.fb_datePicker -> {
                val builder = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                val now = Calendar.getInstance()
                builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
                val picker = builder.build()
                picker.show(supportFragmentManager, picker.toString())
                picker.addOnNegativeButtonClickListener {
                    picker.dismiss()
                }
                picker.addOnPositiveButtonClickListener {
                    // toast("${it.first}:: to :: ${it.second}")
                    val formatter = SimpleDateFormat("ddMMMyyyy")
                    val startDateString = formatter.format(Date(it.first!!))
                    val endDateString = formatter.format(Date(it.second!!))
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    presenter.getDeliveryJobsListByDate(startDateString, endDateString)
                }
            }
        }
    }

    private fun loadDefaultSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusMode)
        adapterMode.setDropDownViewResource(R.layout.spinner_textview_items)
        sp_filter!!.adapter = adapterMode
        sp_filter.onItemSelectedListener = this
    }

    private fun loadDefaultType() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jobType)
        adapterMode.setDropDownViewResource(R.layout.spinner_textview_items)
        sp_type!!.adapter = adapterMode
        sp_type.onItemSelectedListener = this
    }

    private fun getDeliveryJobsList() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        manager = LinearLayoutManager(this)
        rv_jobs_list.layoutManager = manager
        adapterJobsList = BasicAdapter(this, R.layout.item_delivery_jobs_list, adapterClickListener = this)
        rv_jobs_list.apply {
            presenter.getDeliveryJobsList()
            adapter = adapterJobsList
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        bottom_navigation.selectedItemId = R.id.action_jobs_list
        loadDefaultSpinner()
        loadDefaultType()
        getDeliveryJobsList()
    }

    override fun showDeliveryJobsListRes(res: DeliveryJobsListRes) {
        if (res.data!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.data.withNotNullNorEmpty {
                jobsList = res.data
                adapterJobsList.addList(jobsList)
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_textView.text = "No Data Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message ?: getString(R.string.error_something_wrong))
        showViewState(MultiStateView.VIEW_STATE_ERROR)
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Data && type is View) {
            when (op) {
                Constants.JOB_DETAILS -> {
                    val intent = Intent(this, DeliveryJobActivity::class.java)
                    intent.putExtra(Constants.ID, any.id)
                    startActivity(intent)
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}
