package com.dpdelivery.android.ui.deliveryjob

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpdelivery.android.R
import com.dpdelivery.android.commonadapter.BasicAdapter
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.interfaces.IAdapterClickListener
import com.dpdelivery.android.interfaces.SelectedDateListener
import com.dpdelivery.android.model.*
import com.dpdelivery.android.model.input.AssignJobsIp
import com.dpdelivery.android.model.input.UpdateAppointmentIp
import com.dpdelivery.android.model.input.UpdateStatusIp
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.ui.deliveryjoblist.DeliveryJobListActivity
import com.dpdelivery.android.ui.location.MapLocationActivity
import com.dpdelivery.android.ui.photo.PhotosActivity
import com.dpdelivery.android.utils.*
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_delivery_job.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.layout_header.*
import okhttp3.ResponseBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DeliveryJobActivity : BaseActivity(), View.OnClickListener, DeliveryJobContract.View,
        SelectedDateListener, IAdapterClickListener, AdapterView.OnItemSelectedListener {

    lateinit var mContext: Context
    private var job_id: Int? = 0

    @Inject
    lateinit var presenter: DeliveryJobPresenter
    private var appointmentDialog: Dialog? = null
    lateinit var calendar: Calendar
    private var CalendarHour: Int = 0
    private var CalendarMinute: Int = 0
    lateinit var timepickerdialog: TimePickerDialog
    private var modeSpin: Spinner? = null
    private var paymentmodeSpin: Spinner? = null
    private var layout_location: RelativeLayout? = null
    private var btn_upload_images: AppCompatTextView? = null
    private var btn_upload_purifier_image: AppCompatTextView? = null
    private var mode: String? = null
    private var paymentmode: String? = null
    private var phone: String? = null
    private var payamount: EditText? = null
    private var resPayMode: String? = null
    private var altphone: String? = null
    private var status_code: String? = null
    lateinit var manager: LinearLayoutManager
    lateinit var adapterNotesList: BasicAdapter
    private var amount: Double? = null
    internal val LOCATION_REQUEST_CODE = 6
    internal val PHOTO_REQUEST_CODE = 1
    internal val PIC_REQUEST_CODE = 2
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var address: String? = null
    private var locationTxt: TextView? = null
    internal var isLocationSet: Boolean = false
    private var dialog: Dialog? = null
    private var agentsList: List<GetAgentsRes>? = null
    private var selected_id = ArrayList<String>()
    private var agentId: String? = null
    private var TAG = "DeliveryJobActivity"
    private var note_list: ArrayList<Note?>? = null
    private val statusMode: Array<String> = arrayOf<String>("Select Status", "New", "Assigned", "Picked-Up", "In-Progress", "Delayed", "On-Hold", "Rejected", "Delivered")
    private val paymentMode: Array<String> = arrayOf<String>("Payment Type", "Cash", "Card", "QR Scan-GPay", "QR Scan-PhonePe", "QR Scan-PayTm", "QR Scan-BHIM UPI", "Payment Link", "Cheque")

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_delivery_job, layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Delivery Job Details")
        setUpBottomNavView(false)
        toolbar_title.textSize = 18f
        empty_button.setOnClickListener(this)
        error_button.setOnClickListener(this)
        btn_update_appointment.setOnClickListener(this)
        btn_update_appointment.setDrawableLeft(R.drawable.ic_appointment)
        btn_update_status.setOnClickListener(this)
        btn_update_status.setDrawableLeft(R.drawable.ic_edit)
        iv_home.setOnClickListener(this)
        btn_note.setOnClickListener(this)
        iv_home.setDrawableLeft(R.drawable.ic_home)
        btn_note.setDrawableLeft(R.drawable.ic_note)
        iv_call.setOnClickListener(this)
        iv_alt_call.setOnClickListener(this)
        tv_view_notes.setOnClickListener(this)
        showBack()
        if (intent != null) {
            job_id = intent.getIntExtra(Constants.ID, 0)
        }

        getDetailJob()
    }

    private fun getDetailJob() {
        showViewState(MultiStateView.VIEW_STATE_LOADING)
        Handler().postDelayed(Runnable {
            if (job_id != 0) {
                presenter.getDeliveryJob(job_id!!)
                selected_id.add(job_id.toString())
            }
        }, 2000)

    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.error_button -> {
                init()
            }
            R.id.empty_button -> {
                init()
            }
            R.id.btn_update_status -> {
                showUpdateStatus()  // update status
            }
            R.id.btn_update_appointment -> {
                showUpdateAppointment() // update appointment
            }
            R.id.iv_home -> {
                val intent = Intent(this, DeliveryJobListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
            R.id.iv_call -> { //call function
                if (phone!!.isNotEmpty()) {
                    val url = "tel:$phone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                }
            }
            R.id.iv_alt_call -> {  // for call function(alt number)
                if (altphone!!.isNotEmpty()) {
                    val url = "tel:$altphone"
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                }
            }
            R.id.btn_note -> {
                showUpdateNote()  // for update note
            }
            R.id.tv_view_notes -> {
                if (note_list!!.isNotEmpty()) {
                    showNotesList()   // for showing notes list
                } else {
                    toast("No Notes Found")
                }
            }

        }
    }

    private fun showUpdateAppointment() {
        appointmentDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        appointmentDialog!!.setContentView(R.layout.layout_update_appointment)
        appointmentDialog!!.setCancelable(true)
        (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).setDrawableRight(R.drawable.ic_calender)
        (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).setDrawableRight(R.drawable.ic_time)
        appointmentDialog!!.show()
        (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).setOnClickListener {
            CommonUtils.showdatepicker(mContext, this, 0, "")
        }
        (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).setOnClickListener {
            calendar = Calendar.getInstance()
            CalendarHour = calendar.get(Calendar.HOUR_OF_DAY)
            CalendarMinute = calendar.get(Calendar.MINUTE)
            timepickerdialog = TimePickerDialog(mContext,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        val time = "$hourOfDay:$minute"
                        val input = SimpleDateFormat("HH:mm", Locale.ROOT)
                        val output = SimpleDateFormat("hh:mm aa", Locale.ROOT)

                        var d: Date? = null
                        try {
                            d = input.parse(time)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        val formatted = output.format(d!!)

                        (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).setText(formatted).toString()
                        (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).setSelection((appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).length())
                    }, CalendarHour, CalendarMinute, false)
            timepickerdialog.show()
        }
        (appointmentDialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            appointmentDialog!!.dismiss()
        }

        (appointmentDialog!!.findViewById(R.id.btn_update) as AppCompatButton).setOnClickListener {
            if ((appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).text!!.isNotEmpty()) {
                if ((appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).text!!.isNotEmpty()) {

                    val appointmentTime = (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).text.toString() + " " +
                            (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).text.toString()

                    val input = SimpleDateFormat("EEE, d-MMM-yyyy hh:mm aa", Locale.ROOT)
                    val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                    output.timeZone = TimeZone.getTimeZone("GMT")

                    var d: Date? = null
                    try {
                        d = input.parse(appointmentTime)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val formatted = output.format(d!!)
                    presenter.updateAppointment(updateAppointmentIp = UpdateAppointmentIp(jobId = job_id.toString(),
                            appt = formatted,
                            note = (appointmentDialog!!.findViewById(R.id.et_note) as EditText).text.toString()))
                    appointmentDialog!!.dismiss()
                    init()
                } else {
                    toast("Please select time")
                }
            } else {
                toast("Please select date")
            }

        }

        (appointmentDialog!!.findViewById(R.id.btn_reset) as AppCompatButton).setOnClickListener {
            (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).setText("")
            (appointmentDialog!!.findViewById(R.id.et_appointment_time) as TextInputEditText).setText("")
            (appointmentDialog!!.findViewById(R.id.et_note) as EditText).setText("")
        }

    }

    override fun showUpdateAppointmntRes(responseBody: ResponseBody) {
        if (responseBody.string().isNotEmpty()) {
            toast("Appointment updated successfully.")
        }
    }

    private fun showUpdateStatus() {
        appointmentDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        appointmentDialog!!.setContentView(R.layout.layout_update_status)
        appointmentDialog!!.setCancelable(true)
        modeSpin = appointmentDialog!!.findViewById(R.id.sp_mode)
        paymentmodeSpin = appointmentDialog!!.findViewById(R.id.sp_payment)
        layout_location = appointmentDialog!!.findViewById(R.id.rl_location)
        btn_upload_images = appointmentDialog!!.findViewById(R.id.btn_upload_trans_image)
        btn_upload_purifier_image = appointmentDialog!!.findViewById(R.id.btn_upload_purifier_image)
        payamount = appointmentDialog!!.findViewById(R.id.et_amount)
        locationTxt = appointmentDialog!!.findViewById(R.id.tv_location)
        (appointmentDialog!!.findViewById(R.id.iv_location) as AppCompatImageView).setOnClickListener {
            val intent = Intent(context, MapLocationActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }
        btn_upload_images!!.setDrawableLeft(R.drawable.ic_upload)
        btn_upload_purifier_image!!.setDrawableLeft(R.drawable.ic_upload)
        btn_upload_images!!.setOnClickListener {
            val intent = Intent(context, PhotosActivity::class.java)
            intent.putExtra(Constants.ID, job_id)
            intent.putExtra(Constants.SOURCE, btn_upload_images!!.text)
            startActivityForResult(intent, PHOTO_REQUEST_CODE)
        }
        btn_upload_purifier_image!!.setOnClickListener {
            val intent = Intent(context, PhotosActivity::class.java)
            intent.putExtra(Constants.ID, job_id)
            intent.putExtra(Constants.SOURCE, btn_upload_purifier_image!!.text)
            startActivityForResult(intent, PIC_REQUEST_CODE)
        }
        loadDefaultSpinner()
        loadPaymentSpinner()
        (appointmentDialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            appointmentDialog!!.dismiss()
        }

        (appointmentDialog!!.findViewById(R.id.btn_status_update) as AppCompatButton).setOnClickListener {

            if (mode != "Select Status" && mode != "DEL") {
                presenter.updateStatus(updateStatusIp = UpdateStatusIp(jobId = job_id,
                        status = mode,
                        note = (appointmentDialog!!.findViewById(R.id.et_status_note) as EditText).text.toString()))
                appointmentDialog!!.dismiss()
                init()
            } else if (mode == "DEL" && payamount!!.text.toString() != "0") {
                if (locationTxt!!.text.isNotEmpty()) {
                    if (payamount!!.text.isNotEmpty()) {
                        if (paymentmode != "Payment Type") {
                            if (CommonUtils.getTransactionImageName().isNotEmpty()) {
                                if (CommonUtils.getDeliveredImageName().isNotEmpty()) {
                                    presenter.updateStatus(updateStatusIp = UpdateStatusIp(jobId = job_id,
                                            status = mode,
                                            note = (appointmentDialog!!.findViewById(R.id.et_status_note) as EditText).text.toString(),
                                            payAmount = payamount!!.text.toString(),
                                            payType = paymentmode,
                                            latLong = "$latitude,$longitude",
                                            payImage = CommonUtils.getTransactionImageName(),
                                            deliveryImage = CommonUtils.getDeliveredImageName()))
                                    appointmentDialog!!.dismiss()
                                    init()
                                } else {
                                    toast("Please Upload Delivered Image")
                                }
                            } else {
                                toast("Please Upload Payment Image")
                            }
                        } else {
                            toast("Please select payment type")
                        }
                    } else {
                        toast("Please select Amount")
                    }
                } else {
                    toast("Please select location")
                }
            } else if (mode == "DEL" && payamount!!.text.toString() == "0") {
                if (locationTxt!!.text.isNotEmpty()) {
                    if (payamount!!.text.isNotEmpty()) {
                        if (CommonUtils.getTransactionImageName().isNotEmpty()) {
                            if (CommonUtils.getDeliveredImageName().isNotEmpty()) {
                                presenter.updateStatus(updateStatusIp = UpdateStatusIp(jobId = job_id,
                                        status = mode,
                                        note = (appointmentDialog!!.findViewById(R.id.et_status_note) as EditText).text.toString(),
                                        payAmount = payamount!!.text.toString(),
                                        latLong = "$latitude,$longitude",
                                        payImage = CommonUtils.getTransactionImageName(),
                                        deliveryImage = CommonUtils.getDeliveredImageName()))
                                appointmentDialog!!.dismiss()
                                init()
                            } else {
                                toast("Please Upload Delivered Image")
                            }
                        } else {
                            toast("Please Upload Payment Image")
                        }
                    } else {
                        toast("Please select Amount")
                    }
                } else {
                    toast("Please select location")
                }
            }
        }
        (appointmentDialog!!.findViewById(R.id.btn_status_reset) as AppCompatButton).setOnClickListener {
            loadDefaultSpinner()
            (appointmentDialog!!.findViewById(R.id.et_status_note) as EditText).setText("")
        }
        appointmentDialog!!.show()
    }

    private fun loadDefaultSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusMode)
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modeSpin!!.adapter = adapterMode
        modeSpin!!.onItemSelectedListener = this
    }

    private fun loadPaymentSpinner() {
        val adapterMode = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentMode)
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentmodeSpin!!.adapter = adapterMode
        paymentmodeSpin!!.onItemSelectedListener = this
    }

    override fun showUpdateStatusRes(responseBody: ResponseBody) {
        if (responseBody.string().isNotEmpty()) {
            toast("Updated Status/Note successfully.")
            CommonUtils.saveTransactionImageName("")
            CommonUtils.saveDeliveredImageName("")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            R.id.sp_mode -> {
                mode = modeSpin!!.selectedItem.toString()

                when (mode) {
                    "New" -> {
                        mode = "NEW"
                    }
                    "In-Progress" -> {
                        mode = "INP"
                    }
                    "On-Hold" -> {
                        mode = "HLD"
                    }
                    "Assigned" -> {
                        mode = "ASG"
                    }
                    "Picked-Up" -> {
                        mode = "PKU"
                    }
                    "Delayed" -> {
                        mode = "DLY"
                    }
                    "Rejected" -> {
                        mode = "REJ"
                    }
                    "Delivered" -> {
                        mode = "DEL"
                    }
                }
                if (mode == "DEL") {
                    payamount!!.visibility = View.VISIBLE
                    payamount!!.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(editable: Editable?) {
                            try {
                                val amount = payamount!!.text.toString()
                                val intAmount = Integer.valueOf(amount)
                                if (intAmount <= 0) {
                                    paymentmodeSpin!!.visibility = View.GONE
                                } else {
                                    paymentmodeSpin!!.visibility = View.VISIBLE
                                }
                            } catch (e: NumberFormatException) {
                                loge("Not a number")
                            }
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            paymentmodeSpin!!.visibility = View.GONE
                        }
                    })
                    layout_location!!.visibility = View.VISIBLE
                    btn_upload_images!!.visibility = View.VISIBLE
                    btn_upload_purifier_image!!.visibility = View.VISIBLE

                } else {
                    paymentmodeSpin!!.visibility = View.GONE
                    payamount!!.visibility = View.GONE
                    layout_location!!.visibility = View.GONE
                    btn_upload_images!!.visibility = View.GONE
                    btn_upload_purifier_image!!.visibility = View.GONE

                }
            }
            R.id.sp_payment -> {
                paymentmode = paymentmodeSpin!!.selectedItem.toString()

                when (paymentmode) {
                    "Cash" -> {
                        paymentmode = "CSH"
                    }
                    "Card" -> {
                        paymentmode = "CRD"
                    }
                    "QR Scan-GPay" -> {
                        paymentmode = "QGP"
                    }
                    "QR Scan-PhonePe" -> {
                        paymentmode = "QPP"
                    }
                    "QR Scan-PayTm" -> {
                        paymentmode = "QPM"
                    }
                    "QR Scan-BHIM UPI" -> {
                        paymentmode = "QBH"
                    }
                    "Payment Link" -> {
                        paymentmode = "LNK"
                    }
                    "Cheque" -> {
                        paymentmode = "CHK"
                    }
                }
            }
            R.id.sp_agent_name -> {
                agentId = agentsList?.get(position)!!.id.toString()
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
            locationTxt!!.text = address
            locationTxt!!.setTextColor(ContextCompat.getColor(context, R.color.colorBlacklight))
            isLocationSet = true
        } else if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //iv_upload_image!!.visibility = View.VISIBLE
            btn_upload_images!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            btn_upload_images!!.setDrawableLeft(R.drawable.ic_success)
        } else if (requestCode == PIC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //iv_purifier_images!!.visibility = View.VISIBLE
            btn_upload_purifier_image!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            btn_upload_purifier_image!!.setDrawableLeft(R.drawable.ic_success)
        }
    }

    private fun showUpdateNote() {
        appointmentDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        appointmentDialog!!.setContentView(R.layout.layout_add_note)
        appointmentDialog!!.setCancelable(true)
        (appointmentDialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            appointmentDialog!!.dismiss()
        }
        (appointmentDialog!!.findViewById(R.id.btn_submit) as AppCompatButton).setOnClickListener {
            if ((appointmentDialog!!.findViewById(R.id.et_note) as EditText).text!!.isNotEmpty()) {
                presenter.updateStatus(updateStatusIp = UpdateStatusIp(jobId = job_id,
                        status = status_code,
                        note = (appointmentDialog!!.findViewById(R.id.et_note) as EditText).text.toString()))
                appointmentDialog!!.dismiss()
                init()
            } else {
                toast("Note Should not be empty")
            }
        }
        appointmentDialog!!.show()
    }

    override fun showDeliveryJobRes(res: DeliveryJobsRes) {
        if (res != null) {
            showViewState(MultiStateView.VIEW_STATE_CONTENT)
            tv_job_id.text = res.id.toString()
            tv_job_type.text = res.type!!.description
            tv_name.text = res.custName
            tv_phone.text = res.custPhone
            phone = res.custPhone
            tv_alt_phone.text = res.custAltPhone
            altphone = res.custAltPhone
            tv_email.text = res.custEmail
            tv_address.text = res.custAddress
            tv_area.text = res.custArea
            tv_status.text = res.status?.description
            status_code = res.status?.code
            note_list = res.notes
            tv_view_notes.visibility = View.VISIBLE
            if (res.status?.description == "Delivered") {
                btn_update_appointment.isEnabled = false
                btn_update_status.setDrawableLeft(R.drawable.ic_edit_light)
                btn_update_appointment.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
                btn_update_status.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
                btn_update_status.isEnabled = false
                btn_update_appointment.setDrawableLeft(R.drawable.ic_appointment_light)

            } else {
                btn_update_appointment.isEnabled = true
                btn_update_status.isEnabled = true
            }
            if (res.payImage.isNullOrBlank() && res.deliveryImage.isNullOrBlank()) {
                btn_view_images.visibility = View.GONE
            } else {
                btn_view_images.visibility = View.VISIBLE
            }
            btn_view_images.setOnClickListener {
                if (!res.payImage.isNullOrBlank() && !res.deliveryImage.isNullOrBlank()) {
                    val intent = Intent(this, ImagesActivity::class.java)
                    intent.putExtra(Constants.PAYMENT_IMAGE, res.payImage)
                    intent.putExtra(Constants.DELIVERED_IMAGE, res.deliveryImage)
                    startActivity(intent)
                } else {
                    toast("No Images Found")
                }
            }
            if (!res.appointmentAt.isNullOrEmpty()) {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT)
                val output = SimpleDateFormat("EEE, d-MMM-yyyy hh:mm:ss a", Locale.ROOT)
                input.timeZone = TimeZone.getTimeZone("IST")
                var d: Date? = null
                try {
                    d = input.parse(res.appointmentAt)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val formatted = output.format(d!!)
                tv_appointment.text = formatted
            } else {
                tv_appointment.text = res.appointmentAt
            }
            if (CommonUtils.getRole() != "ROLE_DeliveryPerson") {
                tv_agent_assigned.text = res.assignedTo?.name
                tv_agent_assigned.visibility = View.VISIBLE
                tv_agent.visibility = View.VISIBLE
                if (res.assignedTo?.name.isNullOrEmpty()) {
                    btn_assign_agent.visibility = View.VISIBLE
                    btn_assign_agent.text = getString(R.string.assign)
                    btn_assign_agent.setOnClickListener {
                        showAgentsList()
                    }
                } else {
                    btn_assign_agent.visibility = View.VISIBLE
                    btn_assign_agent.text = getString(R.string.re_assign)
                    btn_assign_agent.setOnClickListener {
                        showAgentsList()
                    }
                    if (res.status?.description!!.contentEquals("Delivered")) {
                        btn_assign_agent.visibility = View.GONE
                    }
                }
            } else {
                tv_agent_assigned.visibility = View.GONE
                tv_agent.visibility = View.GONE
                btn_assign_agent.visibility = View.GONE
            }
            tv_payment_type.text = res.payType?.description
            resPayMode = res.payType?.code
            amount = res.payAmount
            tv_amount.text = amount?.toInt().toString()
        }
    }

    private fun showAgentsList() {
        dialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        dialog!!.setContentView(R.layout.layout_assign_agent)
        dialog!!.setCancelable(true)
        modeSpin = dialog!!.findViewById(R.id.sp_agent_name)
        presenter.getAgentsList()
        (dialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            dialog!!.dismiss()
        }
        (dialog!!.findViewById(R.id.btn_agent_reset) as AppCompatButton).setOnClickListener {
            presenter.getAgentsList()
        }
        (dialog!!.findViewById(R.id.btn_agent_update) as AppCompatButton).setOnClickListener {
            presenter.assignJob(assignJobsIp = AssignJobsIp(jobIds = selected_id, assignTo = agentId))
            dialog!!.dismiss()
            init()
        }
        dialog!!.show()
    }


    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_ERROR)
        toast(throwable.message ?: getString(R.string.error_something_wrong))
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun setSelectedDate(date: String, s: String) {
        val appointmentDate = date
        val input = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val output = SimpleDateFormat("EEE, d-MMM-yyyy", Locale.ROOT)
        var d: Date? = null
        try {
            d = input.parse(appointmentDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted = output.format(d!!)
        (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).setText(formatted).toString()
        (appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).setSelection((appointmentDialog!!.findViewById(R.id.et_appointment_date) as TextInputEditText).length())

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

    private fun showNotesList() {
        appointmentDialog = Dialog(context, R.style.CustomDialogThemeLightBg)
        appointmentDialog!!.setContentView(R.layout.layout_note_list)
        appointmentDialog!!.setCancelable(true)
        (appointmentDialog!!.findViewById(R.id.btn_close) as ImageView).setOnClickListener {
            appointmentDialog!!.dismiss()
        }
        manager = LinearLayoutManager(this)
        (appointmentDialog!!.findViewById(R.id.rv_notes_list) as RecyclerView).layoutManager = manager
        adapterNotesList = BasicAdapter(this, R.layout.item_notes_list, adapterClickListener = this)
        (appointmentDialog!!.findViewById(R.id.rv_notes_list) as RecyclerView).apply {
            adapter = adapterNotesList
            note_list.withNotNullNorEmpty {
                adapterNotesList.addList(note_list!!)
            }
        }
        appointmentDialog!!.show()
    }

    override fun onclick(any: Any, pos: Int, type: Any, op: String) {

    }

    override fun showAssignJobsRes(res: AssignJobRes) {
        if (res.success!!) {
            toast("Job Assigned Successfully")
        }
    }

    override fun showAgentsListRes(res: List<GetAgentsRes>) {
        if (res.isNotEmpty()) {
            agentsList = res
            val mAdapter = ArrayAdapter<GetAgentsRes>(this, android.R.layout.simple_spinner_item, agentsList!!)
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            modeSpin!!.adapter = mAdapter
            modeSpin!!.onItemSelectedListener = this
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
