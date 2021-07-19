package com.dpdelivery.android.screens.techjobslist

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techinp.UpdateTokenIP
import com.dpdelivery.android.model.techres.*
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.jobdetails.TechJobDetailsActivity
import com.dpdelivery.android.screens.jobslist.JobsListActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.search.SearchActivity
import com.dpdelivery.android.utils.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_assigned_jobs_list.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import okhttp3.Headers
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

class TechJobsListActivity : TechBaseActivity(), TechJobsListContract.View, IAdapterClickListener,
    View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    lateinit var manager: LinearLayoutManager
    lateinit var adapterAsgJobsList: BasicAdapter
    lateinit var jobsList: ArrayList<Job?>
    private var filter: String? = null
    private var statusFilter: String? = null
    lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: TechJobsListPresenter

    //private val filterMode: Array<String> = arrayOf<String>("Status Filter", "Assigned", "In-Progress", "Completed", "Postponed", "Cancelled")
    private val filterMode: Array<String> =
        arrayOf<String>("Status Filter", "Assigned", "In-Progress", "Completed")
    private val statusFilterMode = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Assigned Jobs")
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
        adapterAsgJobsList =
            BasicAdapter(this, R.layout.item_asg_jobs_list, adapterClickListener = this)
        rv_asg_jobs_list.apply {
            presenter.getAssignedJobsList(
                status = "ASG",
                appointmentDate = DateHelper.getCurrentDate()
            )
            adapter = adapterAsgJobsList
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun getPartnerDetails() {
        presenter.getPartnerDetails()
    }

    override fun showPartnerDetails(res: PartnerDetailsRes) {
        CommonUtils.saveUserDetails(res)
        getDeviceToken()
    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = it.result
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("msg", msg)
            //updateDeviceToken(token)
        }
    }

    private fun updateDeviceToken(token: String?) {
        presenter.updateToken(
            updateTokenIP = UpdateTokenIP(
                deviceToken = token!!,
                userName = CommonUtils.getUserName()
            )
        )
    }

    override fun showUpdateTokenRes(res: CommonRes) {
        if (res.success) {
            Log.d("tag", res.message)
        }
    }

    private fun loadDefaultSpinner() {
        val adapterMode =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterMode)
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
        getPartnerDetails()
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
                    "Postponed" -> {
                        filter = "PPN"
                    }
                    "Cancelled" -> {
                        filter = "CAN"
                    }
                }
                if (filter != "Status Filter") {
                    startActivity(
                        Intent(this, JobsListActivity::class.java).putExtra(
                            "filter",
                            filter
                        )
                    )
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
                                val jobStatus = FinishJobIp(status = statusFilter)
                                presenter.addNote(
                                    any.id,
                                    updateJobIp = UpdateJobIp(note = note.text!!.toString())
                                )
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
            getAssignedJobsList()
        } else {
            toast(res.message)
            dialog.dismiss()
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
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
