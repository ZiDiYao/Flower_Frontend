package com.zidi.flowidentification_demo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zidi.flowidentification_demo.Util.PathUtil
import com.zidi.flowidentification_demo.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PreviewActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private val permissionRequestCode = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val imageView = findViewById<ImageView>(R.id.image_preview_final)
        val btnUpload = findViewById<Button>(R.id.btn_upload_flower)
        val btnBack = findViewById<Button>(R.id.btn_back)

        // 接收图片 URI
        val uriStr = intent.getStringExtra("image_uri")
        imageUri = Uri.parse(uriStr)
        imageView.setImageURI(imageUri)

        // 返回按钮
        btnBack.setOnClickListener { finish() }

        // 上传按钮
        btnUpload.setOnClickListener {
            checkPermissionThenUpload()
        }
    }

    private fun checkPermissionThenUpload() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
        } else {
            imageUri?.let { uploadImageToServer(it) }
        }
    }

    // 动态权限回调
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imageUri?.let { uploadImageToServer(it) }
        } else {
            Toast.makeText(this, "Fuck it !!! NO Right", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToServer(uri: Uri) {
        val contentResolver = contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            Toast.makeText(this, "Fuck it !!! Failed to open image stream", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType? {
                return MediaType.parse("image/*")
            }

            override fun writeTo(sink: okio.BufferedSink) {
                inputStream.use {
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (it.read(buffer).also { bytesRead = it } != -1) {
                        sink.write(buffer, 0, bytesRead)
                    }
                }
            }
        }

        val fileName = "upload_${System.currentTimeMillis()}.jpg"
        val multipart = MultipartBody.Part.createFormData("image", fileName, requestBody)

        RetrofitClient.getInstance().uploadApi.uploadImage(multipart)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PreviewActivity, "Yes !!! Upload successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@PreviewActivity,
                            "Fuck it !!! Upload failed, server responded: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@PreviewActivity,
                        "⚠Fuck it !!! Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("UPLOAD_DEBUG", "Upload failed", t)
                }
            })
    }

}
