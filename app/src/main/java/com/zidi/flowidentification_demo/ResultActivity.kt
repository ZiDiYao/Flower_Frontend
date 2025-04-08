package com.zidi.flowidentification_demo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val flowerName = intent.getStringExtra("flowerName") ?: "Unknown"
        val confidence = intent.getDoubleExtra("confidence", -1.0)
        val description = intent.getStringExtra("description") ?: "No description provided So Far"

        //  TextView
        val nameView = findViewById<TextView>(R.id.text_result_flower_name)
        val confidenceView = findViewById<TextView>(R.id.text_result_confidence)
        val descriptionView = findViewById<TextView>(R.id.text_result_description)

        nameView?.text = "🌸 The name of the flower：$flowerName"
        confidenceView?.text = if (confidence >= 0) "Confidence：${(confidence * 100).toInt()}%" else "Confidence：NONE"
        descriptionView?.text = "Introduction：$description"

        Toast.makeText(this, "Result has been loaded", Toast.LENGTH_SHORT).show()
    }
}