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
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techinp.UpdateTokenIP
import com.dpdelivery.android.model.techres.*
import com.dpdelivery.android.screens.account.AccountActivity
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
    private var reasonsFilter: String? = null
    lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: TechJobsListPresenter

    private val filterMode: Array<String> = arrayOf<String>(
        "Assigned",
        "In-Progress",
        "Completed",
        "Postponed",
        "Cancelled"
    )
    private val statusFilterMode = ArrayList<String>()
    private var reasonsData = ArrayList<String>()
    private var spStatusFilter: Spinner? = null
    private var spReasonsFilter: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_assigned_jobs_list, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(true)
        loadDefaultSpinner()
        iv_search.setOnClickListener(this)
        iv_account.setOnClickListener(this)
        error_button.setOnClickListener(this)
        iv_logout.setOnClickListener(this)
        iv_search.visibility = View.VISIBLE
        iv_logout.visibility = View.VISIBLE
        iv_account.visibility = View.VISIBLE
        iv_account.setOnClickListener(this)
        ll_spinner.visibility = View.VISIBLE
        dialog = CommonUtils.progressDialog(context)
    }

    override fun onStart() {
        super.onStart()
        getPartnerDetails()
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
            updateDeviceToken(token)
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
            ArrayAdapter<String>(this, R.layout.spinner_item, filterMode)
        adapterMode.setDropDownViewResource(R.layout.spinner_item)
        sp_filter!!.adapter = adapterMode
        sp_filter.setSelection(0, false)
        sp_filter.onItemSelectedListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                getAssignedJobsList()
            }
            R.id.iv_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            R.id.iv_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
            }
            R.id.iv_logout -> {
                logOut()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        bottom_navigation.selectedItemId = R.id.action_jobs
        loadDefaultSpinner()
        forceUpdate()
        getAssignedJobsList()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_filter -> {
                filter = sp_filter!!.selectedItem.toString()
                when (filter) {
                    "In-Progress" -> {
                        filter = "INP"
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
                if (filter != "Assigned") {
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
            empty_button.visibility = View.GONE
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
                401 -> {
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
            showViewState(MultiStateView.VIEW_STATE_ERROR)
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
                    spStatusFilter =
                        (statusDialog.findViewById(R.id.sp_status_filter) as Spinner)
                    spReasonsFilter =
                        (statusDialog.findViewById(R.id.sp_reasons_filter) as Spinner)
                    (statusDialog.findViewById(R.id.tv_job_id) as TextView).text =
                        any.id!!.toString()

                    getStatuses(statusFilterData)

                    if (!(context as Activity).isFinishing) {
                        statusDialog.show()
                    }
                    (statusDialog.findViewById(R.id.btn_submit) as AppCompatButton).setOnClickListener {
                        if (statusFilter != "Select Status") {
                            dialog.show()
                            val jobStatus = FinishJobIp(
                                status = statusFilter,
                                note = reasonsFilter
                            )
                            presenter.updateJob(any.id, jobStatus)
                            statusDialog.dismiss()
                        } else {
                            toast("Please Select Status")
                        }
                    }
                }
            }
        }
    }

    private fun getStatuses(statusFilterData: ArrayList<AgentJobStatuses?>) {
        val adapterStatusMode : ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item,
            statusFilterMode as List<String?>
        ){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color grey
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        adapterStatusMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStatusFilter!!.adapter = adapterStatusMode
        spStatusFilter!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    statusFilter = spStatusFilter!!.selectedItem.toString()
                    if (statusFilter != "Select Status") {
                        for (statusCodes in statusFilterData) {
                            if (statusCodes!!.description == statusFilter) {
                                statusFilter = statusCodes.code
                                reasonsData = statusCodes.reasons!!
                                getReasonsData(reasonsData)
                                break
                            }
                        }
                    }
                }
            }
    }

    private fun getReasonsData(reasonsData: ArrayList<String>) {
        val adapterReasonsMode = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            reasonsData
        )
        adapterReasonsMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spReasonsFilter!!.adapter = adapterReasonsMode
        spReasonsFilter!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    reasonsFilter = spReasonsFilter!!.selectedItem.toString()
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
