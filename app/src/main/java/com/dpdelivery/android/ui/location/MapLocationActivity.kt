package com.dpdelivery.android.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.utils.*
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.SEARCHED_ADDRESS
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.SEARCHED_AREA
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.SEARCHED_CITY
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.SEARCHED_LAT
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.SEARCHED_LONG
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.getlat
import com.dpdelivery.android.utils.SharedPreferenceManager.Companion.getlng
import com.dpdelivery.android.utils.Validation.Companion.isValidObject
import com.dpdelivery.android.utils.Validation.Companion.isValidString
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_location.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class MapLocationActivity : DaggerAppCompatActivity(), View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private lateinit var mMap: GoogleMap
    private var mLocationRequest: LocationRequest? = null
    private var service: LocationManager? = null
    private var enabled: Boolean? = null
    var locationButton: View? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var resultText: AppCompatTextView
    private var center: LatLng? = null
    internal var mCurentAddress = ""
    internal var mapAddress: Address? = null
    lateinit var mContext: Context
    @Inject
    lateinit var appPreferences: SharedPreferenceManager
    var mLocationPermissionGranted: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null
    private val LOCATION_INTERVAL = 60000
    private val LOCATION_FAST_INTERVAL = 30000
    private val LOCATION_DISTANCE = 0f
    open var subscription = CompositeDisposable()
    lateinit var rxPermissions: RxPermissions
    private var REQUEST_CHECK_SETTINGS: Int = 0x1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        mContext = this
        init()
        mapDataInitialise()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun init() {
        resultText = findViewById(R.id.tv_location)
        appPreferences.put(SEARCHED_ADDRESS, "")
        appPreferences.put(SEARCHED_LAT, "")
        appPreferences.put(SEARCHED_LONG, "")
        appPreferences.put(SEARCHED_AREA, "")
        appPreferences.put(SEARCHED_CITY, "")
    }

    private fun mapDataInitialise() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btn_use_location.setOnClickListener(this)

        locationButton = (findViewById<View>(Integer.parseInt("1")).parent as View).findViewById(Integer.parseInt("2"))
        val rlp = locationButton!!.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 1800, 0, 0)

        service = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        enabled = service!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true

        locationButton = (findViewById<View>(Integer.parseInt("1")).parent as View).findViewById(Integer.parseInt("2"))
        fab.setOnClickListener {
            if (mMap != null) {
                if (locationButton != null)
                    locationButton!!.callOnClick()
            }
        }
        with(mMap) {
            uiSettings.isZoomControlsEnabled = true
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            } else {
                checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }

        val latLng = LatLng(getlat().toDouble(), getlng().toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        initCameraIdle()
    }

    private fun initCameraIdle() {
        mMap.setOnCameraIdleListener {
            center = mMap.cameraPosition.target
            getCurrentAddress(center!!.latitude, center!!.longitude).execute()
        }
    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (!enabled!!) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!, mLocationRequest!!, this)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            val latLng = LatLng(location.latitude, location.longitude)

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
                        }
                    }
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(p0: Location?) {

    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
                //logd("onLocationResult location: " + locationResult.lastLocation)
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        buildLocationSettingsRequest()
        mLocationRequest?.apply {
            interval = LOCATION_INTERVAL.toLong()
            fastestInterval = LOCATION_FAST_INTERVAL.toLong()
            smallestDisplacement = LOCATION_DISTANCE
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder: LocationSettingsRequest.Builder =
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        val result: Task<LocationSettingsResponse> =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
            } catch (exp: ApiException) {
                loge("buildLocationSettingsRequest:$exp.message?")
                when (exp.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable: ResolvableApiException = exp as ResolvableApiException
                            resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                        } catch (exp: Exception) {
                            loge("LocationSettingsStatusCodes:$exp.message?")
                        }
                    }
                }
            }
        }
    }

    fun checkLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        rxPermissions = RxPermissions(this)
        subscription.add(rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe {
                    if (it) {
                        mLocationPermissionGranted = true
                        createLocationCallback()
                        createLocationRequest()
                        getAddress()
                    } else
                        toast(getString(R.string.please_provide_location_permission))
                })
    }

    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient.lastLocation?.addOnSuccessListener(this, OnSuccessListener { location ->
            onLocationChanged(location)
            if (location == null) {
                logv("onSuccess:null")
                return@OnSuccessListener
            }
        })?.addOnFailureListener(this) { e -> logv("getLastLocation:onFailure:$e") }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_use_location -> {
                appPreferences.put(SEARCHED_ADDRESS, "1")
                appPreferences.put(SEARCHED_LAT, mapAddress!!.latitude.toString())
                appPreferences.put(SEARCHED_LONG, mapAddress!!.longitude.toString())
                appPreferences.put(SEARCHED_AREA, resultText.text.toString())
                appPreferences.put(SEARCHED_CITY, mapAddress!!.subAdminArea)
                //onBackPressed()
                intent.putExtra("address", mapAddress!!.getAddressLine(0))
                intent.putExtra("latitude", mapAddress!!.latitude)
                intent.putExtra("longitude", mapAddress!!.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class getCurrentAddress(internal var Latitude: Double?, internal var Longitude: Double?) :
            AsyncTask<Void, Void, String>() {


        override fun onPreExecute() {
            super.onPreExecute()
            resultText.setText(R.string.please_wait)
        }

        override fun doInBackground(vararg voids: Void): String {
            mapAddress = CommonUtils.getCurrentLocationAddress(mContext, Latitude!!, Longitude!!)
            return if (isValidObject(mapAddress)) mapAddress!!.getAddressLine(0) else ""
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            mCurentAddress = s
            if (isValidString(mCurentAddress)) {
                resultText.text = mCurentAddress
            } else {
                if (!(mContext as Activity).isFinishing) {
                    try {
                        resultText.setText(R.string.woops_something_went_wrong)

                    } catch (e: WindowManager.BadTokenException) {
                        Log.e("WindowManagerBad ", e.toString())
                    }
                }

            }
        }
    }
}
