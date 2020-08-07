package com.promisebooks.app.model

data class Banks(
    val `data`: List<Data1>,
    val message: String,
    val status: String
)

data class Data1(
    var code: String,
    val id: Int,
    var name: String
)