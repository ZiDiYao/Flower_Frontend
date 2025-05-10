package com.zidi.flowidentification_demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Get extras
        val flowerName = intent.getStringExtra("flowerName") ?: "Unknown"
        val confidence = intent.getDoubleExtra("confidence", -1.0)
        val imageUriString = intent.getStringExtra("imageUri")

        //  TextView
        val nameView = findViewById<TextView>(R.id.text_result_flower_name)
        val confidenceView = findViewById<TextView>(R.id.text_result_confidence)
        // val descriptionView = findViewById<TextView>(R.id.text_result_description)
        val imageView = findViewById<ImageView>(R.id.image_result_preview) // 👈 your ImageView


        nameView?.text = "🌸 The name of the flower：$flowerName"
        confidenceView?.text = if (confidence >= 0) "Confidence：${confidence}%" else "Confidence：NONE"
//        descriptionView?.text = "Introduction：$description"
        // Load image
        val imageUri = imageUriString?.let { Uri.parse(it) }
        if (imageUri != null) {
            imageView.setImageURI(imageUri) // ✅ working version
        }
        val backButton = findViewById<Button>(R.id.btn_back_to_dashboard)
        backButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        Toast.makeText(this, "Result has been loaded", Toast.LENGTH_SHORT).show()
    }
}