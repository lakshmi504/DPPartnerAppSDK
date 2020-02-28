package com.dpdelivery.android.ui.photo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dpdelivery.android.R
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_photos.*
import kotlinx.android.synthetic.main.app_bar_base.*

class PhotosActivity : BaseActivity(), View.OnClickListener {

    lateinit var mContext: Context
    var resultImageString: String = ""
    private lateinit var bitmap: Bitmap
    private val RequestPermissionCode = 1

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_photos, layout_container)
        init()
    }

    private fun init() {
        mContext = this
        enableRuntimePermissionToAccessCamera()
        btn_capture.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_capture -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, 7)
                }
            }
        }
    }

    private fun enableRuntimePermissionToAccessCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) { // Printing toast message after enabling runtime permission.
            Toast.makeText(this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), RequestPermissionCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 7 && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            bitmap = extras!!["data"] as Bitmap
            image.setImageBitmap(bitmap)
            GetBase64Image(bitmap).execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetBase64Image(private var bitmap: Bitmap) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg voids: Void): String {
            return CommonUtils.convertToBase64(bitmap)
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            resultImageString = s
            if (resultImageString.isNotEmpty()) {
                btn_upload.visibility = View.VISIBLE
            }

        }
    }

    override fun onRequestPermissionsResult(RC: Int, per: Array<String>, PResult: IntArray) {
        when (RC) {
            RequestPermissionCode -> if (PResult.isNotEmpty() && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
