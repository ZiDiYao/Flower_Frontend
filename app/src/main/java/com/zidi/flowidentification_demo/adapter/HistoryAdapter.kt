package com.zidi.flowidentification_demo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zidi.flowidentification_demo.R
import com.zidi.flowidentification_demo.model.IdentificationResult

class HistoryAdapter(
    private val context: Context,
    private val historyList: MutableList<IdentificationResult>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val flowerName: TextView = itemView.findViewById(R.id.item_flower_name)
        val confidence: TextView = itemView.findViewById(R.id.item_confidence)
        val descriptionSummary: TextView = itemView.findViewById(R.id.item_description_summary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.flowerName.text = "🌸 ${item.flowerName}"
        holder.confidence.text = "Confidence: ${item.confidence}%"
        holder.descriptionSummary.text = "Color: ${item.color} · Petals: ${item.petals} · ${item.location}"

        Glide.with(context)
            .load("http://10.0.2.2:8080/uploads/${item.imageName}")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, com.zidi.flowidentification_demo.HistoryDetailActivity::class.java).apply {
                putExtra("flowerName", item.flowerName)
                putExtra("confidence", item.confidence)
                putExtra("color", item.color)
                putExtra("petals", item.petals)
                putExtra("smell", item.smell)
                putExtra("location", item.location)
                putExtra("imageName", item.imageName)
            }
            context.startActivity(intent)
        }
    }

    fun setData(newData: List<IdentificationResult>) {
        historyList.clear()
        historyList.addAll(newData)
        notifyDataSetChanged()
    }
}
