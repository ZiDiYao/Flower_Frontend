package com.zidi.flowidentification_demo.model

data class FlowerDescriptionRequest(
    val email: String,  // make sure one to many, donot share the history with others
    val imageName: String,
    val description: Map<String, String>
)
