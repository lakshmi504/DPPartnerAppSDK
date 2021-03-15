package com.dpdelivery.android.technicianui.techjobslist

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.technicianui.jobslist.JobsListActivity
import com.dpdelivery.android.technicianui.search.SearchActivity
import com.dpdelivery.android.utils.*
import kotlinx.android.synthetic.main.activity_assigned_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import okhttp3.Headers
import java.util.*
import javax.inject.Inject

class TechJobsListActivity : TechBaseActivity(), TechJobsListContract.View, IAdapterClickListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: BasicAdapter
    lateinit var jobsList: ArrayList<Job?>
    private var filter: String? = null
    lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: TechJobsListPresenter
    private val filterMode: Array<String> = arrayOf<String>("Status Filter", "Assigned", "In-Progress", "Completed")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Jobs List")
        setUpBottomNavView(true)
        loadDefaultSpinner()
        search_filter.visibility = View.GONE
        tv_search.setOnClickListener(this)
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        tv_search.visibility = View.VISIBLE
        sp_filter.visibility = View.VISIBLE
        dialog = CommonUtils.progressDialog(context)
    }

    private fun forceUpdate() {
        val packageManager = this.packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val currentVersion = packageInfo?.versionName
        ForceUpdateAsync(currentVersion!!, this).execute()
        CommonUtils.setCurrentVersion(currentVersion)
    }

    private fun getAssignedJobsList() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        manager = LinearLayoutManager(this)
        rv_asg_jobs_list.layoutManager = manager
        adapterAsgJobsList = BasicAdapter(this, R.layout.item_asg_jobs_list, adapterClickListener = this)
        rv_asg_jobs_list.apply {
            if (CommonUtils.getRole() == "ROLE_Technician") {
                presenter.getFilterJobsList(status = "ASG", appointmentDate = DateHelper.getCurrentDate())
            } else {
                presenter.getAssignedJobsList(status = "ASG", appointmentDate = DateHelper.getCurrentDate())
            }
            adapter = adapterAsgJobsList
            adapter!!.notifyDataSetChanged()
        }

    }

    private fun loadDefaultSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterMode)
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_filter!!.adapter = adapterMode
        sp_filter.onItemSelectedListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                getAssignedJobsList()
            }
            R.id.empty_button -> {
                getAssignedJobsList()
            }
            R.id.tv_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        bottom_navigation.selectedItemId = R.id.action_jobs
        forceUpdate()
        loadDefaultSpinner()
        getAssignedJobsList()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_filter -> {
                (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                (parent.getChildAt(0) as TextView).textSize = 14f
                filter = sp_filter!!.selectedItem.toString()
                when (filter) {
                    "In-Progress" -> {
                        filter = "INP"
                    }
                    "Assigned" -> {
                        filter = "ASG"
                    }
                    "Completed" -> {
                        filter = "COM"
                    }
                }
                if (filter != "Status Filter") {
                    startActivity(Intent(this, JobsListActivity::class.java).putExtra("filter", filter))
                    overridePendingTransition(0, 0)
                }
            }
        }
    }

    override fun showAsgJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                adapterAsgJobsList.addList(jobsList)
               // tv_total_jobs.text = res.total.toString()
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                adapterAsgJobsList.addList(jobsList)
                rl_jobs.visibility = View.GONE
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
        showViewState(MultiStateView.VIEW_STATE_ERROR)
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Job && type is View) {
            when (op) {
                Constants.ASSIGN_JOB_DETAILS -> {
                    if (CommonUtils.getRole() == "ROLE_Technician") {
                        val intent = Intent(this, TechJobDetailsActivity::class.java)
                        intent.putExtra(Constants.ID, any.id)
                        intent.putExtra(Constants.POSITION, pos)
                        intent.putExtra(Constants.STATUS, any.status!!.code)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, TechJobDetailsActivity::class.java)
                        intent.putExtra(Constants.ID, any.id)
                        intent.putExtra(Constants.POSITION, pos)
                        intent.putExtra(Constants.STATUS, any.status!!.code)
                        startActivity(intent)
                    }

                }
                Constants.CUST_PHONE -> {
                    dialog.show()
                    presenter.getVoipCall(caller = any.assignedTo!!.phoneNumber, receiver = any.customerPhone!!)
                }
                Constants.ALT_CUST_PHONE -> {
                    dialog.show()
                    presenter.getVoipCall(caller = any.assignedTo!!.phoneNumber, receiver = any.customerAltPhone!!)
                }
            }
        }
    }

    override fun showVoipRes(res: Headers) {
        dialog.dismiss()
        toast("Call is Connecting..")
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
