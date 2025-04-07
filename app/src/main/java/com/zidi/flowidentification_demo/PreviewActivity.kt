package com.zidi.flowidentification_demo

import android.Manifest
import android.content.Intent
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
import com.zidi.flowidentification_demo.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreviewActivity : AppCompatActivity() {

    // Holds the selected image URI
    private var imageUri: Uri? = null
    private val permissionRequestCode = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val imageView = findViewById<ImageView>(R.id.image_preview_final)
        val btnUpload = findViewById<Button>(R.id.btn_upload_flower)
        val btnBack = findViewById<Button>(R.id.btn_back)

        // Get image URI from intent
        val uriStr = intent.getStringExtra("image_uri")
        if (uriStr == null) {
            Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        imageUri = Uri.parse(uriStr)
        imageView.setImageURI(imageUri)  // Preview image on screen

        // Back button returns to previous screen
        btnBack.setOnClickListener {
            finish()
        }

        // Upload button triggers permission check and upload
        btnUpload.setOnClickListener {
            checkPermissionThenUpload()
        }
    }

    // Check storage permission before uploading image
    private fun checkPermissionThenUpload() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // If not granted, request permission
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
        } else {
            // If permission already granted, upload image
            imageUri?.let { uploadImageToServer(it) }
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            imageUri?.let { uploadImageToServer(it) }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    // Upload image to Spring Boot backend using Retrofit
    private fun uploadImageToServer(uri: Uri) {
        val contentResolver = contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Toast.makeText(this, "Failed to open image stream", Toast.LENGTH_SHORT).show()
            return
        }

        // Get MIME type of the selected image
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

        // Create a RequestBody manually to read and stream the file
        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType? = MediaType.parse(mimeType)

            override fun writeTo(sink: BufferedSink) {
                inputStream.use {
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (it.read(buffer).also { bytesRead = it } != -1) {
                        sink.write(buffer, 0, bytesRead)
                    }
                }
            }
        }

        // Generate a unique image filename with timestamp
        val fileName = "upload_${System.currentTimeMillis()}.jpg"

        // Build multipart form data
        val multipart = MultipartBody.Part.createFormData("image", fileName, requestBody)

        // Send request via Retrofit
        RetrofitClient.getInstance().uploadApi.uploadImage(multipart)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val fileName = response.body()?.string() ?: "unknown.jpg"

                        // To pass the real image name to TextInputActivity
                        val intent = Intent(this@PreviewActivity, TextInputActivity::class.java)
                        intent.putExtra("image_name", fileName)  // 👈 using fileName instead of imageUri
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@PreviewActivity, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@PreviewActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("UPLOAD_DEBUG", "Upload failed", t)
                }
            })
    }
}
