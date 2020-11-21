package com.dpdelivery.android.technicianui.workflow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.model.techinp.AddTextIp
import com.dpdelivery.android.model.techinp.FinishJobIp
import com.dpdelivery.android.model.techres.AddTextRes
import com.dpdelivery.android.model.techres.SubmiPidRes
import com.dpdelivery.android.model.techres.TechNote
import com.dpdelivery.android.model.techres.WorkFlowDataRes
import com.dpdelivery.android.technicianui.base.TechBaseActivity
import com.dpdelivery.android.technicianui.finish.FinishJobActivity
import com.dpdelivery.android.technicianui.techjobslist.TechJobsListActivity
import com.dpdelivery.android.technicianui.workflow.viewholder.TemplateListViewHolder
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.toast
import com.dpdelivery.android.utils.withNotNullNorEmpty
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_work_flow.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.item_element_list.view.*
import kotlinx.android.synthetic.main.item_timeline.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class WorkFlowActivity : TechBaseActivity(), WorkFlowContract.View, View.OnClickListener, IAdapterClickListener {

    lateinit var mContext: Context
    private var jobId: Int? = 0
    private var deviceCode: String? = null
    private var botId: String? = null
    private var connectivity: String? = null
    lateinit var mLayoutManager: LinearLayoutManager
    private var workFlowAdapter: TemplateListViewHolder? = null
    private var currentPosition: Int = 0
    lateinit var manager: LinearLayoutManager
    lateinit var adapterNotesList: BasicAdapter

    @Inject
    lateinit var workFlowPresenter: WorkFlowPresenter
    private var mDataList: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step>? = null
    private var doubleBackToExitPressedOnce = false
    private val CAMERA_REQUEST = 0
    private val PHOTO_FILE_NAME = UUID.randomUUID().toString() + ".jpeg"
    private lateinit var bitmap: Bitmap
    private var imgpath: String = ""
    private var jobType: String? = null
    lateinit var dialog: Dialog
    lateinit var data: JSONObject
    private var image: AppCompatImageView? = null
    private var upload_image: AppCompatImageView? = null
    private var elementId: Int = 0
    private var mTemplateList: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template>? = null
    private var mElementList: ArrayList<WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element>? = null
    private var noteList: ArrayList<TechNote?>? = null
    private var itemsList = ArrayList<String?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_work_flow, tech_layout_container)
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
        getWorkFlowData(jobId)
        initRecyclerView()
        dialog = CommonUtils.progressDialog(context)
        btn_next.setOnClickListener(this)
        btn_Finish.setOnClickListener(this)
        tv_view_notes.visibility = View.VISIBLE
        tv_view_notes.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            /* R.id.btn_next -> {
                 try {
                     var i = 0
                     var j = 0
                     while (i <= mTemplateList!!.size) {
                         mElementList = mTemplateList!![i].elements
                         while (j <= mElementList!!.size) {
                             if (!(mElementList!![j].optional)!! && mElementList!![j].value.isNullOrEmpty()) {
                                 toast("Please submit mandatory fields")
                                 break
                             } else {
                                 if (currentPosition < mDataList!!.size) {
                                     setStep(currentPosition + 1)
                                     if (currentPosition == mDataList!!.size - 1) {
                                         btn_next.visibility = View.GONE
                                         btn_Finish.visibility = View.VISIBLE
                                     }
                                 }
                             }
                             j++
                         }
                         i++
                     }
                 } catch (e: Exception) {
                 }
             }
             R.id.btn_Finish -> {
 *//*                var i = 0
                val ja = ArrayList<AddWorkFlowData.Data>()
                try {
                    while (i <= mTemplateList!!.size) {
                        mElementList = mTemplateList!![i].elements
                        try {
                            val list = mElementList
                            for (data in list!!.indices) {
                                val cmd = list[data]
                                if (list[data].inputApi.equals("TEXT")) {
                                    val temp = AddWorkFlowData.Data(elementId = cmd.id, value = cmd.value)
                                    ja.add(temp)
                                }
                            }
                        } catch (e: Exception) {

                        }
                        i++
                    }
                } catch (e: Exception) {

                }
                workFlowPresenter.addWorkFlow(workFlow = AddWorkFlowData(data = ja, jobId = jobId))*//*
            }*/
            R.id.tv_view_notes -> {
                if (noteList!!.isNotEmpty()) {
                    showNotesList()   // for showing notes list
                } else {
                    toast("No Notes Found")
                }
            }
        }
    }

    private fun showNotesList() {
        dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        dialog.setContentView(R.layout.layout_note_list)
        dialog.setCancelable(true)
        (dialog.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        manager = LinearLayoutManager(this)
        (dialog.findViewById(R.id.rv_notes_list) as RecyclerView).layoutManager = manager
        adapterNotesList = BasicAdapter(this, R.layout.tech_item_notes_list, adapterClickListener = this)
        (dialog.findViewById(R.id.rv_notes_list) as RecyclerView).apply {
            adapter = adapterNotesList
            noteList.withNotNullNorEmpty {
                adapterNotesList.addList(noteList!!)
            }
        }
        dialog.show()
    }

    fun nextStep() {
        /* showViewState(MultiStateView.VIEW_STATE_LOADING)
         var i = 0
         val ja = ArrayList<AddWorkFlowData.Data>()
         try {
             while (i <= mTemplateList!!.size) {
                 mElementList = mTemplateList!![i].elements
                 try {
                     val list = mElementList
                     val sharedPreferences = getSharedPreferences("$jobId${Constants.MyPREFERENCES}", Context.MODE_PRIVATE)
                     val selectedListName = sharedPreferences.getString("name_key", "")
                     for (data in itemsList.indices) {
                         val item = list!![data]
                         if (item.inputApi.equals("TEXT")) {
                             val temp = AddWorkFlowData.Data(elementId = item.id, value = selectedListName)
                             ja.add(temp)
                         }
                     }
                 } catch (e: Exception) {

                 }
                 i++
             }
         } catch (e: Exception) {

         }
         workFlowPresenter.addWorkFlow(workFlow = AddWorkFlowData(data = ja, jobId = jobId))*/
        if (currentPosition < mDataList!!.size) {
            setStep(currentPosition + 1)
            if (currentPosition == mDataList!!.size - 1) {
                btn_next.visibility = View.GONE
                btn_Finish.visibility = View.VISIBLE
            }
        }
    }

    fun finishJob() {
        if (jobType == "DEL" || jobType == "UIN") {
            showViewState(MultiStateView.VIEW_STATE_LOADING)
            val finishJobIp = FinishJobIp(status = "COM")
            workFlowPresenter.finishJob(jobId!!, finishJobIp)
        } else {
            val intent = Intent(this, FinishJobActivity::class.java)
            intent.putExtra(Constants.ID, jobId)
            intent.putExtra(Constants.DEVICE_CODE, deviceCode)
            intent.putExtra(Constants.BOT_ID, botId)
            intent.putExtra(Constants.CONNECTIVITY, connectivity)
            intent.putExtra(Constants.JOB_TYPE, jobType)
            startActivity(intent)
        }
    }

    private fun getWorkFlowData(jobId: Int?) {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        if (jobId != null) {
            workFlowPresenter.getWorkFlowData(jobId)
        }
    }

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
            workFlowAdapter = TemplateListViewHolder(mContext, adapterClickListener = this@WorkFlowActivity, jobId = jobId!!)
            adapter = workFlowAdapter
        }
        val recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
        workFlowAdapter!!.notifyDataSetChanged()
        recyclerView.layoutManager!!.onRestoreInstanceState(recyclerViewState)

    }

    override fun onResume() {
        super.onResume()
        workFlowPresenter.takeView(this)
    }

    override fun showWorFlowDataRes(res: WorkFlowDataRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            mDataList = res.body!!.steps
            setStep(currentPosition)
        }
    }

    private fun setStep(position: Int) {
        if (mDataList != null && mDataList!!.isNotEmpty() && mDataList!!.size > position) {
            currentPosition = position
            tv_nums.text = "" + (position + 1) + "."
            tv_step_name.text = mDataList?.get(position)?.name
            mTemplateList = mDataList?.get(position)?.templates
            workFlowAdapter!!.addList(mTemplateList)
        }
    }

    override fun showAddTextRes(res: AddTextRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            toast(res.message!!)
            init()
        } else {
            toast(res.message!!)
        }
    }

    override fun showWorkFlowDataRes(res: AddTextRes) {
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (res.success!!) {
            toast(res.message!!)
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

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_ERROR)
        error_textView.text = throwable.message.toString()
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {
        if (any is WorkFlowDataRes.WorkFlowDataResBody.Step.Template.Element && type is View) {
            when (op) {
                Constants.ELEMENT_TEXT -> {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    workFlowPresenter.addText(addTextIp = AddTextIp(elementId = any.id, jobId = jobId, text = (type.et_add_text.text.toString())))
                }
                Constants.ELEMENT_IMAGE -> {
                    image = type.btn_add_image
                    upload_image = type.btn_upload_image
                    elementId = any.id!!
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST)
                        } else {
                            startCamera()
                        }
                    } else {
                        Toast.makeText(baseContext, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                    }
                }
                Constants.ELEMENT_UPLOAD_IMAGE -> {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    if (imgpath.isNotEmpty()) {
                        val file = File(imgpath)
                        val compressedImgFile: File = Compressor(this).compressToFile(file)
                        workFlowPresenter.addImage(jobid = jobId!!, elementId = elementId, file = compressedImgFile)
                    }
                }
                Constants.ELEMENT_DROPDOWN -> {
                    showViewState(MultiStateView.VIEW_STATE_LOADING)
                    type.et_add_text.setText(type.spinner_center.selectedItem.toString())
                    workFlowPresenter.addText(addTextIp = AddTextIp(elementId = any.id, jobId = jobId, text = (type.spinner_center.selectedItem.toString())))
                }
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
        val path = mContext.getExternalFilesDir(null)!!.absolutePath
        val dir = File(path, "DP Partner 2.0/Image")
        if (!dir.exists())
            dir.mkdirs()
        val photoURI = FileProvider.getUriForFile(context, "com.dpdelivery.android.provider",
                File(dir.absolutePath, PHOTO_FILE_NAME))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {

            var f = File(mContext.getExternalFilesDir(null)!!.absolutePath)
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
            MediaScannerConnection.scanFile(context, arrayOf<String>(dir.absolutePath), null) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }

            if (!dir.exists()) {

                Toast.makeText(baseContext, "Error while capturing image", Toast.LENGTH_LONG).show()

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
                            ExifInterface.ORIENTATION_NORMAL)

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
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                image!!.setImageBitmap(bitmap)
                if (imgpath.isNotEmpty()) {
                    upload_image!!.visibility = View.VISIBLE
                }
                CommonUtils.setUserImagebitmap(mContext, image!!, stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun showFinishJobRes(res: SubmiPidRes) {
        if (res.success) {
            startActivity(Intent(this, TechJobsListActivity::class.java))
            finish()
        } else {
            toast(res.message)
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
        }
    }

    override fun onBackPressed() {
        if (currentPosition != 0) {
            setStep(currentPosition - 1)
            btn_next.visibility = View.VISIBLE
            btn_Finish.visibility = View.GONE
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (currentPosition != 0) {
                    setStep(currentPosition - 1)
                    btn_next.visibility = View.VISIBLE
                    btn_Finish.visibility = View.GONE
                }
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                }
                this.doubleBackToExitPressedOnce = true
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}