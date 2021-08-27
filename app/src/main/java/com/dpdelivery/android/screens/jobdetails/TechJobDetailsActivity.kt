package com.dpdelivery.android.screens.jobdetails

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
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
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.finish.FinishJobActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.scanner.ScannerActivity
import com.dpdelivery.android.screens.workflow.WorkFlowActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_tech_job_details.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_type_installation.*
import okhttp3.Headers
import org.json.JSONException
import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class TechJobDetailsActivity : TechBaseActivity(), TechJobDetailsContract.View,
    View.OnClickListener, IAdapterClickListener {

    lateinit var mContext: Context
    private var jobId: Int? = 0
    private var phone: String? = null
    private var altPhone: String? = null
    private var statusCode: String? = null
    private var noteList: ArrayList<Note?>? = null
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
    private var botId: String = ""
    private var connectivity: String = ""
    private var jobType: String? = null
    private var tech_phone: String? = null
    private var jobStartTime: String? = null
    private var cxLatLong: String = ""
    private var cxlat: String = ""
    private var cxLong: String = ""
    private var latitude: String = ""
    private var longitude: String = ""
    private var LOCATION_PERMISSION_REQUEST_CODE = 123
    private var diffInHours: Long = 0
    private var isActive: Boolean = false

    @Inject
    lateinit var detailsPresenter: TechJobDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_tech_job_details, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Job Details")
        showBack()
        setUpBottomNavView(true)
        if (intent != null) {
            val data = intent.getStringExtra(Constants.ID)
            jobId = Integer.parseInt(data!!)
        }
        getAssignedJob()
        error_button.setOnClickListener(this)
        empty_button.setOnClickListener(this)
        ivqrcodescan.setOnClickListener(this)
        tv_phone.setOnClickListener(this)
        dialog = CommonUtils.progressDialog(mContext)
        tv_alt_phone.setOnClickListener(this)
        btn_activate.setOnClickListener(this)
        btn_start_job.setOnClickListener(this)
        btn_finish_job.setOnClickListener(this)
        iv_refresh.setOnClickListener(this)
        tv_view_notes.setOnClickListener(this)
        btn_add_note.setOnClickListener(this)
        finish_job.setOnClickListener(this)
        btn_select.setOnClickListener(this)
        tv_address.setOnClickListener(this)
    }

    /**
     * Location
     */
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()!!
        )
    }

    override fun onStart() {
        super.onStart()
        when {
            CommonUtils.isAccessFineLocationGranted(this) -> {
                when {
                    CommonUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        CommonUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                CommonUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
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
        bottom_navigation.menu.getItem(0).isCheckable = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        CommonUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            CommonUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
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
            R.id.ivqrcodescan -> {
                IntentIntegrator(this).setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity::class.java)
                    .initiateScan()
            }
            R.id.tv_phone -> { //call function
                if (phone?.isNotEmpty()!!) {
                    val url = "tel:$phone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    /* dialog!!.show()
                     detailsPresenter.getVoipCall(caller = tech_phone!!, receiver = phone!!)*/
                }
            }
            R.id.tv_alt_phone -> {  // for call function(alt number)
                if (altPhone != null) {
                    val url = "tel:$altPhone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    /*  dialog!!.show()
                      detailsPresenter.getVoipCall(caller = tech_phone!!, receiver = altPhone!!)*/
                }
            }
            R.id.btn_activate -> {
                submitPid()
            }
            R.id.btn_start_job -> {
                if (btn_start_job.visibility == View.VISIBLE) {
                    if (CommonUtils.getRole() == "DeliveryPerson") {
                        startJob()
                    } else {
                        if (cxLatLong.isEmpty() || cxLatLong == "null" || cxLatLong == "0") {
                            startJob()
                        } else {
                            if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                                val loc1 = Location("")
                                loc1.latitude = latitude.toDouble()
                                loc1.longitude = longitude.toDouble()
                                val loc2 = Location("")
                                loc2.latitude = cxlat.toDouble()
                                loc2.longitude = cxLong.toDouble()

                                val distanceInMeters: Float = loc1.distanceTo(loc2)
                                if (distanceInMeters < 500) {
                                    startJob()
                                } else {
                                    toast("Please reach customer place before start job")
                                }
                            } else {
                                startJob()
                            }
                        }
                    }
                }
            }
            R.id.finish_job -> {
                val intent = Intent(this, FinishJobActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                intent.putExtra(Constants.DEVICE_CODE, deviceCode)
                intent.putExtra(Constants.BOT_ID, botId)
                intent.putExtra(Constants.CONNECTIVITY, connectivity)
                intent.putExtra(Constants.JOB_TYPE, jobType)
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
            R.id.btn_select -> {
                val intent = Intent(this, WorkFlowActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                intent.putExtra(Constants.DEVICE_CODE, deviceCode)
                intent.putExtra(Constants.BOT_ID, botId)
                intent.putExtra(Constants.CONNECTIVITY, connectivity)
                intent.putExtra(Constants.JOB_TYPE, jobType)
                intent.putParcelableArrayListExtra(Constants.NOTES, noteList)
                startActivity(intent)
            }
            R.id.tv_address -> {
                if (cxLatLong.isEmpty() || cxLatLong == "null" || cxLatLong == "0") {
                    toast("Location is not set")
                } else {
                    val URL =
                        "https://www.google.com/maps/dir/?api=1&travelmode=two-wheeler&zoom=12&destination=$cxLatLong"
                    val location = Uri.parse(URL)
                    val mapIntent = Intent(Intent.ACTION_VIEW, location)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }
    }

    private fun startJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        val currentTime = Date()
        val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        output.timeZone = TimeZone.getTimeZone("GMT")
        jobStartTime = output.format(currentTime)
        detailsPresenter.startJob(
            jobId!!,
            startJobIP = StartJobIP(jobStartTime = jobStartTime, status = "INP")
        )
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
                detailsPresenter.submitPid(
                    submitPidIp = SubmitPidIp(
                        deviceCode = et_purifierid.text.toString(),
                        jobId = jobId!!
                    )
                )
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
            if ((dialog!!.findViewById(R.id.et_note) as EditText).text!!.isNotEmpty()) {
                showViewState(MultiStateView.VIEW_STATE_LOADING)
                detailsPresenter.addNote(
                    jobId!!,
                    updateJobIp = UpdateJobIp(note = (dialog!!.findViewById(R.id.et_note) as EditText).text!!.toString())
                )
                dialog!!.dismiss()
            } else {
                toast("Note Should not be empty")
            }
        }
        dialog!!.show()
    }

    private fun finishJob() {
        if (et_purifierid!!.text.toString()
                .isNotEmpty() && tv_status!!.text.toString() == "ACTIVE"
        ) {
            isActive = true
            getAssignedJob()
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
        adapterNotesList =
            BasicAdapter(this, R.layout.tech_item_notes_list, adapterClickListener = this)
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
        if (isActive) {
            botId = res.bid + ""
            connectivity = res.connectivity + ""
            if (botId != "null") {
                val intent = Intent(this, FinishJobActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                intent.putExtra(Constants.DEVICE_CODE, et_purifierid.text.toString())
                intent.putExtra(Constants.BOT_ID, botId)
                intent.putExtra(Constants.CONNECTIVITY, connectivity)
                intent.putExtra(Constants.JOB_TYPE, jobType)
                startActivity(intent)
            } else {
                toast("Please wait BOT is Mapping to the Purifier")
            }
        }
        tv_job_id.text = res.id.toString()
        tv_job_type.text = res.type!!.description
        tv_name.text = res.customerName

        if (!res.customerPhone.isNullOrEmpty()) {
            try {
                //tv_phone.text = phone?.replaceRange(5..9, "*****")
                phone = res.customerPhone
                tv_phone.text = phone
            } catch (e: Exception) {

            }
        }
        if (!res.customerAltPhone.isNullOrEmpty()) {
            try {
                // tv_alt_phone.text = altPhone?.replaceRange(5..9, "*****")
                altPhone = res.customerAltPhone
                tv_alt_phone.text = altPhone
            } catch (e: Exception) {

            }
        } else {
            ll_alt_mobile.visibility = View.GONE
        }
        statusCode = res.status!!.code
        noteList = res.notes
        tech_phone = res.assignedTo?.phoneNumber
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

        if (res.status.code.equals("ASG")) {
            btn_start_job.visibility = View.VISIBLE
            ll_workflow.visibility = View.GONE
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

        //job assigned diff
        val startJobInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        startJobInput.timeZone = TimeZone.getTimeZone("GMT")
        var startJobDate: Date? = null
        try {
            startJobDate = startJobInput.parse(res.appointmentStartTime!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val jobstarttime: Long = (startJobDate!!.time) - Date().time
        diffInHours = jobstarttime / (60 * 60 * 1000) % 24
        deviceCode = res.installation?.deviceCode
        botId = res.bid + ""
        connectivity = res.connectivity + ""
        if (res.workflowId != null) {
            if (!res.status.code.equals("ASG")) {
                ll_workflow.visibility = View.VISIBLE
            }
            if (res.status.code.equals("ASG") || (res.status.code.equals("COM"))) {
                ll_workflow.visibility = View.GONE
            } else {
                ll_workflow.visibility = View.VISIBLE
            }
        } else {
            ll_workflow.visibility = View.GONE
            if (!res.type.code.equals("INS") && (res.status.code.equals("INP"))) {
                btn_start_job.visibility = View.GONE
                finish_job.visibility = View.VISIBLE
            } else if (res.type.code.equals("INS") && (res.status.code.equals("INP"))) {
                btn_start_job.visibility = View.GONE
                layout_ins.visibility = View.VISIBLE
            }
        }
        et_purifierid.setText(res.installation?.deviceCode)
        jobType = res.type.code
        cxLatLong = res.customerLatLong.toString()
        if (res.customerLatLong != null) {
            val st = StringTokenizer(cxLatLong, ",")
            val result = ArrayList<String>()
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().toString())
            }
            cxlat = result[0]
            cxLong = result[1]
        }
    }

    override fun showStartJobRes(startJobRes: StartJobRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (startJobRes.success!!) {
            toast("Job Started Successfully")
            init()
        } else {
            toast(startJobRes.error.toString())
        }
    }

    override fun showAddNoteRes(res: StartJobRes) {
        if (res.success!!) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            init()
            toast("Note Added Successfully")
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

    override fun showVoipRes(res: Headers) {
        dialog!!.dismiss()
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
                    /* showViewState(MultiStateView.VIEW_STATE_ERROR)
                     toast(throwable.message.toString())*/
                }
            }
        } else {
            /*showViewState(MultiStateView.VIEW_STATE_ERROR)
            toast(throwable.message.toString())*/
        }
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
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
