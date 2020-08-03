package com.dpdelivery.android.ui.filteredjobs

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.interfaces.PaginationScrollListener
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.model.DeliveryJobsListRes
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import kotlinx.android.synthetic.main.activity_filtered_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import javax.inject.Inject
import kotlin.math.ceil

class FilteredJobsListActivity : BaseActivity(), FilteredJobsListContract.View, View.OnClickListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: FilteredJobListAdapter
    lateinit var jobsList: ArrayList<Data?>
    private var isLastPage = false
    private var isLoading = false
    private val PAGE_START = 1
    private var TOTAL_PAGES = 0
    private var currentPage = PAGE_START
    private var data: String? = null

    @Inject
    lateinit var presenter: FilteredJobsListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_filtered_jobs_list, layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(false)
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        manager = LinearLayoutManager(this)
        if (intent != null)
            data = intent.getStringExtra("filter")
        when {
            data.equals("NEW") -> {
                setTitle("New Jobs List")
            }
            data.equals("INP") -> {
                setTitle("In-Progress Jobs List")
            }
            data.equals("HLD") -> {
                setTitle("On-Hold Jobs List")
            }
            data.equals("ASG") -> {
                setTitle("Assigned Jobs List")
            }
            data.equals("PKU") -> {
                setTitle("Picked-Up Jobs List")
            }
            data.equals("DLY") -> {
                setTitle("Delayed Jobs List")
            }
            data.equals("REJ") -> {
                setTitle("Rejected Jobs List")
            }
            data.equals("DEL") -> {
                setTitle("Delivered Jobs List")
            }
        }
        showBack()
        rv_filtered_jobs_list.layoutManager = manager
        rv_filtered_jobs_list.itemAnimator = DefaultItemAnimator()
        adapterAsgJobsList = FilteredJobListAdapter(this)
        rv_filtered_jobs_list.adapter = adapterAsgJobsList
        rv_filtered_jobs_list.addOnScrollListener(object : PaginationScrollListener(manager) {
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
        getFilteredJobsList(data!!)
    }


    private fun getFilteredJobsList(data: String) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        presenter.getDeliveryJobsList(status = data)
    }

    private fun getMoreResults() {
        presenter.getMoreDeliveryJobsList(currentPage, data!!)
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

    override fun showDeliveryJobsListRes(res: DeliveryJobsListRes) {
        if (res.data!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.data.withNotNullNorEmpty {
                jobsList = res.data
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

    override fun showMoreDeliveryJobsListRes(res: DeliveryJobsListRes) {
        if (res.data!!.isNotEmpty()) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            res.data.withNotNullNorEmpty {
                jobsList = res.data
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

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
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