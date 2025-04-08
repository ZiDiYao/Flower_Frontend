package com.zidi.flowidentification_demo.model

data class HistoryItem(
    val imageName: String,
    val flowerName: String,
    val confidence: Int,
    val color: String,
    val petals: String,
    val smell: String,
    val location: String
)
