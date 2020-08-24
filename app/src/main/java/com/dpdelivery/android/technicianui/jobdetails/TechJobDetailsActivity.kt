package com.dpdelivery.android.technicianui.jobdetails

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techinp.StartJobIP
import com.dpdelivery.android.model.techinp.SubmitPidIp
import com.dpdelivery.android.model.techinp.UpdateJobIp
import com.dpdelivery.android.model.techres.*
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.finish.FinishJobActivity
import com.dpdelivery.android.technicianui.scanner.ScannerActivity
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_assigned_job.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_type_installation.*
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class TechJobDetailsActivity : TechBaseActivity(), TechJobDetailsContract.View, View.OnClickListener, IAdapterClickListener {

    lateinit var mContext: Context
    private var jobId: Int? = 0
    private var phone: String? = null
    private var altPhone: String? = null
    private var statusCode: String? = null
    private var noteList: ArrayList<TechNote?>? = null
    private var dialog: Dialog? = null
    lateinit var manager: LinearLayoutManager
    lateinit var adapterNotesList: BasicAdapter
    private var deviceCode: String? = null
    private var isSuccess: Boolean = false
    private var line1: String = ""
    private var line2: String = ""
    private var city: String = ""
    private var state: String = ""
    private var zipcode: String = ""

    @Inject
    lateinit var detailsPresenter: TechJobDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_assigned_job, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Job Details")
        showBack()
        setUpBottomNavView(false)
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
        }
        getAssignedJob()
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        ivqrcodescan.setOnClickListener(this)
        iv_call.setOnClickListener(this)
        iv_alt_call.setOnClickListener(this)
        btn_activate.setOnClickListener(this)
        btn_start_job.setOnClickListener(this)
        btn_finish_job.setOnClickListener(this)
        iv_refresh.setOnClickListener(this)
        tv_view_notes.setOnClickListener(this)
        btn_add_note.setOnClickListener(this)
        finish_job.setOnClickListener(this)
    }

    private fun getAssignedJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        Handler().postDelayed(Runnable {
            if (jobId != 0) {
                detailsPresenter.getAssignedJob(jobId!!)
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        detailsPresenter.takeView(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                init()
            }
            R.id.empty_button -> {
                init()
            }
            R.id.ivqrcodescan -> {
                IntentIntegrator(this).setCaptureActivity(ScannerActivity::class.java).initiateScan()
            }
            R.id.iv_call -> { //call function
                if (phone!!.isNotEmpty()) {
                    val url = "tel:$phone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                }
            }
            R.id.iv_alt_call -> {  // for call function(alt number)
                if (altPhone!!.isNotEmpty()) {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                }
            }
            R.id.btn_activate -> {
                submitPid()
            }
            R.id.btn_start_job -> {
                startJob()
            }
            R.id.finish_job -> {
                val intent = Intent(this, FinishJobActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                intent.putExtra(Constants.DEVICE_CODE, deviceCode)
                startActivity(intent)
            }
            R.id.btn_finish_job -> {
                finishJob()
            }
            R.id.iv_refresh -> {
                when {
                    et_purifierid!!.text.toString().trim { it <= ' ' }.isEmpty() -> {
                        toast("Please submit Purifier Id")
                    }
                    et_purifierid!!.text!!.length != 10 -> {
                        toast("Invalid Purifier Id")
                        iv_refresh!!.isEnabled = false
                    }
                    else -> {
                        val purifierId = et_purifierid.text.toString()
                        detailsPresenter.refreshPidStatus(purifierId)
                    }
                }
            }
            R.id.tv_view_notes -> {
                if (noteList!!.isNotEmpty()) {
                    showNotesList()   // for showing notes list
                } else {
                    toast("No Notes Found")
                }
            }
            R.id.btn_add_note -> {
                addNote()
            }
        }
    }

    private fun startJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        val currentTime = Date()
        val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        output.timeZone = TimeZone.getTimeZone("GMT")
        val jobStartTime = output.format(currentTime)
        detailsPresenter.startJob(jobId!!, startJobIP = StartJobIP(jobStartTime = jobStartTime, status = ""))
    }

    private fun submitPid() {
        when {
            et_purifierid!!.text.toString().trim { it <= ' ' }.isEmpty() -> {
                toast("Purifier Id is required")
            }
            et_purifierid!!.text!!.length != 10 -> {
                toast("Invalid Purifier Id")
                iv_refresh!!.isEnabled = false
            }
            else -> {
                detailsPresenter.submitPid(submitPidIp = SubmitPidIp(deviceCode = et_purifierid.text.toString(), jobId = jobId!!))
            }
        }
    }

    private fun addNote() {
        dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        dialog!!.setContentView(R.layout.layout_add_note)
        dialog!!.setCancelable(true)
        (dialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            dialog!!.dismiss()
        }
        (dialog!!.findViewById(R.id.btn_submit) as AppCompatButton).setOnClickListener {
            showViewState(MultiStateView.VIEW_STATE_LOADING)
            if ((dialog!!.findViewById(R.id.et_note) as EditText).text!!.isNotEmpty()) {
                detailsPresenter.addNote(jobId!!, updateJobIp = UpdateJobIp(note = (dialog!!.findViewById(R.id.et_note) as EditText).text!!.toString()))
                dialog!!.dismiss()
            } else {
                toast("Note Should not be empty")
            }
        }
        dialog!!.show()
    }

    private fun finishJob() {
        if (et_purifierid!!.text.toString().isNotEmpty() && tv_status!!.text.toString() == "ACTIVE") {
            val intent = Intent(this, FinishJobActivity::class.java)
            intent.putExtra(Constants.ID, jobId)
            intent.putExtra(Constants.DEVICE_CODE, et_purifierid.text.toString())
            startActivity(intent)
        } else {
            toast("Please verify fields before finish job.")
        }
    }

    private fun showNotesList() {
        dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        dialog!!.setContentView(R.layout.layout_note_list)
        dialog!!.setCancelable(true)
        (dialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            dialog!!.dismiss()
        }
        manager = LinearLayoutManager(this)
        (dialog!!.findViewById(R.id.rv_notes_list) as RecyclerView).layoutManager = manager
        adapterNotesList = BasicAdapter(this, R.layout.tech_item_notes_list, adapterClickListener = this)
        (dialog!!.findViewById(R.id.rv_notes_list) as RecyclerView).apply {
            adapter = adapterNotesList
            noteList.withNotNullNorEmpty {
                adapterNotesList.addList(noteList!!)
            }
        }
        dialog!!.show()
    }

    override fun showAssignedJobRes(res: Job) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        tv_job_id.text = res.id.toString()
        tv_job_type.text = res.type!!.description
        tv_name.text = res.customerName
        phone = res.customerPhone
        tv_phone.text = phone
        altPhone = res.customerAltPhone
        tv_alt_phone.text = altPhone
        statusCode = res.status!!.code
        noteList = res.notes
        tv_view_notes.visibility = View.VISIBLE

        //address
        if (!res.customerAddress?.line1.isNullOrEmpty()) {
            line1 = res.customerAddress?.line1.toString()
        }
        if (!res.customerAddress?.line2.isNullOrEmpty()) {
            line2 = ",${res.customerAddress?.line2.toString()}"
        }
        if (!res.customerAddress?.city.isNullOrEmpty()) {
            city = ",${res.customerAddress?.city}"
        }
        if (!res.customerAddress?.state.isNullOrEmpty()) {
            state = ",${res.customerAddress?.state}"
        }
        if (!res.customerAddress?.zip.isNullOrEmpty()) {
            zipcode = ",${res.customerAddress?.zip}"
        }

        val address = "$line1$line2$city$state$zipcode"
        tv_address.text = address
        tv_job_desc.text = res.description

        if (res.type.code.equals("ISU") && (res.status.code.equals("INP"))) {
            btn_start_job.visibility = View.GONE
            finish_job.visibility = View.VISIBLE
        } else if (res.type.code.equals("INS") && (res.status.code.equals("INP"))) {
            btn_start_job.visibility = View.GONE
            layout_ins.visibility = View.VISIBLE
        } else if (res.type.code.equals("ISU") && (res.status.code.equals("ASG"))) {
            btn_start_job.visibility = View.VISIBLE
        } else if (res.type.code.equals("INS") && (res.status.code.equals("ASG"))) {
            btn_start_job.visibility = View.VISIBLE
        } else {
            btn_start_job.visibility = View.GONE
        }

        if (!res.appointmentStartTime.isNullOrEmpty() || !res.appointmentEndTime.isNullOrEmpty()) {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
            val output = SimpleDateFormat("EEE, d-MMM-yyyy hh:mm:ss a", Locale.ROOT)
            input.timeZone = TimeZone.getTimeZone("IST")
            var d: Date? = null
            var d1: Date? = null
            try {
                d = input.parse(res.appointmentStartTime!!)
                d1 = input.parse(res.appointmentEndTime!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val formattedStartTime = output.format(d!!)
            val formattedEndTime = output.format(d1!!)
            tv_appt_start.text = formattedStartTime
            tv_appt_end.text = formattedEndTime
        } else {
            tv_appt_start.text = res.appointmentStartTime
            tv_appt_end.text = res.appointmentEndTime
        }
        deviceCode = res.installation?.deviceCode
        et_purifierid.setText(res.installation?.deviceCode)
    }

    override fun showStartJobRes(startJobRes: StartJobRes) {
        if (startJobRes.success!!) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            toast("Job Started Successfully")
            init()
        } else {
            toast(startJobRes.error.toString())
        }
    }

    override fun showAddNoteRes(res: StartJobRes) {
        if (res.success!!) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            toast("Note Added Successfully")
            init()
        }
    }

    override fun showSubmittedPidRes(submiPidRes: SubmiPidRes) {
        if (submiPidRes.success) {
            try {
                btn_activate!!.isEnabled = false
                et_purifierid!!.isEnabled = false
                iv_refresh!!.isEnabled = true
                toast(submiPidRes.message)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (!submiPidRes.success) {
            toast(submiPidRes.message)
            isSuccess = submiPidRes.success
            iv_refresh!!.isEnabled = false
        }
    }

    override fun showRefreshPidRes(res: PIdStatusRes) {
        try {
            tv_status!!.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            tv_status!!.text = res.description
            tv_status!!.setTextColor(Color.RED)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //We will get scan results here
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //check for null
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val regex = "^[a-zA-Z0-9]+$"
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(result.contents)
                if (matcher.matches()) {
                    et_purifierid.setText(result.contents)
                } else
                    toast("Purifier ID Is Not Valid")
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
