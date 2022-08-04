package com.dpdelivery.android.screens.getnextjob

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.model.techres.GetNextJobRes
import com.dpdelivery.android.model.techres.PartnerDetailsRes
import com.dpdelivery.android.screens.account.AccountActivity
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_get_next_job.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import okhttp3.Headers
import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetNextJobActivity : TechBaseActivity(), GetNextJobsContract.View, View.OnClickListener {
    lateinit var mContext: Context
    lateinit var progressDialog: Dialog
    private var jobId: Int = 0
    private var tech_phone: String? = null
    private var appUpdateManager: AppUpdateManager? = null
    private var latitude: String = ""
    private var longitude: String = ""
    private var cxLatLong: String = ""
    private var cxlat: String = ""
    private var cxLong: String = ""
    private var LOCATION_PERMISSION_REQUEST_CODE = 123
    private var line1: String = ""
    private var line2: String = ""
    private var city: String = ""
    private var state: String = ""
    private var zipcode: String = ""

    @Inject
    lateinit var presenter: GetNextJobPresenter

    override fun onStart() {
        super.onStart()
        getPartnerDetails()
    }

    private fun getPartnerDetails() {
        presenter.getPartnerDetails()
    }

    override fun showPartnerDetails(res: PartnerDetailsRes) {
        CommonUtils.saveUserDetails(res)
        if (CommonUtils.getRole() == "Technician") {
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
        } else {
            startActivity(Intent(this, TechJobsListActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context)
            .inflate(R.layout.activity_get_next_job, tech_layout_container)
        init()
    }


    override fun init() {
        mContext = this
        setUpBottomNavView(false)
        progressDialog = CommonUtils.progressDialog(mContext)
        error_button.setOnClickListener(this)
        iv_logout.setOnClickListener(this)
        iv_logout.visibility = View.VISIBLE
        iv_account.visibility = View.VISIBLE
        iv_account.setOnClickListener(this)
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
                    /*if (locationResult.lastLocation != null) {
                        latitude = locationResult.lastLocation.latitude.toString()
                        longitude = locationResult.lastLocation.longitude.toString()
                    }*/
                    for (location in locationResult.locations) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                    if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                        CommonUtils.setLatitude(latitude)
                        CommonUtils.setLongitude(longitude)
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()!!
        )
        getNextJob()
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

    private fun getNextJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        presenter.getNextJob(latitude = CommonUtils.latitude, longitude = CommonUtils.longitude)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_update_status -> {
                progressDialog.show()
                presenter.saveJobResponse(
                    jobId,
                    response = "REJECT"
                )
            }
            R.id.btn_know_more -> {
                progressDialog.show()
                presenter.saveJobResponse(
                    jobId,
                    response = "ACCEPT"
                )
            }
            R.id.error_button -> {
                init()
                setUpLocationListener()
            }
            R.id.iv_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
            }
            R.id.iv_logout -> {
                logOut()
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

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        progressDialog.dismiss()
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
                    error_textView.text = throwable.message.toString()
                }
            }
        } else {
            progressDialog.dismiss()
            showViewState(MultiStateView.VIEW_STATE_ERROR)
            error_textView.text = throwable.message.toString()
        }
    }

    override fun showSaveJobResponse(res: GetNextJobRes) {
        if (res.success) {
            progressDialog.dismiss()
            toast(res.message)
            setUpLocationListener()
        } else {
            progressDialog.dismiss()
            toast(res.message)
            setUpLocationListener()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        currentAppVersionCheck()
        checkForAppUpdate()
    }

    private fun currentAppVersionCheck() {
        val packageManager = this.packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val currentVersion = packageInfo?.versionName
        // ForceUpdateAsync(currentVersion!!, this).execute()
        CommonUtils.setCurrentVersion(currentVersion!!)
    }

    override fun showNextJobRes(res: GetNextJobRes) {
        if (res.success) {
            if (res.body.status.description == "Assignment In Progress") {
                showViewState(MultiStateView.VIEW_STATE_CONTENT)
                btn_update_status.visibility = View.VISIBLE
                btn_update_status.text = "Reject"
                btn_know_more.text = "Accept"
                jobId = res.body.id
                tech_phone = res.body.assignedTo.phoneNumber
                tv_jobtypevalue.text = res.body.type.description
                tv_jobtypevalue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                tv_jobidvalue.text = res.body.id.toString()
                tv_namevalue.text = res.body.customerName
                tv_colorCodeValue.text = res.body.zipColorName
                val hexColor = res.body.zipColorCode
                if (!hexColor.isNullOrEmpty()) {
                    iv_color.visibility = View.VISIBLE
                    iv_color.setColorFilter(Color.parseColor(hexColor))
                }
                //address
                if (!res.body.customerAddress.line1.isNullOrEmpty()) {
                    line1 = res.body.customerAddress.line1
                }
                if (!res.body.customerAddress.line2.isNullOrEmpty()) {
                    line2 = ",${res.body.customerAddress.line2}"
                }
                if (!res.body.customerAddress.city.isNullOrEmpty()) {
                    city = ",${res.body.customerAddress.city}"
                }
                if (!res.body.customerAddress.state.isNullOrEmpty()) {
                    state = ",${res.body.customerAddress.state}"
                }
                if (!res.body.customerAddress.zip.isNullOrEmpty()) {
                    zipcode = ",${res.body.customerAddress.zip}"
                }

                val address = "$line1$line2$city$state$zipcode"
                tv_address.text = address

                if (!res.body.appointmentStartTime.isNullOrEmpty()) {
                    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                    val output = SimpleDateFormat("d-MMM-yyyy", Locale.ROOT)
                    val time = SimpleDateFormat("hha", Locale.ROOT)
                    input.timeZone = TimeZone.getTimeZone("IST")
                    var d: Date? = null
                    var d1: Date? = null
                    try {
                        d = input.parse(res.body.appointmentStartTime)
                        d1 = input.parse(res.body.appointmentEndTime!!)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val formatted = output.format(d!!)
                    val startTime = time.format(d)
                    val endTime = time.format(d1!!)
                    tv_appointmentdate.text = formatted
                    appointmenttimevalue.text = "$startTime to $endTime".toLowerCase(Locale.ROOT)
                } else {
                    tv_appointmentdate.text = res.body.appointmentStartTime
                }
                tv_statusvalue.text = res.body.status.description
                val text = res.body.customerPhone
                if (text.isNotEmpty()) {
                    try {
                        try {
                            tv_cust_phn.text = text.replaceRange(5..9, "*****")
                        } catch (e: Exception) {

                        }
                    } catch (e: Exception) {

                    }
                }
                tv_cust_phn.paintFlags = tv_cust_phn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                if (!res.body.customerPhone.isNullOrEmpty()) {
                    tv_cust_phn.setOnClickListener {
                        progressDialog.show()
                        presenter.getVoipCall(
                            caller = tech_phone!!,
                            receiver = res.body.customerPhone
                        )
                    }
                }

                if (res.body.customerAltPhone.isNullOrEmpty()) {
                    ll_alt_mobile.visibility = View.GONE
                } else {
                    val altPhone = res.body.customerAltPhone
                    tv_alternate_no.paintFlags =
                        tv_alternate_no.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    tv_alternate_no.visibility = View.VISIBLE
                    alternate_no.visibility = View.VISIBLE
                    try {
                        tv_alternate_no.text = (altPhone).replaceRange(5..9, "*****")
                    } catch (e: Exception) {

                    }
                    tv_alternate_no.setOnClickListener {
                        progressDialog.show()
                        presenter.getVoipCall(
                            caller = tech_phone!!,
                            receiver = res.body.customerAltPhone
                        )
                    }
                }
                btn_know_more.setOnClickListener(this)
                btn_update_status.setOnClickListener(this)
                cxLatLong = res.body.customerLatLong.toString()
                if (res.body.customerLatLong != null) {
                    val st = StringTokenizer(cxLatLong, ",")
                    val result = ArrayList<String>()
                    while (st.hasMoreTokens()) {
                        result.add(st.nextToken().toString())
                    }
                    cxlat = result[0]
                    cxLong = result[1]
                }

                tv_distance_value.text = res.body.distanceToBeTravelled
                tv_travel_duration_value.text = res.body.durationForTechnician
                if (res.body.projectedEarning > 0) {
                    ll_earning.visibility = View.VISIBLE
                    tv_earning_value.text = res.body.projectedEarning.toString()
                } else {
                    ll_earning.visibility = View.GONE
                }
            } else {
                startActivity(Intent(this, TechJobsListActivity::class.java))
            }
        } else {
            showViewState(MultiStateView.VIEW_STATE_ERROR)
            error_button.visibility = View.VISIBLE
            error_button.text = "Refresh"
            error_textView.text = res.message
        }
    }

    override fun showVoipRes(res: Headers) {
        progressDialog.dismiss()
        Toast.makeText(mContext, "Request sent, you will get the call back soon", Toast.LENGTH_LONG)
            .show()
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    //force update
    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfo = appUpdateManager!!.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                        appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request an immediate update.
                val pendingTransactionDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
                pendingTransactionDialog.setContentView(R.layout.dialog_alert_message)
                pendingTransactionDialog.setCancelable(false)
                pendingTransactionDialog.setCanceledOnTouchOutside(false)

                (pendingTransactionDialog.findViewById(R.id.btn_update) as AppCompatButton).setOnClickListener {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + context.packageName)
                        )
                    )
                    pendingTransactionDialog.dismiss()
                }
                (pendingTransactionDialog.findViewById(R.id.iv_close) as ImageView).visibility =
                    View.INVISIBLE
                pendingTransactionDialog.show()
            }
        }
    }
}