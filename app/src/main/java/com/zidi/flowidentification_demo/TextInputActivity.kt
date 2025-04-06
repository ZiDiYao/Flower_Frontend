package com.zidi.flowidentification_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zidi.flowidentification_demo.network.RetrofitClient
import com.zidi.flowidentification_demo.model.FlowerDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TextInputActivity : AppCompatActivity() {

    private lateinit var imageName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)

        imageName = intent.getStringExtra("image_name") ?: ""

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
                )
            )
            uploadJson(data)
        }
    }

    // Configures a spinner with given options and disables the first "hint" item
    private fun setupSpinner(id: Int, options: List<String>) {
        val spinner = findViewById<Spinner>(id)
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Disable the first item as a hint
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.setTextColor(if (position == 0) android.graphics.Color.GRAY else android.graphics.Color.WHITE)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
    }

    // Ensures the user has selected valid items for all dropdowns
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

    // Returns the selected value from a Spinner by its ID
    private fun getSpinnerValue(id: Int): String =
        findViewById<Spinner>(id).selectedItem.toString()

    // Extracts only the filename from a URI string
    private fun extractImageName(uriStr: String): String =
        uriStr.substringAfterLast("/")

    // Sends the form data (image name + description) as JSON to the server using Retrofit
    private fun uploadJson(data: FlowerDescription) {
        Log.d("UPLOAD_DEBUG", "Uploading JSON: $data")
        RetrofitClient.getInstance().getDescriptionApi().saveDescription(data)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.e("API_ERROR", "Error code: ${response.code()}, errorBody: ${response.errorBody()?.string()}")
                        Toast.makeText(this@TextInputActivity, "Saved!", Toast.LENGTH_SHORT).show()

                        // Navigate to result page after successful save
                        startActivity(Intent(this@TextInputActivity, ResultActivity::class.java))
                        finish()
                    } else {
                        // Server responded but failed to process request
                        Toast.makeText(this@TextInputActivity, "Save failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Network or other unexpected error
                    Toast.makeText(this@TextInputActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
