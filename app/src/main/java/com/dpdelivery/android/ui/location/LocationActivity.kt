package com.dpdelivery.android.ui.location

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.Validation
import com.dpdelivery.android.utils.toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback,
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    lateinit var mContext: Context
    lateinit var mapFragment: SupportMapFragment
    lateinit var resultText: AppCompatTextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationButton: View? = null
    private var REQUEST_LOCATION_CODE = 101
    private var center: LatLng? = null
    internal var mapAddress: Address? = null
    internal var mCurentAddress = ""
    private var pendingTransactionDialog: Dialog? = null
    private var mLocationRequest: LocationRequest? = null
    private var service: LocationManager? = null
    private var enabled: Boolean? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        mContext = this
        mapDataInitialise()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun mapDataInitialise() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        resultText = findViewById(R.id.tv_location)
        btn_use_location.setOnClickListener(this)

        locationButton = (findViewById<View>(Integer.parseInt("1")).parent as View).findViewById(Integer.parseInt("2"))
        val rlp = locationButton!!.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 50, 0, 0)

        service = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        enabled = service!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_use_location -> {
                if (mapAddress!!.getAddressLine(0).isNotEmpty()) {
                    val intent = Intent()
                    intent.putExtra("address", mapAddress!!.getAddressLine(0))
                    intent.putExtra("latitude", mapAddress!!.latitude)
                    intent.putExtra("longitude", mapAddress!!.longitude)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    toast("Please select location")
                }
            }
        }
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
        initCameraIdle()
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient!!.connect()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("Allow DP Delivery to\n" +
                                "access this device’s\n" +
                                "location?")
                        .setPositiveButton("OK") { dialog, which ->
                            ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    REQUEST_LOCATION_CODE
                            )
                        }
                        .create()
                        .show()
            } else ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun initCameraIdle() {
        mMap.setOnCameraIdleListener {
            center = mMap.cameraPosition.target
            getCurrentAddress(center!!.latitude, center!!.longitude).execute()
        }
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
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!, mLocationRequest!!, this)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13f))
                            val cameraPosition = CameraPosition.Builder()
                                    .target(LatLng(location.latitude, location.longitude)) // Sets the center of the map to location user
                                    .zoom(17f) // Sets the zoom
                                    .bearing(120f) // Sets the orientation of the camera to east
                                    .tilt(40f) // Sets the tilt of the camera to 30 degrees
                                    .build()
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }
                    }
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
        val latLng = LatLng(location!!.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
    }

    inner class getCurrentAddress(internal var Latitude: Double?, internal var Longitude: Double?) : AsyncTask<Void, Void, String>() {


        override fun onPreExecute() {
            super.onPreExecute()
            resultText.setText(R.string.please_wait)
        }

        override fun doInBackground(vararg voids: Void): String {
            mapAddress = CommonUtils.getCurrentLocationAddress(mContext, Latitude!!, Longitude!!)
            return if (Validation.isValidObject(mapAddress)) mapAddress!!.getAddressLine(0) else ""

        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            mCurentAddress = s
            if (Validation.isValidString(mCurentAddress)) {
                resultText.text = mCurentAddress
            } else {
                if (!(mContext as Activity).isFinishing) {
                    try {
                        resultText.setText(R.string.woops_something_went_wrong)
                        /*pendingTransactionDialog = Dialog(mContext, R.style.CustomDialogThemeLightBg)
                        pendingTransactionDialog!!.setCanceledOnTouchOutside(true)
                        pendingTransactionDialog!!.setContentView(R.layout.dialog_alert_message)
                        (pendingTransactionDialog!!.findViewById(R.id.dialog_title) as TextView).text = mContext.getString(R.string.unable_to_identify)
                        (pendingTransactionDialog!!.findViewById(R.id.dialog_text) as TextView).text = mContext.getString(R.string.please_try_again)
                        (pendingTransactionDialog!!.findViewById(R.id.tv_retry) as TextView).text = mContext.getString(R.string.retry).toUpperCase()
                        pendingTransactionDialog!!.show()

                        (pendingTransactionDialog!!.findViewById(R.id.tv_retry) as TextView).setOnClickListener {
                            mapDataInitialise()
                            pendingTransactionDialog!!.dismiss()

                        }*/
                    } catch (e: WindowManager.BadTokenException) {
                        Log.e("WindowManagerBad ", e.toString())
                    }
                }

            }
        }
    }
}


