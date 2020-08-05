package com.promisebooks.app.model

data class AccounRequest(
    val amount: String,
    val client_ip: String,
    val currency: String,
    val device_fingerprint: String,
    val duration: Int,
    val email: String,
    val frequency: Int,
    val is_permanent: Boolean,
    val narration: String,
    val phone_number: String,
    val tx_ref: String
)