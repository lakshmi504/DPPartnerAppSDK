package com.dpdelivery.android.screens.photo

import android.Manifest
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dpdelivery.android.R
import com.dpdelivery.android.commonviews.MultiStateView
import com.dpdelivery.android.constants.Constants
import com.dpdelivery.android.model.techres.UploadPhotoRes
import com.dpdelivery.android.screens.base.TechBaseActivity
import com.dpdelivery.android.screens.login.LoginActivity
import com.dpdelivery.android.utils.CommonUtils
import com.dpdelivery.android.utils.SharedPreferenceManager
import com.dpdelivery.android.utils.toast
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.app_bar_tech_base.*
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject

class ImageActivity : TechBaseActivity(), View.OnClickListener, ImageContract.View {

    lateinit var mContext: Context
    private val CAMERA_REQUEST = 0
    private val PHOTO_FILE_NAME = UUID.randomUUID().toString() + ".jpeg"
    private lateinit var bitmap: Bitmap
    private var imgpath: String = ""
    private var jobId: Int = 0
    lateinit var dialog: Dialog

    @Inject
    lateinit var presenter: ImagePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(context).inflate(R.layout.activity_image, tech_layout_container)
        init()
    }

    override fun init() {
        mContext = this
        setTitle("Upload Photo")
        if (intent != null) {
            jobId = intent.getIntExtra(Constants.ID, 0)
        }
        dialog = CommonUtils.progressDialog(context)
        showBack()
        setUpBottomNavView(false)
        btn_capture.setOnClickListener(this)
        btn_upload.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_capture -> {
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
            R.id.btn_upload -> {
                if (imgpath.isNotEmpty()) {
                    dialog.show()
                    val file = File(imgpath)
                    val compressedImgFile: File = Compressor(this).compressToFile(file)
                    presenter.uploadPhoto(jobId, compressedImgFile)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
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
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                image.setImageBitmap(bitmap)
                if (imgpath.isNotEmpty()) {
                    btn_upload.visibility = View.VISIBLE
                    btn_capture.visibility = View.GONE
                }
                CommonUtils.setUserImagebitmap(mContext, image, stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun showUploadPhotoRes(uploadPhotoRes: UploadPhotoRes) {
        dialog.dismiss()
        if (uploadPhotoRes.success) {
            btn_upload.visibility = View.GONE
            upload_message.visibility = View.VISIBLE
            CommonUtils.saveTransactionImageName(uploadPhotoRes.message)
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
        showViewState(MultiStateView.VIEW_STATE_CONTENT)
        if (throwable is HttpException) {
            when (throwable.code()) {
                403 -> {
                    SharedPreferenceManager.clearPreferences()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                else -> {
                    showViewState(MultiStateView.VIEW_STATE_ERROR)
                    toast(throwable.message.toString())
                }
            }
        } else {
            toast(throwable.message.toString())
        }
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun showViewState(state: Int) {
        multistateview.viewState = state
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}