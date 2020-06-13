package com.dpdelivery.android.ui.photo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.UploadPhotoRes
import com.dpdelivery.android.ui.base.BaseActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.toast
import kotlinx.android.synthetic.main.activity_photos.*
import kotlinx.android.synthetic.main.app_bar_base.*
import kotlinx.android.synthetic.main.error_view.*
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class PhotosActivity : BaseActivity(), View.OnClickListener, PhotoContract.View {

    lateinit var mContext: Context
    private lateinit var bitmap: Bitmap
    private val PHOTO_FILE_NAME = UUID.randomUUID().toString() + ".jpeg"
    private var imgpath: String = ""
    private val CAMERA_REQUEST = 0

    @Inject
    lateinit var presenter: PhotoPresenter
    private var job_id: Int? = 0
    private var source: String? = null
    lateinit var dialog: Dialog

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LayoutInflater.from(baseContext).inflate(R.layout.activity_photos, layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setUpBottomNavView(false)
        if (intent != null) {
            job_id = intent.getIntExtra(Constants.ID, 0)
            source = intent.getStringExtra(Constants.SOURCE)
        }
        showBack()
        textdata.text = source
        dialog = CommonUtils.progressDialog(context)
        btn_capture.setOnClickListener(this)
        btn_upload.setOnClickListener(this)
        error_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_capture -> {
                if (Build.VERSION.SDK_INT >= 17) {
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
            R.id.btn_upload -> {
                if (imgpath.isNotEmpty()) {
                    dialog.show()
                    val file = File(imgpath)
                    presenter.uploadPhoto(job_id!!, file)
                }
            }
            R.id.error_button -> {
                init()
            }

        }
    }

    private fun startCamera() {
        val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
        val path = mContext.getExternalFilesDir(null)!!.absolutePath
        val dir = File(path, "DPDelivery/Image")
        if (!dir.exists())
            dir.mkdirs()
        val photoURI = FileProvider.getUriForFile(context, "com.dpdelivery.android.provider",
                File(dir.absolutePath, PHOTO_FILE_NAME))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {

            var f = File(mContext.getExternalFilesDir(null)!!.absolutePath)
            var dir = File(f, "DPDelivery/Image")
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
                image.setImageBitmap(bitmap)
                if (imgpath.isNotEmpty()) {
                    btn_upload.visibility = View.VISIBLE
                }
                CommonUtils.setUserImagebitmap(mContext, image, stream)
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

    override fun showUploadRes(uploadPhotoRes: UploadPhotoRes) {
        dialog.dismiss()
        if (uploadPhotoRes.success) {
            btn_upload.visibility = View.GONE
            upload_message.visibility = View.VISIBLE
            if (source == getString(R.string.upload_images)) {
                CommonUtils.saveTransactionImageName(uploadPhotoRes.message)
            } else if (source == getString(R.string.upload_delivered_image)) {
                CommonUtils.saveDeliveredImageName(uploadPhotoRes.message)
            }
            Handler().postDelayed(Runnable {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }, 2000)
        } else {
            toast(uploadPhotoRes.message)
        }
    }

    override fun showErrorMsg(throwable: Throwable, apiType: String) {
        showViewState(MultiStateView.VIEW_STATE_ERROR)
        error_textView.text = throwable.message ?: getString(R.string.error_something_wrong)
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
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
}
