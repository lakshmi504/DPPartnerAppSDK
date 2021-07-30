package com.dpdelivery.android.screens.jobslist

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.interfaces.PaginationScrollListener
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.model.techres.StartJobRes
import com.dpdelivery.android.model.techres.SubmiPidRes
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.utils.*
import kotlinx.android.synthetic.main.activity_assigned_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import okhttp3.Headers
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

class JobsListActivity : TechBaseActivity(), JobsListContract.View, View.OnClickListener,
    IAdapterClickListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: JobListAdapter
    lateinit var jobsList: ArrayList<Job?>
    private var isLastPage = false
    private var isLoading = false
    private val PAGE_START = 1
    private var TOTAL_PAGES = 0
    private var currentPage = PAGE_START
    private var data: String? = null
    lateinit var dialog: Dialog
    private val statusFilterMode = ArrayList<String>()
    private var statusFilter: String? = null

    @Inject
    lateinit var presenter: JobsListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(true)
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        manager = LinearLayoutManager(this)
        dialog = CommonUtils.progressDialog(mContext)
        if (intent != null)
            data = intent.getStringExtra("filter")
        when {
            data.equals("ASG") -> {
                setTitle("Assigned Jobs")
            }
            data.equals("INP") -> {
                setTitle("In-Progress Jobs")
            }
            data.equals("COM") -> {
                setTitle("Completed Jobs")
            }
            data.equals("PPN") -> {
                setTitle("Postponed Jobs")
            }
            data.equals("CAN") -> {
                setTitle("Cancelled Jobs")
            }
        }
        showBack()
        rv_asg_jobs_list.layoutManager = manager
        rv_asg_jobs_list.itemAnimator = DefaultItemAnimator()
        adapterAsgJobsList = JobListAdapter(this, adapterClickListener = this, jobType = data!!)
        rv_asg_jobs_list.adapter = adapterAsgJobsList
        rv_asg_jobs_list.addOnScrollListener(object : PaginationScrollListener(manager) {
            override val totalPageCount: Int
                get() = TOTAL_PAGES

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                // mocking network delay for API call
                Handler().postDelayed({ getMoreResults() }, 1000)
            }
        })

        getJobsList(data!!, DateHelper.getCurrentDate())
    }

    private fun getJobsList(data: String, appointmentDate: String) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        if (data == "ASG") {
            presenter.getAssignedJobsList(status = data, appointmentDate = appointmentDate)
        } else {
            presenter.getJobsList(status = data)
        }

    }

    private fun getMoreResults() {
        if (data.equals("ASG")) {
            presenter.getMoreAsgJobsList(currentPage, data!!, DateHelper.getCurrentDate())
        } else {
            presenter.getMoreJobsList(currentPage, data!!)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                init()
            }
            R.id.empty_button -> {
                init()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun showAsgJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                if (data.equals("ASG")) {
                    adapterAsgJobsList.addAll(jobsList)
                } else {
                    TOTAL_PAGES =
                        ceil(res.total?.toDouble()?.div(10.toDouble())!!.toDouble()).toInt()
                    //jobsList.sortWith(Comparator { listItem, t1 -> t1?.appointmentStartTime?.let { listItem?.appointmentStartTime?.compareTo(it) }!! })
                    adapterAsgJobsList.addAll(jobsList)
                    if (currentPage < TOTAL_PAGES) adapterAsgJobsList.addLoadingFooter()
                    else isLastPage = true
                }
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_EMPTY)
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showMoreAsgJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                adapterAsgJobsList.removeLoadingFooter()
                isLoading = false
                TOTAL_PAGES = ceil(res.total?.div(10)!!.toDouble()).toInt()
                adapterAsgJobsList.addAll(jobsList)
                if (currentPage <= TOTAL_PAGES) adapterAsgJobsList.addLoadingFooter()
                else isLastPage = true
            }
        } else {
            adapterAsgJobsList.removeLoadingFooter()
        }
    }

    override fun showVoipRes(res: Headers) {
        dialog.dismiss()
        toast("Call is Connecting..")
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (throwable is HttpException) {
            when (throwable.code()) {
                403 -> {
                    SharedPreferenceManager.clearPreferences()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                else -> {
                    showViewState(MultiStateView.VIEW_STATE_ERROR)
                    toast(throwable.message.toString())
                }
            }
        } else {
            toast(throwable.message.toString())
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Job && type is View) {
            when (op) {
                Constants.ASSIGN_JOB_DETAILS -> {
                    val intent = Intent(this, TechJobDetailsActivity::class.java)
                    intent.putExtra(Constants.ID, any.id.toString())
                    startActivity(intent)
                }
                Constants.CUST_PHONE -> {
                    dialog.show()
                    presenter.getVoipCall(
                        caller = any.assignedTo!!.phoneNumber,
                        receiver = any.customerPhone!!
                    )
                }
                Constants.ALT_CUST_PHONE -> {
                    dialog.show()
                    presenter.getVoipCall(
                        caller = any.assignedTo!!.phoneNumber,
                        receiver = any.customerAltPhone!!
                    )
                }
                Constants.JOB_TYPE -> {
                    val statusDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
                    statusDialog.setContentView(R.layout.layout_status_change)
                    statusDialog.setCancelable(true)
                    (statusDialog.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
                        statusDialog.dismiss()
                    }
                    val statusFilterData = any.agentJobStatuses
                    if (statusFilterData!!.isNotEmpty()) {
                        statusFilterMode.clear()
                        statusFilterMode.add("Select Status")
                        for (item in statusFilterData) {
                            statusFilterMode.add(item?.description!!)
                        }
                    }
                    val spStatusFilter =
                        (statusDialog.findViewById(R.id.sp_status_filter) as Spinner)
                    val note = (statusDialog.findViewById(R.id.et_note) as EditText)
                    (statusDialog.findViewById(R.id.tv_job_id) as TextView).text =
                        any.id!!.toString()
                    val adapterStatusMode = ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statusFilterMode
                    )
                    adapterStatusMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spStatusFilter.adapter = adapterStatusMode
                    spStatusFilter.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                statusFilter = spStatusFilter.selectedItem.toString()
                                if (statusFilter != "Select Status") {
                                    for (statusCodes in statusFilterData) {
                                        if (statusCodes!!.description == statusFilter) {
                                            statusFilter = statusCodes.code
                                            break
                                        }
                                    }
                                }
                            }

                        }
                    if (!(context as Activity).isFinishing) {
                        statusDialog.show()
                    }
                    (statusDialog.findViewById(R.id.btn_submit) as AppCompatButton).setOnClickListener {
                        if (statusFilter != "Select Status") {
                            if (note.text.toString().isNotEmpty()) {
                                dialog.show()
                                val jobStatus = FinishJobIp(
                                    status = statusFilter,
                                    note = note.text!!.toString()
                                )
                               /* presenter.addNote(
                                    any.id,
                                    updateJobIp = UpdateJobIp(note = note.text!!.toString())
                                )*/
                                presenter.updateJob(any.id, jobStatus)
                                statusDialog.dismiss()
                            } else {
                                toast("Note should not be empty")
                            }
                        } else {
                            toast("Please Select Status")
                        }
                    }
                }
            }
        }
    }

    override fun showAddNoteRes(res: StartJobRes) {
        if (res.success!!) {
            dialog.dismiss()
        }
    }

    override fun showUpdateJobRes(res: SubmiPidRes) {
        if (res.success) {
            dialog.dismiss()
            toast("Updated Successfully")
            init()
        } else {
            toast(res.message)
            dialog.dismiss()
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                overridePendingTransition(0, 0)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
        finish()
    }
}