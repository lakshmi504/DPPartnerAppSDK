package com.dpdelivery.android.technicianui.techjobslist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.dpdelivery.android.utils.setDrawableRight
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import kotlinx.android.synthetic.main.activity_assigned_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_search_filter.*
import javax.inject.Inject

class TechJobsListActivity : TechBaseActivity(), TechJobsListContract.View, IAdapterClickListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: BasicAdapter
    lateinit var jobsList: ArrayList<Job?>
    private var filter: String? = null

    @Inject
    lateinit var presenter: TechJobsListPresenter
    private val filterMode: Array<String> = arrayOf<String>("Status Filter", "Assigned", "In-Progress", "Completed")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Jobs List")
        setUpBottomNavView(false)
        loadDefaultSpinner()
        search_filter.visibility = View.GONE
        tv_search.setOnClickListener(this)
        et_search.setDrawableRight(R.drawable.ic_search)
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        tv_search.visibility = View.VISIBLE
        sp_filter.visibility = View.VISIBLE
        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                presenter.getSearchJobsList(search = et_search.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun getAssignedJobsList() {
        multistateview1.viewState = MultiStateView.VIEW_STATE_LOADING
        manager = LinearLayoutManager(this)
        rv_asg_jobs_list.layoutManager = manager
        adapterAsgJobsList = BasicAdapter(this, R.layout.item_asg_jobs_list, adapterClickListener = this)
        rv_asg_jobs_list.apply {
            presenter.getFilterJobsList(status = "ASG")
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
        loadDefaultSpinner()
        et_search.text?.clear()
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
                    //multistateview1.viewState = MultiStateView.VIEW_STATE_LOADING
                    //presenter.getFilterJobsList(status = filter.toString())
                    startActivity(Intent(this, JobsListActivity::class.java).putExtra("filter", filter))
                    overridePendingTransition(0, 0)
                }
            }
        }
    }

    override fun showAsgJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            multistateview1.viewState = MultiStateView.VIEW_STATE_CONTENT
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                adapterAsgJobsList.addList(jobsList)
            }
        } else {
            multistateview1.viewState = MultiStateView.VIEW_STATE_EMPTY
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.toString())
        multistateview1.viewState = MultiStateView.VIEW_STATE_CONTENT
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
