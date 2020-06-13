package com.dpdelivery.android.technicianui.finish

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techres.SparePartsData
import com.dpdelivery.android.model.techres.SubmiPidRes
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.photo.ImageActivity
import com.dpdelivery.android.technicianui.techjobslist.TechJobsListActivity
import com.dpdelivery.android.ui.location.MapLocationActivity
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_finish_job.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class FinishJobActivity : TechBaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener, FinishJobContract.View {

    lateinit var mContext: Context
    private var address: String? = null
    private val LOCATION_REQUEST_CODE = 6
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var isLocationSet: Boolean = false
    private var isTDSSet: Boolean = false
    private var isPhotoSet: Boolean = false
    private var isOTPSet: Boolean = false
    private val PHOTO_REQUEST_CODE = 1
    private var jobId: Int = 0
    private var deviceCode: String? = null
    private var inputTds: Int = 0
    private var outputTds: Int = 0
    private var jobEndTime: String? = null
    lateinit var dialog: Dialog
    private var sparePartsIdList: List<String>? = null
    private var partsIdList: ArrayList<Int>? = null

    @Inject
    lateinit var presenter: FinishJobPresenter
    private val TAG = FinishJobActivity::class.java.simpleName
    private var mode: String? = null
    private val paymentMode: Array<String> = arrayOf<String>("Payment Type", "Cash", "Card", "PayTM", "InstaMojo", "EazyPay-Paytm", "EazyPay-GPay", "EazyPay-PhonePay", "BankTransfer", "Cheque", "Other")
    lateinit var partList: ArrayList<SparePartsData>
    private var amountCollected: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(baseContext).inflate(R.layout.activity_finish_job, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Finish Job")
        showBack()
        setUpBottomNavView(false)
        btn_location.setOnClickListener(this)
        btn_happy_code.setOnClickListener(this)
        btn_photo.setOnClickListener(this)
        btn_add_parts.setOnClickListener(this)
        btn_tds.setOnClickListener(this)
        error_button.setOnClickListener(this)
        btn_finish_job.setOnClickListener(this)
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
            deviceCode = intent.getStringExtra(Constants.DEVICE_CODE)
        }
        et_amount!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                try {
                    val amount = et_amount!!.text.toString()
                    val intAmount = Integer.valueOf(amount)
                    if (intAmount <= 0) {
                        type.visibility = View.GONE
                        sp_mode.visibility = View.GONE
                    } else {
                        type.visibility = View.VISIBLE
                        sp_mode.visibility = View.VISIBLE
                    }
                } catch (e: NumberFormatException) {
                    //loge("Not a number")
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                type.visibility = View.GONE
                sp_mode.visibility = View.GONE
            }
        })
        loadDefaultSpinner()
        dialog = progressDialog(mContext)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        fetchItemsFromSharedPref()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_location -> {
                val intent = Intent(context, MapLocationActivity::class.java)
                startActivityForResult(intent, LOCATION_REQUEST_CODE)
            }
            R.id.btn_happy_code -> {
                showOtp()
            }
            R.id.btn_photo -> {
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra(Constants.ID, jobId)
                startActivityForResult(intent, PHOTO_REQUEST_CODE)
            }
            R.id.btn_add_parts -> {
                getSparePartsList()
            }
            R.id.btn_tds -> {
                showTds()
            }
            R.id.error_button -> {
                init()
            }
            R.id.btn_finish_job -> {
                finishJob()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            address = data!!.getStringExtra("address")
            latitude = data.getDoubleExtra("latitude", 0.00)
            longitude = data.getDoubleExtra("longitude", 0.00)
            Log.i(TAG, "LAT:$latitude .LONG$longitude")
            locationtxt!!.text = address
            locationtxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            isLocationSet = true
            val sharedPreferenceslocation = getSharedPreferences("location$jobId", Context.MODE_PRIVATE)
            val editorlocation = sharedPreferenceslocation.edit()
            editorlocation.putString("location_txt", address)
            editorlocation.putBoolean("location_type", isLocationSet)
            editorlocation.putString("latitude", latitude.toString())
            editorlocation.putString("longitude", longitude.toString())
            editorlocation.apply()
        } else if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            phototxt.text = getString(R.string.uploaded_photo)
            phototxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            isPhotoSet = true
            val sharedPreferencesPhoto = getSharedPreferences("photo$jobId", Context.MODE_PRIVATE)
            val editorPhoto = sharedPreferencesPhoto.edit()
            editorPhoto.putBoolean("upload_type", isPhotoSet)
            editorPhoto.apply()
        }
    }

    private fun loadDefaultSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentMode)
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_mode!!.adapter = adapterMode
        sp_mode.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_mode -> {
                mode = sp_mode!!.selectedItem.toString()
                when (mode) {
                    "Cash" -> {
                        mode = "CSH"
                    }
                    "Card" -> {
                        mode = "CRD"
                    }
                    "PayTM" -> {
                        mode = "PTM"
                    }
                    "InstaMojo" -> {
                        mode = "INM"
                    }
                    "EazyPay-Paytm" -> {
                        mode = "EPT"
                    }
                    "EazyPay-GPay" -> {
                        mode = "EGP"
                    }
                    "EazyPay-PhonePay" -> {
                        mode = "EPP"
                    }
                    "BankTransfer" -> {
                        mode = "NBK"
                    }
                    "Cheque" -> {
                        mode = "CHQ"
                    }
                    "Other" -> {
                        mode = "OTH"
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun showOtp() {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.layout_otp, null)
        val otp = mView.findViewById<AppCompatEditText>(R.id.output_otp)
        val submitBtn = mView.findViewById<AppCompatButton>(R.id.submitotpbutton)

        mBuilder.setView(mView)
        val dialog = mBuilder.create()
        dialog.show()
        submitBtn.setOnClickListener {
            if (otp.text.toString().isNotEmpty()) {
                val ot = otp.text.toString()
                otptxt!!.text = ot
                isOTPSet = true
                val sharedPreferenceshappycode = getSharedPreferences("happycode$jobId", Context.MODE_PRIVATE)
                val editorhappycode = sharedPreferenceshappycode.edit()
                editorhappycode.putBoolean("otp_type", isOTPSet)
                editorhappycode.putString("otp", ot)
                editorhappycode.apply()
                otptxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                dialog.dismiss()
            } else {
                otptxt!!.text = "Not Set"
            }
        }
    }

    private fun showTds() {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.layout_tds, null)
        val inputTDS = mView.findViewById<EditText>(R.id.input_tds)
        val outputTDS = mView.findViewById<EditText>(R.id.output_tds)
        val submitBtn = mView.findViewById<Button>(R.id.submittdsbutton)

        mBuilder.setView(mView)
        val dialog = mBuilder.create()
        dialog.show()

        submitBtn.setOnClickListener {
            if (inputTDS.text.toString().isNotEmpty() && outputTDS.text.toString().isNotEmpty()) {
                tdsTxt!!.text = "Input TDS :" + inputTDS.text.toString() + " Output TDS :" + outputTDS.text.toString()
                inputTds = Integer.parseInt(inputTDS.text.toString())
                outputTds = Integer.parseInt(outputTDS.text.toString())
                isTDSSet = true

                val sharedPreferencestds = getSharedPreferences("tds$jobId", Context.MODE_PRIVATE)
                val editortds = sharedPreferencestds.edit()
                editortds.putBoolean("tds_type", isTDSSet)
                editortds.putInt("input_tds", inputTds)
                editortds.putInt("output_tds", outputTds)
                editortds.apply()
                tdsTxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                dialog.dismiss()
            }
        }
    }

    private fun getSparePartsList() {
        dialog.show()
        presenter.getSparePartsList()
    }

    private fun addParts(jobId: Int, partsList: ArrayList<SparePartsData>) {
        val newFragment = SparePartsFragment.newInstance(jobId, partsList)
        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        newFragment.show(supportFragmentManager, "dialog")
    }

    override fun showSparePartsRes(res: ArrayList<SparePartsData>) {
        dialog.dismiss()
        if (res.isNotEmpty()) {
            partList = res
            if (partList.isNotEmpty()) {
                addParts(jobId, partList)
            } else {
                toast("No Spares Found")
            }
        }
    }

    private fun finishJob() {
        val currentTime = Date()
        val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        output.timeZone = TimeZone.getTimeZone("GMT")
        jobEndTime = output.format(currentTime)

        val sharedPreferenceslocation = getSharedPreferences("location$jobId", Context.MODE_PRIVATE)
        val latitude = sharedPreferenceslocation.getString("latitude", "")
        val longitude = sharedPreferenceslocation.getString("longitude", "")

        val sharedPreferencestds = getSharedPreferences("tds$jobId", Context.MODE_PRIVATE)
        inputTds = sharedPreferencestds.getInt("input_tds", 0)
        outputTds = sharedPreferencestds.getInt("output_tds", 0)

        val amount = et_amount.text.toString()
        amountCollected = amount.toFloat()

        if (isLocationSet && isPhotoSet && isTDSSet && isOTPSet && amountCollected == 0f) {
            showViewState(MultiStateView.VIEW_STATE_LOADING)
            val finishJobIp = FinishJobIp(status = "COM",
                    latitude = latitude.toString(),
                    longitude = longitude.toString(),
                    inputTds = inputTds.toString(),
                    outputTds = outputTds.toString(),
                    happyCode = otptxt.text.toString(),
                    amountCollected = amountCollected,
                    deviceCode = deviceCode!!,
                    jobEndTime = jobEndTime.toString(),
                    paymentType = "",
                    spares = partsIdList!!)
            presenter.finishJob(jobId, finishJobIp)
        } else if (isLocationSet && isPhotoSet && isTDSSet && isOTPSet && mode != "Payment Type" && amountCollected > 0f) {
            showViewState(MultiStateView.VIEW_STATE_LOADING)
            val finishJobIp = FinishJobIp(status = "COM",
                    latitude = latitude.toString(),
                    longitude = longitude.toString(),
                    inputTds = inputTds.toString(),
                    outputTds = outputTds.toString(),
                    happyCode = otptxt.text.toString(),
                    amountCollected = amountCollected,
                    paymentType = mode.toString(),
                    deviceCode = deviceCode!!,
                    jobEndTime = jobEndTime.toString(),
                    spares = partsIdList!!)
            presenter.finishJob(jobId, finishJobIp)
        } else {
            toast("Please check all the fields before submitting")
        }
    }

    override fun showFinishJobRes(res: SubmiPidRes) {
        if (res.success) {
            clearPreferences()
            startActivity(Intent(this, TechJobsListActivity::class.java))
            finish()
        } else {
            toast(res.message)
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message ?: getString(R.string.error_something_wrong))
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    private fun progressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val loadingText = inflate.findViewById<TextView>(R.id.loading_text)
        loadingText.text = "Loading,Please Wait.."
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun fetchItemsFromSharedPref() {
        val sharedPreferences = getSharedPreferences("DrinkPrimeParts_$jobId.txt", Context.MODE_PRIVATE)
        val fetchedItemsName = sharedPreferences.getString("itemsName", "")
        val s = sharedPreferences.getString("key", "")
        val spareParts = fetchedItemsName!!.replace("[", "")
        val spareParts1 = spareParts.replace("]", "")
        val st = StringTokenizer(s, ",")
        val result = ArrayList<Int>()
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().toInt())
        }
        partsIdList = result
        if (fetchedItemsName.isNotEmpty()) {
            add_parts_txt!!.text = spareParts1
            add_parts_txt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        } else {
            add_parts_txt!!.text = "No Parts Are Selected"
            add_parts_txt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }

        val sharedPreferenceshappycode = getSharedPreferences("happycode$jobId", Context.MODE_PRIVATE)
        isOTPSet = sharedPreferenceshappycode.getBoolean("otp_type", false)
        val otp = sharedPreferenceshappycode.getString("otp", "")
        if (isOTPSet) {
            otptxt!!.text = otp
            otptxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        } else {
            otptxt!!.text = "Not Set"
            otptxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }

        val sharedPreferencesphoto = getSharedPreferences("photo$jobId", Context.MODE_PRIVATE)
        isPhotoSet = sharedPreferencesphoto.getBoolean("upload_type", false)
        if (isPhotoSet) {
            phototxt.text = getString(R.string.uploaded_photo)
            phototxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        } else {
            phototxt!!.text = "Not Set"
            phototxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }

        val sharedPreferenceslocation = getSharedPreferences("location$jobId", Context.MODE_PRIVATE)
        isLocationSet = sharedPreferenceslocation.getBoolean("location_type", false)
        if (isLocationSet) {
            val data = sharedPreferenceslocation.getString("location_txt", "")
            locationtxt!!.text = data
            locationtxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        } else {
            locationtxt!!.text = "Not Set"
            locationtxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }

        val sharedPreferencestds = getSharedPreferences("tds$jobId", Context.MODE_PRIVATE)
        isTDSSet = sharedPreferencestds.getBoolean("tds_type", false)
        if (isTDSSet) {
            val inputtds = sharedPreferencestds.getInt("input_tds", 0)
            val outputtds = sharedPreferencestds.getInt("output_tds", 0)
            tdsTxt!!.text = "Input TDS :$inputtds Output TDS :$outputtds"
            tdsTxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        } else {
            tdsTxt!!.text = "Not Set"
            tdsTxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
    }

    private fun clearPreferences() {
        val sharedPreferences = getSharedPreferences("DrinkPrimeParts_$jobId.txt", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val sharedPreferencesHappyCode = getSharedPreferences("happycode$jobId", Context.MODE_PRIVATE)
        val happyCodeEditor = sharedPreferencesHappyCode.edit()
        happyCodeEditor.clear()
        happyCodeEditor.apply()

        val sharedPreferencesPhoto = getSharedPreferences("photo$jobId", Context.MODE_PRIVATE)
        val photoEditor = sharedPreferencesPhoto.edit()
        photoEditor.clear()
        photoEditor.apply()

        val sharedPreferencesLocation = getSharedPreferences("location$jobId", Context.MODE_PRIVATE)
        val locationEditor = sharedPreferencesLocation.edit()
        locationEditor.clear()
        locationEditor.apply()

        val sharedPreferencesTds = getSharedPreferences("tds$jobId", Context.MODE_PRIVATE)
        val tdsEditor = sharedPreferencesTds.edit()
        tdsEditor.clear()
        tdsEditor.apply()


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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
