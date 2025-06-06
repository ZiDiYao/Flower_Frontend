package com.zidi.flowidentification_demo

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zidi.flowidentification_demo.network.RetrofitClient
import com.zidi.flowidentification_demo.model.FlowerDescriptionRequest as FlowerDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TextInputActivity : AppCompatActivity() {

    private lateinit var imageName: String
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var email: String

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_USERNAME = "username"
        private const val TAG = "UPLOAD_DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)

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
                        startActivity(Intent(this@TextInputActivity, ResultActivity::class.java))
                        finish()
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
}
