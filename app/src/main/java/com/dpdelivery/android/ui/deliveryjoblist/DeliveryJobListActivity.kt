package com.dpdelivery.android.ui.deliveryjoblist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.dpdelivery.android.utils.setDrawableRight
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import kotlinx.android.synthetic.main.activity_delivery_job_list.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_search_filter.*
import javax.inject.Inject

class DeliveryJobListActivity : BaseActivity(), DeliveryJobsListContract.View, View.OnClickListener,
        IAdapterClickListener, AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    lateinit var jobsList: ArrayList<Data?>
    @Inject
    lateinit var presenter: DeliveryJobsListPresenter
    lateinit var adapterJobsList: BasicAdapter
    private var isScrolling: Boolean = false
    private var currentItems: Int = 0
    private var totalItems: Int = 0
    private var scrollOutItems: Int = 0
    lateinit var manager: LinearLayoutManager
    private var mode: String? = null
    private var dialog: Dialog? = null
    private var modeSpin: Spinner? = null
    private val statusMode: Array<String> = arrayOf<String>("Status Filter", "New", "Assigned", "Picked-Up", "In-Progress", "Delayed", "On-Hold", "Rejected", "Delivered")

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_delivery_job_list, layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Delivery Jobs List")
        empty_button.setOnClickListener(this)
        error_button.setOnClickListener(this)
        et_search.setDrawableRight(R.drawable.ic_search)
        loadDefaultSpinner()
        getDeliveryJobsList()
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

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_mode -> {
                mode = sp_mode!!.selectedItem.toString()
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
                if (mode != "Status Filter") {
                    presenter.getFilterJobsList(status = mode.toString())
                } else if ((mode == "Status Filter")) {
                    presenter.getDeliveryJobsList()
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
        }
    }

    private fun loadDefaultSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusMode)
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_mode!!.adapter = adapterMode
        sp_mode.onItemSelectedListener = this
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
        /* rv_jobs_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
             override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                 super.onScrollStateChanged(recyclerView, newState)
                 if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                     isScrolling = true
                 }
             }

             override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                 super.onScrolled(recyclerView, dx, dy)
                 currentItems = manager.childCount
                 totalItems = manager.itemCount
                 scrollOutItems = manager.findFirstVisibleItemPosition()
                 if (isScrolling && (currentItems + scrollOutItems >= totalItems && scrollOutItems >= 0)) {
                     isScrolling = false
                     fetchData()

                 }
             }
         })*/
    }

    /* private fun fetchData() {
         progressbar.visibility = View.VISIBLE
         Handler().postDelayed(Runnable {
             for (i in 0..2) {
                 adapterJobsList.addList(jobsList)
                 adapterJobsList.notifyDataSetChanged()
                 progressbar.visibility = View.GONE
             }
         }, 2000)
     }*/

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
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

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.agent_menu, menu)
        val item = menu.findItem(R.id.action_agent)
        item.isVisible = CommonUtils.getRole() != ("ROLE_DeliveryPerson")
        //item.isEnabled = selected.isNotEmpty()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_agent -> {
                showAgentsList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
*/


}
