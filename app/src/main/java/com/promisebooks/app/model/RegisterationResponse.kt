package com.promisebooks.app.model

data class RegisterationResponse(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val link: String
)