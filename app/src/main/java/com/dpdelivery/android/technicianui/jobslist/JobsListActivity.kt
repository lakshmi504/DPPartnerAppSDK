package com.dpdelivery.android.technicianui.jobslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.interfaces.PaginationScrollListener
import com.dpdelivery.android.model.techres.ASGListRes
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import kotlinx.android.synthetic.main.activity_assigned_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import javax.inject.Inject
import kotlin.math.ceil

class JobsListActivity : TechBaseActivity(), JobsListContract.View, View.OnClickListener, IAdapterClickListener {

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

    @Inject
    lateinit var presenter: JobsListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(false)
        search_filter.visibility = View.GONE
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        manager = LinearLayoutManager(this)
        if (intent != null)
            data = intent.getStringExtra("filter")
        if (data.equals("ASG")) {
            setTitle("Assigned Jobs List")
        } else if (data.equals("INP")) {
            setTitle("In-Progress Jobs List")
        } else if (data.equals("COM")) {
            setTitle("Completed Jobs List")
        }
        showBack()
        rv_asg_jobs_list.layoutManager = manager
        rv_asg_jobs_list.itemAnimator = DefaultItemAnimator()
        adapterAsgJobsList = JobListAdapter(this)
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
        getAssignedJobsList(data!!)
    }

    private fun getAssignedJobsList(data: String) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        presenter.getAssignedJobsList(status = data)
    }

    private fun getMoreResults() {
        presenter.getMoreJobsList(currentPage, data!!)
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
                TOTAL_PAGES = ceil(res.total?.toDouble()?.div(10.toDouble())!!.toDouble()).toInt()
                adapterAsgJobsList.addAll(jobsList)
                if (currentPage < TOTAL_PAGES) adapterAsgJobsList.addLoadingFooter()
                else isLastPage = true
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

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Job && type is View) {
            when (op) {
                Constants.ASSIGN_JOB_DETAILS -> {
                    val intent = Intent(this, TechJobDetailsActivity::class.java)
                    intent.putExtra(Constants.ID, any.id)
                    startActivity(intent)
                }
            }
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.toString())
        showViewState(MultiStateView.VIEW_STATE_ERROR)
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                overridePendingTransition(0, 0)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
        finish()
    }
}