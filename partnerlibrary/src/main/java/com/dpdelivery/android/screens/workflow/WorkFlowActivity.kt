package com.dpdelivery.android.screens.workflow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.SyncCommandsRes
import com.dpdelivery.android.model.techinp.*
import com.dpdelivery.android.model.techinp.Cmd
import com.dpdelivery.android.model.techres.*
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.scanner.ScannerActivity
import com.dpdelivery.android.screens.sync.Command
import com.dpdelivery.android.screens.sync.DatabaseHandler
import com.dpdelivery.android.screens.sync.SyncActivity
import com.dpdelivery.android.screens.techjobslist.TechJobsListActivity
import com.dpdelivery.android.screens.workflow.workflowadapter.SparesListAdapter
import com.dpdelivery.android.screens.workflow.workflowadapter.TemplateListAdapter
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_work_flow.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.item_element_list.view.*
import kotlinx.android.synthetic.main.item_spares.view.*
import kotlinx.android.synthetic.main.item_timeline.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject


class WorkFlowActivity : TechBaseActivity(), WorkFlowContract.View, View.OnClickListener,
    IAdapterClickListener {

    lateinit var mContext: Context
    private var jobId: Int? = 0
    private var deviceCode: String? = null
    private var botId: String? = null
    private var connectivity: String? = null
    lateinit var mLayoutManager: LinearLayoutManager
    private var workFlowAdapter: TemplateListAdapter? = null
    private var currentPosition: Int = 0
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapterNotesList: BasicAdapter
    private var isSuccess: Boolean = false

    @Inject
    lateinit var workFlowPresenter: WorkFlowPresenter
    private var mDataList: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step>? = null
    private val CAMERA_REQUEST = 0
    private val PHOTO_FILE_NAME = UUID.randomUUID().toString() + ".jpeg"
    private lateinit var bitmap: Bitmap
    private var imgpath: String = ""
    private var jobType: String? = null
    lateinit var dialog: Dialog
    lateinit var data: JSONObject
    private var image: AppCompatImageView? = null
    private var mandatory: AppCompatImageView? = null
    private var mandatoryIcon: AppCompatImageView? = null
    private var elementId: Int = 0
    private var spareelementId: Int = 0
    private var mTemplateList: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>? = null
    private var noteList: ArrayList<Note?>? = null
    private val stepMap: MutableMap<String, String> = mutableMapOf()
    private val stepsFinished: MutableMap<String, Boolean> = mutableMapOf()
    private val stepMapList = ArrayList<AddWorkFlowData.Data>()
    private var latitude: String = ""
    private var longitude: String = ""
    private var syncElementId: Int = 0
    private var activationElementId: Int = 0
    private var wifiConfigId: Int = 0
    private var sparePartId: Int = 0
    private var et_device_code: AppCompatEditText? = null
    private var et_purifier_id: AppCompatEditText? = null
    private var btn_activate: AppCompatButton? = null
    private var btn_set_up_wifi: AppCompatButton? = null
    private var iv_refresh: AppCompatImageView? = null
    private var tv_status: AppCompatTextView? = null
    private var LOCATION_PERMISSION_REQUEST_CODE = 123
    lateinit var partList: ArrayList<PartInfo>
    private var spinnerSpares: Spinner? = null
    private var rvSpares: RecyclerView? = null

    //private var searchView: androidx.appcompat.widget.SearchView? = null
    private var etSearch: AppCompatEditText? = null
    private var value: String? = null
    lateinit var dbH: DatabaseHandler
    private var ownerName: String = ""
    private var synctext: TextView? = null
    private var statusText: TextView? = null
    private var isSync: Boolean = false
    lateinit var adapterPartsList: SparesListAdapter
    private var itemsMap: MutableMap<Int, String> = mutableMapOf()
    private var currentPhotoPath: String? = null
    private val cmds = ArrayList<Cmd>()
    private var apiElementId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(applicationContext)
            .inflate(R.layout.activity_work_flow, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("WorkFlow Details")
        showBack()
        setUpBottomNavView(false)
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
            deviceCode = intent.getStringExtra(Constants.DEVICE_CODE)
            botId = intent.getStringExtra(Constants.BOT_ID)
            connectivity = intent.getStringExtra(Constants.CONNECTIVITY)
            jobType = intent.getStringExtra(Constants.JOB_TYPE)
            noteList = intent.getParcelableArrayListExtra(Constants.NOTES)
        }
        initRecyclerView()
        dialog = CommonUtils.progressDialog(context)
        btn_next.setOnClickListener(this)
        btn_Finish.setOnClickListener(this)
        tv_view_notes.visibility = View.VISIBLE
        tv_view_notes.setOnClickListener(this)
        error_button.setOnClickListener(this)
        dbH = DatabaseHandler(this)
        getPartnerDetails()
    }

    private fun getPartnerDetails() {
        workFlowPresenter.getPartnerDetails()
    }

    override fun showPartnerDetails(res: PartnerDetailsRes) {
        CommonUtils.saveUserDetails(res)
        getWorkFlowData(jobId)
    }

    //get workflow data
    private fun getWorkFlowData(jobId: Int?) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        if (jobId != null) {
            workFlowPresenter.getWorkFlowData(jobId)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_view_notes -> {
                if (noteList!!.isNotEmpty()) {
                    showNotesList()   // for showing notes list
                } else {
                    toast("No Notes Found")
                }
            }
            R.id.btn_next -> {
                if (stepsFinished.containsValue(false)) {
                    toast("Please submit mandatory fields")
                } else {
                    nextStep()
                }
            }
            R.id.btn_Finish -> {
                if (stepsFinished.containsValue(false)) {
                    toast("Please submit mandatory fields")
                } else {
                    finishJob()
                    btn_Finish.isEnabled = false
                }
            }
            R.id.btn_submit -> {
                if (stepsFinished.containsValue(false)) {
                    toast("Please submit mandatory fields")
                } else {
                    submit()
                }
            }
            R.id.error_button -> {
                init()
            }
        }
    }

    //showing notes
    private fun showNotesList() {
        dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        dialog.setContentView(R.layout.layout_note_list)
        dialog.setCancelable(true)
        (dialog.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        manager = LinearLayoutManager(this)
        (dialog.findViewById(R.id.rv_notes_list) as RecyclerView).layoutManager = manager
        adapterNotesList =
            BasicAdapter(this, R.layout.tech_item_notes_list, adapterClickListener = this)
        (dialog.findViewById(R.id.rv_notes_list) as RecyclerView).apply {
            adapter = adapterNotesList
            noteList.withNotNullNorEmpty {
                adapterNotesList.addList(noteList!!)
            }
        }
        dialog.show()
    }

    //initializing recyclerview
    private fun initRecyclerView() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("LongLogTag")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        mLayoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerView.apply {
            layoutManager = mLayoutManager
            workFlowAdapter = TemplateListAdapter(
                mContext,
                adapterClickListener = this@WorkFlowActivity,
                stepMap = stepMap,
                stepsFinished = stepsFinished,
                activationElementId = activationElementId,
                wifiConfigId = wifiConfigId,
                syncElementId = syncElementId,
                sparePartId = sparePartId
            )
            adapter = workFlowAdapter
        }
        val recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
        workFlowAdapter!!.notifyDataSetChanged()
        recyclerView.layoutManager!!.onRestoreInstanceState(recyclerViewState)

    }


    //workflow data response
    override fun showWorFlowDataRes(res: WorkFlowDataRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            mDataList = res.body!!.steps
            activationElementId = res.body.activationElementId
            wifiConfigId = res.body.wifiConfigID
            syncElementId = res.body.syncElementId
            sparePartId = res.body.sparePartId
            /* wifiBotId = res.body.wifiBotId
             if (wifiBotId == res.body.steps?.get(1)?.templates?.get(0)?.elements!![1].id) {
                 wifiBotAddress = res.body.steps[0].templates?.get(0)?.elements!![1].value
             }*/
            //setting up steps based on position
            setStep(currentPosition)
        }
    }

    //setting up steps based on position
    @SuppressLint("SetTextI18n")
    private fun setStep(position: Int) {
        if (mDataList != null && mDataList!!.isNotEmpty() && mDataList!!.size > position) {
            currentPosition = position
            stepMap.clear()
            stepsFinished.clear()
            stepMapList.clear()
            tv_nums.text = "" + (position + 1) + "."
            tv_step_name.text = mDataList?.get(position)?.name
            mTemplateList = mDataList?.get(position)?.templates
            workFlowAdapter!!.addList(
                mTemplateList,
                activationElementId,
                wifiConfigId,
                syncElementId,
                sparePartId
            )
            if (currentPosition == mDataList!!.size - 1) {
                btn_next.visibility = View.GONE
                btn_Finish.visibility = View.VISIBLE
            }
        }
    }

    //nextstep will be shown when there are more steps
    private fun nextStep() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        stepMapList.clear()
        for (mutableEntry in stepMap) {
            stepMapList.add(
                AddWorkFlowData.Data(
                    elementId = mutableEntry.key,
                    value = mutableEntry.value
                )
            )
        }
        workFlowPresenter.addWorkFlow(
            workFlow = AddWorkFlowData(
                data = stepMapList,
                jobId = jobId!!
            )
        )
    }

    //submit will be called when matches with submission field in dropdown
    private fun submit() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        for (mutableEntry in stepMap) {
            stepMapList.add(
                AddWorkFlowData.Data(
                    elementId = mutableEntry.key,
                    value = mutableEntry.value
                )
            )
        }
        workFlowPresenter.addWorkFlowSubmit(
            workFlow = AddWorkFlowData(
                data = stepMapList,
                jobId = jobId!!
            )
        )
    }

    private fun finishJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        stepMapList.clear()
        for (mutableEntry in stepMap) {
            stepMapList.add(
                AddWorkFlowData.Data(
                    elementId = mutableEntry.key,
                    value = mutableEntry.value
                )
            )
        }
        workFlowPresenter.addFinishWorkFlow(
            workFlow = AddWorkFlowData(
                data = stepMapList,
                jobId = jobId!!
            )
        )
    }

    override fun onResume() {
        super.onResume()
        workFlowPresenter.takeView(this)
    }

    override fun showAddTextRes(res: AddTextRes) {
        dialog.dismiss()
        if (res.success!!) {
            toast(res.message!!)
        } else {
            toast(res.message!!)
        }
    }

    override fun showWorkFlowDataRes(res: AddTextRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            stepMap.clear()
            itemsMap.clear()
            stepsFinished.clear()
            stepMapList.clear()
            if (currentPosition < mDataList!!.size) {
                setStep(currentPosition + 1)
                if (currentPosition == mDataList!!.size - 1) {
                    btn_next.visibility = View.GONE
                    btn_Finish.visibility = View.VISIBLE
                }
            }
        } else {
            toast(res.message!!)
        }
    }

    override fun showWorkFlowDataSubmitRes(res: AddTextRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            stepMap.clear()
            stepsFinished.clear()
            stepMapList.clear()
            finish()
        } else {
            toast(res.message!!)
        }
    }

    override fun showWorkFlowFinishDataRes(res: AddTextRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            showViewState(MultiStateView.VIEW_STATE_LOADING)
            val currentTime = Date()
            val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
            output.timeZone = TimeZone.getTimeZone("GMT")
            val jobEndTime = output.format(currentTime)
            val finishJobIp = FinishJobIp(
                status = "COM",
                latitude = latitude,
                longitude = longitude,
                jobEndTime = jobEndTime
            )
            workFlowPresenter.finishJob(jobId!!, finishJobIp)
        } else {
            btn_Finish.isEnabled = true
            toast(res.message!!)
        }
    }

    override fun showFinishJobRes(res: SubmiPidRes) {
        if (res.success) {
            toast("Job Completed")
            stepMap.clear()
            stepsFinished.clear()
            stepMapList.clear()
            CommonUtils.saveBotId("")
            finish()
        } else {
            toast(res.message)
            btn_Finish.isEnabled = true
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (throwable is HttpException) {
            when (throwable.code()) {
                403 -> {
                    SharedPreferenceManager.clearPreferences()
                    toast("Session expired..reopen app again")
                    finishAffinity()
                }
                else -> {
                    showViewState(MultiStateView.VIEW_STATE_ERROR)
                    toast(throwable.message.toString())
                }
            }
        } else {
            //toast(throwable.message.toString())
            Log.e("msg", throwable.message.toString())
        }
        dialog.dismiss()
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element && type is View) {
            when (op) {
                Constants.ELEMENT_IMAGE -> {
                    image = type.btn_add_image
                    elementId = any.id
                    mandatory = type.iv_mandatory2
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), CAMERA_REQUEST
                            )
                        } else {
                            startCamera()
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.permission_denied),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                Constants.SCAN_CODE -> {
                    et_device_code = type.et_add_text
                    IntentIntegrator(this).setCaptureActivity(ScannerActivity::class.java)
                        .initiateScan()
                }
                Constants.SCAN_QR_CODE -> {
                    et_device_code = type.et_purifier_id
                    IntentIntegrator(this).setCaptureActivity(ScannerActivity::class.java)
                        .initiateScan()
                }
                Constants.SUBMIT_PID -> {
                    dialog.show()
                    et_purifier_id = type.et_purifier_id
                    iv_refresh = type.iv_refresh
                    btn_activate = type.btn_activate
                    workFlowPresenter.submitPid(
                        submitPidIp = SubmitPidIp(
                            deviceCode = type.et_purifier_id.text.toString(),
                            jobId = jobId!!
                        )
                    )
                }
                Constants.REFRESH_STATUS -> {
                    dialog.show()
                    mandatory = type.iv_mandatory
                    tv_status = type.tv_status
                    et_purifier_id = type.et_purifier_id
                    elementId = any.id
                    deviceCode = type.et_purifier_id.text.toString()
                    workFlowPresenter.refreshPidStatus(type.et_purifier_id.text.toString())
                }
                Constants.SPARE_PARTS -> {
                    dialog.show()
                    spareelementId = any.id
                    value = any.value
                    etSearch = type.et_search
                    rvSpares = type.rv_spares
                    rvSpares!!.layoutManager = LinearLayoutManager(context)
                    val api = any.functionName
                    workFlowPresenter.getSparePartsList("$api${CommonUtils.getId()}")
                }
                Constants.API_INPUT -> {
                    dialog.show()
                    apiElementId = any.id
                    value = any.value
                    mandatoryIcon = type.iv_mandatory_list
                    spinnerSpares = type.spinner_spares
                    val functionName = any.functionName.toString()
                    if (functionName.contains("jobId")) {
                        val data = functionName.replace("{jobId}", jobId.toString())
                        workFlowPresenter.getApiDataList(data)
                    } else {
                        workFlowPresenter.getApiDataList(any.functionName.toString())
                    }
                }
                Constants.SYNC -> {
                    dialog.show()
                    synctext = type.tv_sync
                    elementId = any.id
                    getCmdDetails(deviceCode)
                }
                Constants.SET_UP_WIFI -> {
                    //startActivity(Intent(this, SmartConfigActivity::class.java))
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                Constants.REFRESH_WIFI -> {
                    dialog.show()
                    elementId = any.id
                    mandatory = type.iv_mandatory
                    statusText = type.tv_wifi_bot_status
                    btn_set_up_wifi = type.btn_setup_wifi
                    workFlowPresenter.getBidStatus(
                        data = BIDStatusIp(
                            botId = botId!!,
                            connectivity = connectivity!!
                        )
                    )
                }
            }
        }
        if (any is PartInfo && type is View) {
            when (op) {
                Constants.ADD_SPARES -> {
                    if (any.picked > 0) {
                        any.mycart += 1
                        type.tv_quantity.text = any.mycart.toString()
                        type.iv_add.visibility = View.GONE
                        type.ll_add.visibility = View.VISIBLE
                        val itemName =
                            when {
                                any.item_name.contains(",") -> {
                                    any.item_name.replace(",", "-", true)
                                }
                                any.item_name.contains("/") -> {
                                    any.item_name.replace("/", "|", true)
                                }
                                else -> {
                                    any.item_name
                                }
                            }
                        itemsMap[any.item_id] =
                            "{item_id:${any.item_id}/item_name:$itemName/quantity:${any.mycart}/serializable:${any.serializable}}"
                        stepMap[spareelementId.toString()] = itemsMap.values.toString()
                    } else {
                        Toast.makeText(context, "Inventory items not available", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                Constants.INCREMENT -> {
                    if (any.mycart < any.picked) {
                        any.mycart += 1
                        type.tv_quantity.text = any.mycart.toString()
                        val itemName =
                            when {
                                any.item_name.contains(",") -> {
                                    any.item_name.replace(",", "-", true)
                                }
                                any.item_name.contains("/") -> {
                                    any.item_name.replace("/", "|", true)
                                }
                                else -> {
                                    any.item_name
                                }
                            }
                        itemsMap[any.item_id] =
                            "{item_id:${any.item_id}/item_name:$itemName/quantity:${any.mycart}/serializable:${any.serializable}}"
                        stepMap[spareelementId.toString()] = itemsMap.values.toString()
                    } else {
                        Toast.makeText(
                            context,
                            "Inventory items not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Constants.DECREMENT -> {
                    if (any.mycart != 0) {
                        any.mycart -= 1
                        if (any.mycart == 0) {
                            type.iv_add.visibility = View.VISIBLE
                            type.ll_add.visibility = View.GONE
                        }
                        type.tv_quantity.text = any.mycart.toString()
                        val itemName =
                            when {
                                any.item_name.contains(",") -> {
                                    any.item_name.replace(",", "-", true)
                                }
                                any.item_name.contains("/") -> {
                                    any.item_name.replace("/", "|", true)
                                }
                                else -> {
                                    any.item_name
                                }
                            }
                        itemsMap[any.item_id] =
                            "{item_id:${any.item_id}/item_name:$itemName/quantity:${any.mycart}/serializable:${any.serializable}}"
                        if (itemsMap.values.contains("{item_id:${any.item_id}/item_name:$itemName/quantity:${0}/serializable:${any.serializable}}")) {
                            itemsMap.remove(
                                any.item_id,
                                "{item_id:${any.item_id}/item_name:$itemName/quantity:${any.mycart}/serializable:${any.serializable}}"
                            )
                        }
                        stepMap[spareelementId.toString()] = itemsMap.values.toString()
                    }
                }
            }
        }
    }

    override fun showBidStatus(res: BIDStatusRes) {
        dialog.dismiss()
        if (res.success) {
            mandatory!!.visibility = View.INVISIBLE
            statusText!!.text = "SUCCESS"
            stepMap[elementId.toString()] = "SUCCESS"
            stepsFinished[elementId.toString()] = true
            btn_set_up_wifi!!.visibility = View.GONE
        } else {
            statusText!!.text = "FAILED"
            btn_set_up_wifi!!.visibility = View.VISIBLE
        }
    }

    private fun getCmdDetails(purifierid: String?) {
        workFlowPresenter.getBleCmdDetails(deviceCode = purifierid!!, onlyPending = true)
    }

    override fun showBleCmdDetailsRes(res: SyncCommandsRes) {
        dialog.dismiss()
        val cmds = res.body
        if (cmds != null) {
            dbH.deleteAll()
            for (i in cmds.indices) {
                val c = Command(cmds[i].sequenceNo, cmds[i].command, cmds[i].status)
                dbH.addCommand(c)
                Log.i("SSyyzzzz", "added into table")
            }
        }
        getPidDetails(deviceCode)
    }

    private fun getPidDetails(deviceCode: String?) {
        workFlowPresenter.getPidDetails(homeIP = HomeIP(purifierid = deviceCode!!))
    }

    override fun showPidDetailsRes(res: BLEDetailsRes) {
        dialog.dismiss()
        if (res.status.equals("OK")) {
            if (isSync) {
                synctext!!.text = res.output!!.lastsync.toString()
                stepMap[elementId.toString()] = res.output.lastsync.toString()
                stepsFinished[elementId.toString()] = true
            } else {
                ownerName = res.output?.owner!!
                dbH.deleteAll()
                val cmds = res.cmds
                for (i in cmds!!.indices) {
                    val c = Command(cmds[i]?.id!!, cmds[i]!!.cmd, "INIT")
                    //Log.i("command", "INIT Command Found")
                    dbH.addCommand(c)
                    Log.i("SSyyzzzz", "added into table")
                }
                if (botId != null) {
                    val botId = botId!!.substring(0, 2) + ":" + botId!!.substring(
                        2,
                        4
                    ) + ":" + botId!!.substring(4, 6) + ":" + botId!!.substring(
                        6,
                        8
                    ) + ":" + botId!!.substring(8, 10) + ":" + botId!!.substring(10, 12)
                    startActivity(
                        Intent(this, SyncActivity::class.java)
                            .putExtra("botId", botId)
                            .putExtra("purifierId", deviceCode)
                            .putExtra("owner", ownerName)
                    )
                } else {
                    toast("Please Restart your app")
                }
            }
        } else {
            toast(res.output!!.message!!)
        }
    }

    override fun showSparePartsRes(res: InventoryRes) {
        dialog.dismiss()
        if (res.part_info.isNotEmpty()) {
            partList = res.part_info
            adapterPartsList = SparesListAdapter(context, adapterClickListener = this)
            rvSpares!!.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
            rvSpares!!.adapter = adapterPartsList
            rvSpares!!.setItemViewCacheSize(100)
            rvSpares!!.isNestedScrollingEnabled = false
            adapterPartsList.addList(partList)
            etSearch!!.visibility = View.VISIBLE
            etSearch!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    adapterPartsList.filter.filter(p0)
                }
            })
            /*searchView!!.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // calling a method to filter our recycler view.
                    adapterPartsList.filter.filter(newText)
                    return false
                }
            })*/
        } else {
            etSearch!!.visibility = View.GONE
            toast("No Spares Found")
        }
    }

    override fun showApiInputRes(res: ApiInputRes) {
        dialog.dismiss()
        val data = ArrayList<String>()
        if (res.isNotEmpty()) {
            data.clear()
            data.add("-")
            for (i in res) {
                data.add(i)
            }
            for (i in data.indices) {
                if (!value.isNullOrEmpty()) {
                    if (data[i].equals(
                            (value),
                            true
                        )
                    ) {
                        val adapterMode: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                            this, android.R.layout.simple_spinner_item, data as List<String?>
                        ) {
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
                        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerSpares!!.adapter = adapterMode
                        spinnerSpares!!.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    if (position > 0) {
                                        val selectedString = spinnerSpares!!.selectedItem.toString()
                                        stepMap[elementId.toString()] = selectedString
                                        stepsFinished[elementId.toString()] = true
                                        mandatoryIcon!!.visibility = View.INVISIBLE
                                    }
                                }
                            }
                        spinnerSpares!!.setSelection(i)
                    }
                } else {
                    val adapterMode: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                        this, android.R.layout.simple_spinner_item, data as List<String?>
                    ) {
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
                    adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSpares!!.adapter = adapterMode
                    spinnerSpares!!.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position > 0) {
                                    val selectedString = spinnerSpares!!.selectedItem.toString()
                                    stepMap[apiElementId.toString()] = selectedString
                                    stepsFinished[apiElementId.toString()] = true
                                    mandatoryIcon!!.visibility = View.INVISIBLE
                                }
                            }
                        }
                    spinnerSpares!!.setSelection(0)
                }
            }

        } else {
            toast("No Data Found")
        }
    }

    private fun startCamera() {
        val intent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        val path = mContext.getExternalFilesDir(null)!!.absolutePath
        val dir = File(path, "DP Partner 2.0/Image")
        if (!dir.exists())
            dir.mkdirs()
        val photoURI = FileProvider.getUriForFile(
            context, "com.dpdelivery.android.provider",
            File(dir.absolutePath, PHOTO_FILE_NAME)
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, CAMERA_REQUEST)
        /*  Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
              // Ensure that there's a camera activity to handle the intent
              takePictureIntent.resolveActivity(packageManager)?.also {
                  // Create the File where the photo should go
                  val photoFile: File? = try {
                      createImageFile()
                  } catch (ex: IOException) {
                      // Error occurred while creating the File
                      null
                  }
                  // Continue only if the File was successfully created
                  photoFile?.also {
                      val photoURI: Uri = FileProvider.getUriForFile(
                          this,
                          "com.example.android.fileprovider",
                          it
                      )
                      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                  }
              }
          }*/
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            val f = File(mContext.getExternalFilesDir(null)!!.absolutePath)
            var dir = File(f, "DP Partner 2.0/Image")
            if (!dir.exists())
                dir.mkdirs()
            for (temp in dir.listFiles()!!) {
                if (temp.name == PHOTO_FILE_NAME) {
                    dir = temp
                    imgpath = dir.absolutePath.toString()
                    break
                }
            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf<String>(dir.absolutePath),
                null
            ) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }

            if (!dir.exists()) {
                Toast.makeText(
                    baseContext,
                    "Error while capturing image",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            try {
                bitmap = BitmapFactory.decodeFile(dir.absolutePath)
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                var rotate = 0
                try {
                    val exif = ExifInterface(dir.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val matrix = Matrix()
                matrix.postRotate(rotate.toFloat())
                bitmap =
                    Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                image!!.setImageBitmap(bitmap)
                if (imgpath.isNotEmpty()) {
                    dialog.show()
                    mandatory!!.visibility = View.INVISIBLE
                    stepsFinished[elementId.toString()] = true
                    workFlowPresenter.addImage(
                        jobid = jobId!!,
                        elementId = elementId,
                        file = Compressor(this).compressToFile(File(imgpath))
                    )
                } else {
                    toast("Problem in Taking Photo..Please try again")
                }
                CommonUtils.setUserImagebitmap(mContext, image!!, stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                    if (result.contents.length == 10) {
                        et_device_code!!.setText(result.contents)
                    } else {
                        toast("Purifier ID Is Not Valid")
                    }
                } else
                    toast("Purifier ID Is Not Valid")
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
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

    override fun showSubmittedPidRes(submiPidRes: SubmiPidRes) {
        if (submiPidRes.success) {
            dialog.dismiss()
            try {
                btn_activate!!.isEnabled = false
                et_purifier_id!!.isEnabled = false
                iv_refresh!!.isEnabled = true
                toast(submiPidRes.message)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (!submiPidRes.success) {
            toast(submiPidRes.message)
            dialog.dismiss()
            isSuccess = submiPidRes.success
        }
    }

    override fun showRefreshPidRes(res: PIdStatusRes) {
        try {
            dialog.dismiss()
            tv_status!!.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            tv_status!!.text = res.description.toUpperCase()
            tv_status!!.setTextColor(Color.RED)
            if (tv_status!!.text.toString() == "ACTIVE") {
                workFlowPresenter.getJob(jobId!!)
                mandatory!!.visibility = View.INVISIBLE
                stepMap[elementId.toString()] = et_purifier_id!!.text.toString()
                stepsFinished[elementId.toString()] = true
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun showJobRes(res: Job) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        // botId = res.bid + ""
        CommonUtils.saveBotId(res.bid + "")
        connectivity = res.connectivity + ""
    }

    /**
     * Location
     */
    private fun setUpLocationListener() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (currentPosition > 0) {
                    init()
                    setStep(currentPosition - 1)
                    btn_next.visibility = View.VISIBLE
                    btn_Finish.visibility = View.GONE
                } else {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (currentPosition > 0) {
            init()
            setStep(currentPosition - 1)
            btn_next.visibility = View.VISIBLE
            btn_Finish.visibility = View.GONE
        } else {
            finish()
        }
    }
}