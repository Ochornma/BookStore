package com.promisebooks.app.model

data class USSDResponse(
    val `data`: DataX,
    val message: String,
    val meta: MetaXX,
    val status: String
)

data class MetaXX(
    val authorization: AuthorizationX
)


data class DataX(
    val account_id: Int,
    val amount: Int,
    val app_fee: Double,
    val auth_model: String,
    val charge_type: String,
    val charged_amount: Int,
    val created_at: String,
    val currency: String,
    val customer: CustomerX,
    val device_fingerprint: String,
    val flw_ref: String,
    val fraud_status: String,
    val id: Int,
    val ip: String,
    val merchant_fee: Int,
    val narration: String,
    val payment_code: String,
    val payment_type: String,
    val processor_response: String,
    val status: String,
    val tx_ref: String
)

data class AuthorizationX(
    val mode: String,
    val note: String
)

data class CustomerX(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val phone_number: String
)