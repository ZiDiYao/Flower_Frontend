package com.zidi.flowidentification_demo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyText = findViewById<TextView>(R.id.history_text)
        historyText.text = "Your past flower identifications will appear here."
    }
}
