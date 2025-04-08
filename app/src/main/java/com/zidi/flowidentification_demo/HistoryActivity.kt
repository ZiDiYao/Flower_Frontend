package com.zidi.flowidentification_demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zidi.flowidentification_demo.adapter.HistoryAdapter
import com.zidi.flowidentification_demo.model.IdentificationResult
import com.zidi.flowidentification_demo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_history)
        emptyText = findViewById(R.id.history_title)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter(this, mutableListOf())

        recyclerView.adapter = adapter

        loadHistory()
    }

    private fun loadHistory() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getLong("userId", -1L)

        if (userId == -1L) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance().resultApi.getHistory(userId.toString())
            .enqueue(object : Callback<List<IdentificationResult>> {
                override fun onResponse(
                    call: Call<List<IdentificationResult>>,
                    response: Response<List<IdentificationResult>>
                ) {
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        adapter.setData(response.body()!!)
                        emptyText.visibility = View.GONE
                    } else {
                        emptyText.text = "No history records found."
                        emptyText.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<IdentificationResult>>, t: Throwable) {
                    Toast.makeText(this@HistoryActivity, "Failed to load history", Toast.LENGTH_SHORT).show()
                    emptyText.text = "Error loading history."
                    emptyText.visibility = View.VISIBLE
                }
            })
    }

}
