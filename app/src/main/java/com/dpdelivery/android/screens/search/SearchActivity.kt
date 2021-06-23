package com.dpdelivery.android.screens.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techres.*
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListContract
import com.dpdelivery.android.screens.techjobslist.TechJobsListPresenter
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_header_search.*
import okhttp3.Headers
import retrofit2.HttpException
import javax.inject.Inject

class SearchActivity : DaggerAppCompatActivity(), TechJobsListContract.View, IAdapterClickListener, View.OnClickListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: BasicAdapter
    lateinit var jobsList: ArrayList<Job?>
    var toolbar: Toolbar? = null

    @Inject
    lateinit var presenter: TechJobsListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        init()
    }

    override fun init() {
        mContext = this
        toolbar = findViewById<View>(R.id.toolbar1) as Toolbar?
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        iv_close.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        manager = LinearLayoutManager(this)
        rv_search_jobs_list.layoutManager = manager
        adapterAsgJobsList = BasicAdapter(this, R.layout.item_asg_jobs_list, adapterClickListener = this)
        rv_search_jobs_list.apply {
            adapter = adapterAsgJobsList
            adapter!!.notifyDataSetChanged()
        }
        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (et_search.text.isNullOrEmpty()) {
                    iv_close.visibility = View.INVISIBLE
                } else {
                    iv_close.visibility = View.VISIBLE
                    multistateview2.viewState = MultiStateView.VIEW_STATE_LOADING
                    presenter.getSearchJobsList(search = et_search.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun showAsgJobsListRes(res: ASGListRes) {
        if (res.jobs!!.isNotEmpty()) {
            multistateview2.viewState = MultiStateView.VIEW_STATE_CONTENT
            res.jobs.withNotNullNorEmpty {
                jobsList = res.jobs
                rv_search_jobs_list.visibility = View.VISIBLE
                adapterAsgJobsList.addList(jobsList)
            }
        } else {
            multistateview2.viewState = MultiStateView.VIEW_STATE_EMPTY
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showVoipRes(res: Headers) {

    }

    override fun showUpdateJobRes(res: SubmiPidRes) {

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

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Job && type is View) {
            when (op) {
                Constants.ASSIGN_JOB_DETAILS -> {
                    val intent = Intent(this, TechJobDetailsActivity::class.java)
                    intent.putExtra(Constants.ID, any.id.toString())
                    startActivity(intent)
                }
            }
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
            R.id.iv_close -> {
                et_search.text?.clear()
            }
            R.id.iv_back -> {
                finish()
            }
        }
    }

    override fun showAddNoteRes(res: StartJobRes) {

    }

    override fun showUpdateTokenRes(res: CommonRes) {

    }

    override fun showPartnerDetails(res: PartnerDetailsRes) {

    }
}