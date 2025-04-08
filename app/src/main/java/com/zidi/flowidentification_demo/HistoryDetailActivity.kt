package com.zidi.flowidentification_demo

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zidi.flowidentification_demo.R

class HistoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        val imageView: ImageView = findViewById(R.id.detail_image)
        val flowerNameText: TextView = findViewById(R.id.detail_flower_name)
        val confidenceText: TextView = findViewById(R.id.detail_confidence)
        val colorText: TextView = findViewById(R.id.detail_color)
        val petalsText: TextView = findViewById(R.id.detail_petals)
        val smellText: TextView = findViewById(R.id.detail_smell)
        val locationText: TextView = findViewById(R.id.detail_location)

        val flowerName = intent.getStringExtra("flowerName") ?: "Unknown"
        val confidence = intent.getIntExtra("confidence", -1)
        val color = intent.getStringExtra("color") ?: "-"
        val petals = intent.getStringExtra("petals") ?: "-"
        val smell = intent.getStringExtra("smell") ?: "-"
        val location = intent.getStringExtra("location") ?: "-"
        val imageName = intent.getStringExtra("imageName") ?: ""

        flowerNameText.text = "🌸 $flowerName"
        confidenceText.text = "Confidence: $confidence%"
        colorText.text = "Color: $color"
        petalsText.text = "Petals: $petals"
        smellText.text = "Smell: $smell"
        locationText.text = "Location: $location"

        Glide.with(this)
            .load("http://10.0.2.2:8080/uploads/$imageName")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)
    }
}
