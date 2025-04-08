package com.zidi.flowidentification_demo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zidi.flowidentification_demo.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.RequestBody
import com.zidi.flowidentification_demo.model.FlowerDescriptionRequest as FlowerDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class TextInputActivity : AppCompatActivity() {

    private lateinit var imageName: String
    private lateinit var imageUriString: String
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var email: String
//    private val imageUriString = intent.getStringExtra("image_uri")
//    private val imageUri = imageUriString?.let { Uri.parse(it) }
//    private val imageFile: File? = imageUriString?.let {
//        val uri = Uri.parse(it)
//        File(getRealPathFromUri(this, uri)) // only runs if imageUriString is not null
//    }

    private var imageUri: Uri? = null
    private lateinit var color : String
    private lateinit var petals : String
    private lateinit var smell : String
    private lateinit var location : String

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_USERNAME = "username"
        private const val TAG = "UPLOAD_DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)

        imageUriString = intent.getStringExtra("image_uri")?:""
        imageName = intent.getStringExtra("image_name") ?: ""

        sharedPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        email = sharedPrefs.getString(KEY_USERNAME, "") ?: ""

        if (email.isEmpty()) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupSpinner(R.id.spinner_color, listOf("Select a color", "Red", "Yellow", "White", "Pink", "Purple", "Blue"))
        setupSpinner(R.id.spinner_petals, listOf("Select petal count", "1-3", "4-6", "7-9", "10+"))
        setupSpinner(R.id.spinner_smell, listOf("Is it scented?", "Yes", "No"))
        setupSpinner(R.id.spinner_location, listOf("Select a location", "Park", "Campus", "Mountain", "Lakeside", "Garden"))

        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            if (!validateSelections()) return@setOnClickListener

            val data = FlowerDescription(
                imageName = imageName,
                description = mapOf(
                    "color" to getSpinnerValue(R.id.spinner_color),
                    "petals" to getSpinnerValue(R.id.spinner_petals),
                    "smell" to getSpinnerValue(R.id.spinner_smell),
                    "location" to getSpinnerValue(R.id.spinner_location)
                ),
                email = email
            )

            uploadJson(data)
            predict()
        }
    }

    private fun setupSpinner(id: Int, options: List<String>) {
        val spinner = findViewById<Spinner>(id)
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options) {
            override fun isEnabled(position: Int): Boolean = position != 0

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.WHITE)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }

    private fun validateSelections(): Boolean {
        val fields = mapOf(
            "color" to getSpinnerValue(R.id.spinner_color),
            "petals" to getSpinnerValue(R.id.spinner_petals),
            "smell" to getSpinnerValue(R.id.spinner_smell),
            "location" to getSpinnerValue(R.id.spinner_location)
        )
        for ((key, value) in fields) {
            if (value.startsWith("Select") || value.startsWith("Is it")) {
                Toast.makeText(this, "Please select a valid $key", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun getSpinnerValue(id: Int): String =
        findViewById<Spinner>(id).selectedItem.toString()

    private fun uploadJson(data: FlowerDescription) {
        Log.d(TAG, "Uploading JSON: $data")
        RetrofitClient.getInstance().getDescriptionApi().saveDescription(data)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Upload success: ${response.body()?.string()}")
                        Toast.makeText(this@TextInputActivity, "Saved!", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@TextInputActivity, ResultActivity::class.java))
//                        finish()
                    } else {
                        Log.e(TAG, "Upload failed. Response code: ${response.code()}")
                        Toast.makeText(this@TextInputActivity, "Save failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Upload error: ${t.message}", t)
                    Toast.makeText(this@TextInputActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String{
        var path = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null){
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            path = cursor.getString(columnIndex)
            cursor.close()
        }
        return path
    }

    private fun predict(){

        callExpert1()
    }

    /**
     * Call Expert1
     */
    private fun callExpert1() {
        imageUri = imageUriString?.let { Uri.parse(it) }

        val imageFile = imageUri?.let { File(getRealPathFromUri(this, it)) }

        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = imageFile.asRequestBody("image/*".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        RetrofitClient.getInstance().getImageMLAPI()
            .predict(imagePart)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body()?.string() ?: "")
                        Log.d("EXPERT1_RESULT", json.toString())

                        // 👇 After Expert1 finishes, call Expert2
                        callExpert2(json)
                    } else {
                        Toast.makeText(this@TextInputActivity, "Expert1 failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TextInputActivity, "Expert1 error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun callExpert2(expert1Result: JSONObject) {
        val color = getSpinnerValue(R.id.spinner_color)
        val petals = getSpinnerValue(R.id.spinner_petals)
        val smell = getSpinnerValue(R.id.spinner_smell)
        val location = getSpinnerValue(R.id.spinner_location)

        // 👇 match JSON keys from Postman exactly
        val expert2Input = mapOf(
            "color" to color,
            "petal" to petals,
            "smell" to smell,
            "location" to location
        )

        val body = JSONObject(expert2Input).toString()
            .toRequestBody("application/json".toMediaType())

        val a = JSONObject(expert2Input).toString()

        RetrofitClient.getInstance().getImageMLAPI()
            .predict2(body)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val expert2Json = JSONObject(response.body()?.string() ?: "")
                        Log.d("EXPERT2_RESULT", expert2Json.toString())

                        // Combine with expert1 result
                        resolveConflict(expert1Result, expert2Json)
                    } else {
                        Toast.makeText(this@TextInputActivity, "Expert2 failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TextInputActivity, "Expert2 error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun resolveConflict(expert1Result: JSONObject, expert2Result: JSONObject) {
        val combinedJson = JSONObject()

        combinedJson.put(
            "expert1",
            expert1Result
        )   // already like { "flower1": { name, confidence } }
        combinedJson.put(
            "expert2",
            expert2Result
        )   // already like { "flower1": ..., "flower2": ... }

        Log.d("CONFLICT_INPUT", combinedJson.toString(4)) // log the structure

        val body = combinedJson.toString()
            .toRequestBody("application/json".toMediaType())

        RetrofitClient.getInstance().getImageMLAPI()
            .resolveConflict(body)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val resultJson = JSONObject(response.body()?.string() ?: "")
                        Log.d("CONFLICT_RESULT", resultJson.toString(4))

                        // You could navigate to ResultActivity or update UI
                        startActivity(Intent(this@TextInputActivity, ResultActivity::class.java))
                        finish()
                    } else {
                        Log.e("CONFLICT_RESULT", "Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CONFLICT_RESULT", "Network error: ${t.message}")
                }
            })
    }
}
