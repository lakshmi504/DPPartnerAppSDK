package com.dpdelivery.android.ui.search

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
import com.dpdelivery.android.model.Data
import com.dpdelivery.android.model.DeliveryJobsListRes
import com.dpdelivery.android.model.techres.Job
import com.dpdelivery.android.ui.deliveryjob.DeliveryJobActivity
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobsListContract
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobsListPresenter
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_delivery_search.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_header_search.*
import javax.inject.Inject

class DeliverySearchActivity : DaggerAppCompatActivity(), DeliveryJobsListContract.View, IAdapterClickListener, View.OnClickListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterJobsList: BasicAdapter
    lateinit var jobsList: ArrayList<Data?>
    var toolbar: Toolbar? = null

    @Inject
    lateinit var presenter: DeliveryJobsListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_search)
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
        adapterJobsList = BasicAdapter(this, R.layout.item_delivery_jobs_list, adapterClickListener = this)
        rv_search_jobs_list.apply {
            adapter = adapterJobsList
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

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is Job && type is View) {
            when (op) {
                Constants.JOB_DETAILS -> {
                    val intent = Intent(this, DeliveryJobActivity::class.java)
                    intent.putExtra(Constants.ID, any.id)
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

    override fun showDeliveryJobsListRes(res: DeliveryJobsListRes) {
        if (res.data!!.isNotEmpty()) {
            multistateview2.viewState = MultiStateView.VIEW_STATE_CONTENT
            res.data.withNotNullNorEmpty {
                jobsList = res.data
                rv_search_jobs_list.visibility = View.VISIBLE
                adapterJobsList.addList(jobsList)
            }
        } else {
            multistateview2.viewState = MultiStateView.VIEW_STATE_EMPTY
            empty_textView.text = "No Jobs Found"
            empty_button.text = "Back to list"
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.toString())
        multistateview2.viewState = MultiStateView.VIEW_STATE_ERROR
    }
}