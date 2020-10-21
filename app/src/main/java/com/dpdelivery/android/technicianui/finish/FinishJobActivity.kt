package com.dpdelivery.android.technicianui.finish

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techres.BLEDetailsRes
import com.dpdelivery.android.model.techres.SparePartsData
import com.dpdelivery.android.model.techres.SubmiPidRes
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.photo.ImageActivity
import com.dpdelivery.android.technicianui.sync.Command
import com.dpdelivery.android.technicianui.sync.DatabaseHandler
import com.dpdelivery.android.technicianui.sync.SyncActivity
import com.dpdelivery.android.technicianui.techjobslist.TechJobsListActivity
import com.dpdelivery.android.ui.location.LocationActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_finish_job.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
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
    private var ownerName: String = ""
    private var botId: String? = null
    private var connectivity: String? = null
    private var inputTds: Int = 0
    private var outputTds: Int = 0
    private var jobEndTime: String? = null
    lateinit var dialog: Dialog
    private var partsIdList: ArrayList<Int>? = null
    private var temp: String? = ""
    private var tempid: String? = ""
    private var selectedItem: String? = null
    private var selectedItemid: String? = null
    private var items: String? = null
    private var itemsid: String? = null
    private var selectedItems = ArrayList<String>()
    private var selectedItemsid = ArrayList<String>()
    lateinit var dbH: DatabaseHandler

    @Inject
    lateinit var presenter: FinishJobPresenter
    private val TAG = FinishJobActivity::class.java.simpleName
    private var mode: String? = null
    private val paymentMode: Array<String> = arrayOf<String>("Payment Type", "Cash", "Card", "PayTM", "InstaMojo", "EazyPay-Paytm", "EazyPay-GPay", "EazyPay-PhonePay", "BankTransfer", "Cheque", "Other")
    lateinit var partList: ArrayList<SparePartsData>
    private var amountCollected: Float = 0.0f
    private var syncAt: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_finish_job, tech_layout_container)
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
        dbH = DatabaseHandler(this)
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
            deviceCode = intent.getStringExtra(Constants.DEVICE_CODE)
            botId = intent.getStringExtra(Constants.BOT_ID)
            connectivity = intent.getStringExtra(Constants.CONNECTIVITY)
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
        updateLatestDetails(deviceCode)
        fetchItemsFromSharedPref()
        if (connectivity!!.contentEquals("BLE")) {
            ll_sync.visibility = View.VISIBLE
            val botId = botId!!.substring(0, 2) + ":" + botId!!.substring(2, 4) + ":" + botId!!.substring(4, 6) + ":" + botId!!.substring(6, 8) + ":" + botId!!.substring(8, 10) + ":" + botId!!.substring(10, 12)
            btn_sync.setOnClickListener {
                startActivity(Intent(this, SyncActivity::class.java)
                        .putExtra("botId", botId)
                        .putExtra("purifierId", deviceCode)
                        .putExtra("owner", ownerName))
            }
        }
    }

    private fun updateLatestDetails(deviceCode: String?) {
        if (CommonUtils.hasUpdates) {
            updateServerCmds()
        } else {
            getPidDetails(deviceCode)
        }
    }

    private fun updateServerCmds() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        val params = HashMap<String, String>()
        params["purifierid"] = deviceCode.toString()
        params["currentliters"] = CommonUtils.current.toString() + ""
        params["validity"] = CommonUtils.validity!!
        params["flowlimit"] = CommonUtils.flowlimit.toString() + ""
        params["status"] = CommonUtils.purifierStatus.toString() + ""
        params["techApp"] = "1"

        val ja = JSONArray()
        try {
            val list = dbH.allAcks
            for (i in list.indices) {
                val cmd = list[i]
                val temp = JSONObject()
                temp.put("cmdid", cmd.id)
                temp.put("cmd", cmd.cmd)
                temp.put("status", cmd.status)
                ja.put(temp)
            }
            params["cmds"] = ja.toString()

        } catch (e: Exception) {

        }
        presenter.updateServerCmds(params)
    }

    override fun showSyncRes(res: BLEDetailsRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.status.equals("OK")) {
            dbH.clearAcks()
            CommonUtils.resetUpdate()
            getPidDetails(deviceCode)
        } else {
            updateServerCmds()
            toast(res.output!!.message!!)
        }
    }

    private fun getPidDetails(deviceCode: String?) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        val params = HashMap<String, String>()
        params["purifierid"] = deviceCode.toString()
        presenter.getPidDetails(params)
    }

    override fun showPidDetailsRes(res: BLEDetailsRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.status.equals("OK")) {
            ownerName = res.output?.owner!!
            syncAt = res.output.lastsync
            synctxt.text = syncAt
            dbH.deleteAll()
            val cmds = res.cmds
            for (i in 0 until cmds!!.size) {
                val c = Command(cmds[i]!!.id!!, cmds[i]!!.cmd, "INIT")
                //Log.i("command", "INIT Command Found")
                dbH.addCommand(c)
                Log.i("SSyyzzzz", "added into table")
            }
        } else {
            toast(res.output!!.message!!)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_location -> {
                val intent = Intent(context, LocationActivity::class.java)
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

    companion object {
        private const val FORMAT = "%02d:%02d:%02d"
    }

    private fun showOtp() {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.layout_otp, null)
        val otp = mView.findViewById<AppCompatEditText>(R.id.output_otp)
        val submitBtn = mView.findViewById<AppCompatButton>(R.id.submitotpbutton)
        val time = mView.findViewById<AppCompatTextView>(R.id.tv_time)
        val resendCode = mView.findViewById<AppCompatTextView>(R.id.tv_resend)

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time.text = ("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished))),
                        (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))))
            }

            override fun onFinish() {
                time.text = getString(R.string.time_out)
                resendCode.visibility = View.VISIBLE
            }
        }.start()

        mBuilder.setView(mView)
        val dialog = mBuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        resendCode.setOnClickListener {
            dialog.show()
            presenter.reSendHappyCode(jobId)
        }
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

    override fun showSparePartsRes(res: ArrayList<SparePartsData>) {
        dialog.dismiss()
        if (res.isNotEmpty()) {
            partList = res
            if (partList.isNotEmpty()) {
                addParts(partList)
            } else {
                toast("No Spares Found")
            }
        }
    }

    override fun reSendHappyCodeRes(res: SubmiPidRes) {
        if (res.success) {
            toast(res.message)
        }
    }

    private fun addParts(partsList: ArrayList<SparePartsData>) {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.layout_spare_parts, null)
        mBuilder.setView(mView)
        val dialog = mBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
        val listView = mView.findViewById<ListView>(R.id.list_view)
        val addbtn = mView.findViewById<Button>(R.id.btn_add_spares)
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        val adapter = ArrayAdapter(this, R.layout.item_spare_parts, R.id.tv_spare_name, partsList)
        listView.adapter = adapter

        //populate selectedItems from sharedpref
        prepareSelectedItemsList(listView, partList)
        fetchItemsAndSet(listView, partList, selectedItems, selectedItemsid)

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedItem = (view as TextView).text.toString()
            selectedItemid = partsList[position].itemid.toString()
            if (selectedItems.contains(selectedItem!!) && selectedItemsid.contains(selectedItemid!!)) {
                selectedItems.remove(selectedItem!!)
                selectedItemsid.remove(selectedItemid!!)
            } else {
                selectedItems.add(selectedItem!!)
                selectedItemsid.add(selectedItemid!!)
            }
        }

        dialog.setOnCancelListener {
            selectedItems.clear()
            selectedItemsid.clear()
            items = ""
            itemsid = ""
        }
        addbtn.setOnClickListener {
            items = ""
            itemsid = ""
            for (item in selectedItems) {
                if (items!!.isEmpty()) {
                    items += item
                } else if (items!!.isNotEmpty()) {
                    items += ",$item"
                }
            }
            for (item in selectedItemsid) {
                if (itemsid!!.isEmpty()) {
                    itemsid += item
                } else if (itemsid!!.isNotEmpty()) {
                    itemsid += ",$item"
                }
            }

            val sharedPreferences = getSharedPreferences("DrinkPrimeParts_$jobId.txt", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("items", items)
            editor.putString("itemsid", itemsid)
            editor.apply()
            fetchItemsFromSharedPref()
            dialog.dismiss()
        }

    }

    private fun prepareSelectedItemsList(chl: ListView, purifierParts: ArrayList<SparePartsData>) {
        val sharedPreferences = getSharedPreferences("DrinkPrimeParts_$jobId.txt", Context.MODE_PRIVATE)
        temp = sharedPreferences.getString("items", "Nothing")
        tempid = sharedPreferences.getString("itemsid", "Nothing")

        if (temp == "Nothing") temp = ""
        if (tempid == "Nothing") tempid = ""

        val aList = ArrayList(Arrays.asList(*temp!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        aList.remove("")
        for (i in aList.indices) {
            if (!selectedItems.contains(aList[i].toString())) {
                selectedItems.add(aList[i].toString())

            }
        }
        val aListId = ArrayList(Arrays.asList(*tempid!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        aListId.remove("")
        for (i in aListId.indices) {
            if (!selectedItemsid.contains(aListId[i].toString())) {
                selectedItemsid.add(aListId[i].toString())
            }
        }
        //loop through purifierParts
        //find the position of items in purifierParts
        val checked = chl.checkedItemPositions
        for (i in 0 until chl.adapter.count) {
            if (checked.get(i)) {
                val part = purifierParts[i].itemname
                val partId = purifierParts[i].itemid.toString()
                if (!selectedItems.contains(part) && !selectedItemsid.contains(partId))
                    selectedItems.add(part)
                selectedItemsid.add(partId)

            }
        }
    }

    private fun fetchItemsAndSet(chl: ListView, purifierParts: ArrayList<SparePartsData>, selectedItems: ArrayList<String>, selectedItemsid: ArrayList<String>) {
        var item: String
        var itemid: String
        for (i in selectedItems.indices) {

            item = selectedItems[i]
            Log.e(TAG, selectedItems[i])
            for (j in purifierParts.indices) {
                if (item == purifierParts[j]?.itemname) {
                    chl.setItemChecked(j, true)
                }
            }
        }

        for (i in selectedItemsid.indices) {

            itemid = selectedItemsid[i]
            Log.e(TAG, selectedItemsid[i])
            for (j in purifierParts.indices) {
                if (itemid == purifierParts[j].itemid.toString()) {
                    chl.setItemChecked(j, true)
                }
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
        if (amount.isNotEmpty()) {
            amountCollected = amount.toFloat()
        } else {
            toast("Please check all the fields before submitting")
        }
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
            startActivity(Intent(this, TechJobsListActivity::class.java))
            clearPreferences()
            finish()
        } else {
            toast(res.message)
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        toast(throwable.message.toString())
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
        val fetchedItemsName = sharedPreferences.getString("items", "")
        val s = sharedPreferences.getString("itemsid", "")
        val spareParts = fetchedItemsName!!.replace("[", "")
        val spareParts1 = spareParts.replace("]", "")
        val st = StringTokenizer(s, ",")
        val result = ArrayList<Int>()
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().toInt())
        }
        partsIdList = result
        if (fetchedItemsName.isNullOrEmpty()) {
            add_parts_txt!!.text = "No Parts Are Selected"
            add_parts_txt!!.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        } else {
            add_parts_txt!!.text = spareParts1
            add_parts_txt!!.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
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
